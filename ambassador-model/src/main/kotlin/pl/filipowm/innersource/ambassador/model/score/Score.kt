package pl.filipowm.innersource.ambassador.model.score

import pl.filipowm.innersource.ambassador.model.Source
import io.vavr.control.Either
import io.vavr.control.Try
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.function.Supplier


const val MAX_CONFIDENCE = 10


sealed class AggregatedScore<T> : Score<MutableMap<Source, Score<T>>>(Source.BASICS, mutableMapOf(), MAX_CONFIDENCE) {

    fun add(score: Score<T>) : AggregatedScore<T> {
        this.data[score.source] = score
        return this
    }
}

sealed class Score<T>(val source: Source, val data: T, val confidence: Int) {


    class Pass<T>(source: Source, data: T, confidence: Int) : Score<T>(source, data, confidence) {
        fun unless(predicate: Predicate<T>): Either<Pass<T>, Fail> {
            return unless(predicate, { RuntimeException() })
        }

        fun unless(predicate: Predicate<T>, throwableSupplier: Supplier<Throwable>): Either<Pass<T>, Fail> {
            if (predicate.test(data)) {
                return Either.left(this)
            }
            return Either.right(this.toFail(throwableSupplier.get()))
        }

        fun unlessThrown(c: Consumer<T>): Either<Pass<T>, Fail> {
            return try {
                c.accept(data)
                Either.left(this)
            } catch (exc: RuntimeException) {
                Either.right(this.toFail(exc))
            }
        }

        fun toFail(cause: Throwable): Fail {
            return fail(source, cause, confidence)
        }
    }

    class Fail(source: Source, cause: Throwable, confidence: Int) : Score<Throwable>(source, cause, confidence) {
        @Throws(Throwable::class)
        fun rethrow() {
            throw this.data
        }
    }

    companion object Factory {
        fun <T> from(source: Source, supplier: Supplier<T>): Either<Pass<T>, Fail> {
            return Try.ofCallable { supplier.get() }
                    .map { data -> pass(source, data, MAX_CONFIDENCE) }
                    .toEither()
                    .mapLeft { fail(source, it, MAX_CONFIDENCE) }
                    .swap()
        }

        fun <T> pass(data: T): Pass<T> {
            return pass(data, MAX_CONFIDENCE)
        }

        fun <T> pass(data: T, confidence: Int): Pass<T> {
            return pass(Source.BASICS, data, confidence)
        }

        fun <T> pass(source: Source, data: T, confidence: Int): Pass<T> {
            return Pass(source, data, confidence)
        }

        fun fail(cause: Throwable): Fail {
            return fail(cause, MAX_CONFIDENCE)
        }

        fun fail(cause: Throwable, confidence: Int): Fail {
            return fail(Source.BASICS, cause, confidence)
        }

        fun fail(source: Source, cause: Throwable, confidence: Int): Fail {
            return Fail(source, cause, confidence)
        }
    }
}