/*
 * Copyright 2013 Mario Arias
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.funktionale.option

import org.funktionale.collections.prependTo
import org.funktionale.either.Disjunction
import org.funktionale.either.Either
import org.funktionale.either.Either.Left
import org.funktionale.either.Either.Right
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import org.funktionale.utils.GetterOperation
import org.funktionale.utils.GetterOperationImpl
import org.funktionale.utils.hashCodeForNullable
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 17/05/13
 * Time: 12:53
 */
sealed class Option<out T> {
    abstract fun isEmpty(): Boolean

    fun nonEmpty(): Boolean = isDefined()

    fun isDefined(): Boolean = !isEmpty()

    abstract fun get(): T

    fun orNull(): T? = if (isEmpty()) {
        null
    } else {
        get()
    }

    inline fun <R> map(f: (T) -> R): Option<R> = if (isEmpty()) {
        None
    } else {
        Some(f(get()))
    }

    inline fun <P1, R> map(p1: Option<P1>, f: (T, P1) -> R): Option<R> = if (isEmpty()) {
        None
    } else {
        p1.map { pp1 -> f(get(), pp1) }
    }

    inline fun <R> fold(ifEmpty: () -> R, f: (T) -> R): R = if (isEmpty()) {
        ifEmpty()
    } else {
        f(get())
    }

    inline fun <R> flatMap(f: (T) -> Option<R>): Option<R> = if (isEmpty()) {
        None
    } else {
        f(get())
    }

    inline fun filter(predicate: (T) -> Boolean): Option<T> = if (nonEmpty() && predicate(get())) {
        this
    } else {
        None
    }

    inline fun filterNot(predicate: (T) -> Boolean): Option<T> = if (nonEmpty() && !predicate(get())) {
        this
    } else {
        None
    }

    inline fun exists(predicate: (T) -> Boolean): Boolean = nonEmpty() && predicate(get())

    inline fun forEach(f: (T) -> Unit) {
        if (nonEmpty()) f(get())
    }


    fun toList(): List<T> = if (isEmpty()) {
        listOf()
    } else {
        listOf(get())
    }

    @Deprecated("Use toEitherRight", ReplaceWith("toEitherRight(left)"))
    inline fun <X> toRight(left: () -> X): Either<X, T> = toEitherRight(left)

    inline fun <X> toEitherRight(left: () -> X): Either<X, T> = if (isEmpty()) {
        Left(left())
    } else {
        Right(get())
    }

    inline fun <X> toDisjunctionRight(left: () -> X): Disjunction<X, T> = toEitherRight(left).toDisjunction()

    @Deprecated("use toEitherLeft", ReplaceWith("toEitherLeft(right)"))
    inline fun <X> toLeft(right: () -> X): Either<T, X> = toEitherLeft(right)

    inline fun <X> toEitherLeft(right: () -> X): Either<T, X> = if (isEmpty()) {
        Right(right())
    } else {
        Left(get())
    }

    inline fun <X> toDisjunctionLeft(right: () -> X): Disjunction<T, X> = toEitherLeft(right).toDisjunction()

    infix fun <X> and(value: Option<X>): Option<X> = if (isEmpty()) {
        None
    } else {
        value
    }

    object None : Option<Nothing>() {
        override fun get() = throw NoSuchElementException("None.get")

        override fun isEmpty() = true

        override fun equals(other: Any?): Boolean = when (other) {
            is None -> true
            else -> false
        }

        override fun hashCode(): Int = Integer.MAX_VALUE
    }

    class Some<out T>(val t: T) : Option<T>() {
        override fun get() = t

        override fun isEmpty() = false

        override fun equals(other: Any?): Boolean = when (other) {
            is Some<*> -> t == other.get()
            is None -> false
            else -> false
        }

        override fun hashCode(): Int = t.hashCodeForNullable(17) { a, b -> a + b }

        override fun toString(): String = "Some<$t>"
    }
}

fun <T> Option<T>.getOrElse(default: () -> T): T = if (isEmpty()) {
    default()
} else {
    get()
}

fun <T> Option<T>.orElse(alternative: () -> Option<T>): Option<T> = if (isEmpty()) {
    alternative()
} else {
    this
}

infix fun <T> Option<T>.or(value: Option<T>): Option<T> = if (isEmpty()) {
    value
} else {
    this
}

fun <T> T?.toOption(): Option<T> = if (this != null) {
    Some(this)
} else {
    None
}

