package th.co.scb.sagapattern.base.utils

import java.util.function.Predicate

object BaseHelper {
    fun <T> List<T>.replaceIf(predicate: Predicate<in T>, replacement: T): List<T> {
        val result = this.toMutableList()
        result.replaceAll { t -> if (predicate.test(t)) replacement else t }
        return result
    }
}