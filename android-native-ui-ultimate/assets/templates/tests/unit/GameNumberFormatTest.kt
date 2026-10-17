package com.example.game.ui.template

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

/*
 * TEMPLATE — JVM unit test (src/test/...). Pure Kotlin, no device needed.
 * Locale is fixed to US so separators are predictable; add locale-specific cases if the app supports them.
 */
class GameNumberFormatTest {

    private val us = Locale.US

    @Test
    fun smallNumbersAreGroupedAndUnscaled() {
        assertEquals("0", GameNumberFormat.compact(0, us))
        assertEquals("999", GameNumberFormat.compact(999, us))
        assertEquals("9,999", GameNumberFormat.compact(9_999, us))
    }

    @Test
    fun thousandsUseK() {
        assertEquals("10K", GameNumberFormat.compact(10_000, us))
        assertEquals("12.3K", GameNumberFormat.compact(12_345, us))
    }

    @Test
    fun millionsAndBillionsUseMAndB() {
        assertEquals("1.2M", GameNumberFormat.compact(1_250_000, us))
        assertEquals("3.4B", GameNumberFormat.compact(3_400_000_000L, us))
    }

    @Test
    fun roundsDownSoValuesAreNeverOverstated() {
        assertEquals("1.9M", GameNumberFormat.compact(1_999_999, us))
    }
}