inline fun <T> optionTry(body: () -> T): Option<T> = try {
    Some(body())
} catch(e: Exception) {
    None
}

val <K, V> Map<K, V>.option: GetterOperation<K, Option<V>>
    get () {
        return GetterOperationImpl { k -> this[k].toOption() }
    }


fun <T> Array<out T>.firstOption(): Option<T> {
    return firstOrNull().toOption()
}

fun BooleanArray.firstOption(): Option<Boolean> {
    return firstOrNull().toOption()
}

fun ByteArray.firstOption(): Option<Byte> {
    return firstOrNull().toOption()
}

fun CharArray.firstOption(): Option<Char> {
    return firstOrNull().toOption()
}

fun DoubleArray.firstOption(): Option<Double> {
    return firstOrNull().toOption()
}

fun FloatArray.firstOption(): Option<Float> {
    return firstOrNull().toOption()
}


fun IntArray.firstOption(): Option<Int> {
    return firstOrNull().toOption()
}


fun LongArray.firstOption(): Option<Long> {
    return firstOrNull().toOption()
}


fun ShortArray.firstOption(): Option<Short> {
    return firstOrNull().toOption()
}

fun <T> Iterable<T>.firstOption(): Option<T> {
    return firstOrNull().toOption()
}

fun <T> List<T>.firstOption(): Option<T> {
    return firstOrNull().toOption()
}

fun <T> Sequence<T?>.firstOption(): Option<T> {
    return firstOrNull().toOption()
}


fun String.firstOption(): Option<Char> {
    return firstOrNull().toOption()
}

fun <T> Array<out T>.firstOption(predicate: (T) -> Boolean): Option<T> {
    return firstOrNull(predicate).toOption()
}

inline fun BooleanArray.firstOption(predicate: (Boolean) -> Boolean): Option<Boolean> {
    return firstOrNull(predicate).toOption()
}

inline fun ByteArray.firstOption(predicate: (Byte) -> Boolean): Option<Byte> {
    return firstOrNull(predicate).toOption()
}

inline fun CharArray.firstOption(predicate: (Char) -> Boolean): Option<Char> {
    return firstOrNull(predicate).toOption()
}

inline fun DoubleArray.firstOption(predicate: (Double) -> Boolean): Option<Double> {
    return firstOrNull(predicate).toOption()
}

inline fun FloatArray.firstOption(predicate: (Float) -> Boolean): Option<Float> {
    return firstOrNull(predicate).toOption()
}

inline fun IntArray.firstOption(predicate: (Int) -> Boolean): Option<Int> {
    return firstOrNull(predicate).toOption()
}

inline fun LongArray.firstOption(predicate: (Long) -> Boolean): Option<Long> {
    return firstOrNull(predicate).toOption()
}

inline fun ShortArray.firstOption(predicate: (Short) -> Boolean): Option<Short> {
    return firstOrNull(predicate).toOption()
}

fun <T> Iterable<T>.firstOption(predicate: (T) -> Boolean): Option<T> {
    return firstOrNull(predicate).toOption()
}

fun <T> Sequence<T>.firstOption(predicate: (T) -> Boolean): Option<T> {
    return firstOrNull(predicate).toOption()
}

inline fun String.firstOption(predicate: (Char) -> Boolean): Option<Char> {
    return firstOrNull(predicate).toOption()
}

@Deprecated("Use optionTraverse", ReplaceWith("optionTraverse(f)"))
fun <T, R> List<T>.traverse(f: (T) -> Option<R>) = optionTraverse(f)

fun <T, R> List<T>.optionTraverse(f: (T) -> Option<R>): Option<List<R>> = foldRight(Some(emptyList())) { i: T, accumulator: Option<List<R>> ->
    f(i).map(accumulator) { head: R, tail: List<R> ->
        head prependTo tail
    }
}

@Deprecated("Use optionSequential", ReplaceWith("optionSequential()"))
fun <T> List<Option<T>>.sequential(): Option<List<T>> = optionSequential()

fun <T> List<Option<T>>.optionSequential(): Option<List<T>> = optionTraverse { it }

fun <T> List<Option<T>>.flatten(): List<T> {
    return filter { it.isDefined() }.map { it.get() }
}

fun <P1, R> Function1<P1, R>.optionLift(): (Option<P1>) -> Option<R> {
    return { it.map(this) }
}


