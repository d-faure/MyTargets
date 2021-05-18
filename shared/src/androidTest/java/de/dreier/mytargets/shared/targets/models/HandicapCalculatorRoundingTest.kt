/*
 * MyTargets Project Copyright (C) 2018 Florian Dreier
 *
 * This file is (c) 2021 Jez McKinley - Canford Magna Bowmen
 *
 * Calculations used in this file are courtesy of Jack Atkinson - see:
 * https://www.jackatkinson.net/post/archery_handicap/
 * derived from David Lane's Handicap Calcs for Toxophilus 1979
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.shared.targets.models

import android.content.Context
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.HandicapCalculator
import de.dreier.mytargets.shared.models.Score
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Round
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.math.RoundingMode

@SmallTest
@RunWith(AndroidJUnit4::class)
class HandicapCalculatorRoundingTest {
    private lateinit var context: Context

    @Before
    @Throws(Exception::class)
    fun setUp() {
        SharedApplicationInstance.context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    fun assertBigDecimalEquals(expected: BigDecimal, actual: BigDecimal, scale: Int=2, roundingMode: RoundingMode=RoundingMode.HALF_UP) {
        assertEquals(expected.setScale(scale, roundingMode), actual.setScale(scale, roundingMode))
    }

    fun assertBigDecimalEquals(expected: String, actual: BigDecimal, scale: Int=2, roundingMode: RoundingMode=RoundingMode.HALF_UP) {
        assertEquals(BigDecimal(expected).setScale(scale, roundingMode), actual.setScale(scale, roundingMode))
    }

    fun assertBigDecimalEquals(expected: Int, actual: BigDecimal, scale: Int=2, roundingMode: RoundingMode=RoundingMode.HALF_UP) {
        assertEquals(BigDecimal(expected.toString()).setScale(scale, roundingMode), actual.setScale(scale, roundingMode))
    }

    private fun checkArraysEqual(expected: ArrayList<Int>, actual: List<BigDecimal>) {
        for ((index, item) in expected.withIndex()) {
            val actualItem = actual.get(index)
            val message = "Item at index: $index should be -- $item -- but was -- $actualItem --"
            assertEquals(message, BigDecimal(item.toString()), BigDecimal(actualItem.toString()))
        }
    }

    @Test
    fun check_distance_conversion_yards_to_metres_rounds_correctly() {
        var longDistance = Dimension(60f, Dimension.Unit.YARDS)
        var unit = HandicapCalculator()
        unit.setTargetDistance(longDistance)
        assertEquals(BigDecimal("54.86400"), unit.metricDistance)
    }

    @Test
    fun dimensionToString() {
        var unit = Dimension(22.5487f, Dimension.Unit.CENTIMETER)
        MatcherAssert.assertThat(unit.formatString(), CoreMatchers.equalTo("22.5487 cm"))

        unit = Dimension(22.54f, Dimension.Unit.INCH)
        MatcherAssert.assertThat(unit.formatString(), CoreMatchers.equalTo("22.54 in"))
    }

    @Test
    fun rounding_errors_test_imperial_round() {
        // YARDS, CENTIMETER,
        // first params here are targetface, scoringstyle and shotsperend, the tuples are distance, diameter and ends
        // WAFull.ID, 5, 6, 100, 122, 12, 80, 122, 8, 60, 122, 4
        var longDistance = Dimension(60f, Dimension.Unit.YARDS)
        var longDiameter = Dimension(122f, Dimension.Unit.CENTIMETER)
        var longTarget = Target(WAFull.ID, 5, longDiameter)
        var longScore = Score(219, 432, 72)
        var longRound = Round(0, 0, 0, 6, 12, longDistance, "60y", longTarget, longScore)

        val scoreList = arrayListOf<Int>(
                648, 648, 648, 648, 648, 648, 647, 647, 647, 646, 646, 645, 644, 642, 641, 639, 638, 635, 633, 630,
                628, 624, 621, 618, 614, 610, 606, 601, 597, 592, 587, 582, 576, 570, 564, 558, 551, 544, 537, 530,
                521, 513, 504, 495, 485, 475, 464, 453, 441, 428, 415, 401, 387, 372, 357, 341, 325, 308, 292, 275,
                258, 241, 224, 208, 192, 177, 162, 148, 134, 121, 109, 98, 88, 78, 69, 61, 54, 47, 42, 36, 32, 27,
                24, 20, 18, 15, 13, 11, 9, 8, 7, 6, 5, 4, 3, 3, 2, 2, 2, 1, 1
        )

        var arrowDiameter = Dimension(0.28125f, Dimension.Unit.INCH)
        var arrowRadius = BigDecimal((arrowDiameter.convertTo(Dimension.Unit.CENTIMETER).value / 2).toString()).setScale(3, RoundingMode.HALF_UP)
        assertEquals(arrowRadius, BigDecimal("0.357"))
        var unit = HandicapCalculator(longRound)

        assertEquals(0.000155355997, unit.handicapCoefficient(65), 0.0000000001)
        assertEquals(BigDecimal("1.467630639"), unit.dispersionFactor(65))
        assertEquals(BigDecimal("0.4504199138"), unit.angularDeviation(65))

        var calculated = unit.handicapScoresList()
        checkArraysEqual(scoreList, calculated)
    }

}
