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
package de.dreier.mytargets.shared.models

import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.targets.models.TargetModelBase
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.exp
import kotlin.math.pow

class HandicapCalculator {
    constructor(round: Round) {
        setArrowCount(round.score.shotCount)
        setTargetModel(round.target.model)
        setScoringStyle(round.target.getScoringStyle())
        setTargetSize(round.target.diameter)
        maxScore = round.score.totalPoints
        reachedScore = round.score.reachedPoints
        setTargetDistance(round.distance)
    }

    constructor()

    var reachedScore: Int = 0
        private set
    var maxScore: Int = 0
        private set
    var arrowCount: Int = 1
        private set
    lateinit var targetModel: TargetModelBase
        private set
    var targetSize: Dimension = Dimension.UNKNOWN
        private set
    lateinit var scoringStyle: ScoringStyle
        private set
    lateinit var targetDistance: Dimension
        private set
    lateinit var metricDistance: BigDecimal
        private set
    var arrowRadius: BigDecimal = BigDecimal("0.357")
        private set

    companion object {
        // Handicap range: 0 to 100 (inclusive)
        @JvmStatic
        fun handicapLowerBound(): Int = 0

        @JvmStatic
        fun handicapUpperBound(): Int = 100

        private const val HANDICAP_TABLE_BASE_COEFFICIENT = 1.91153596182896e-6
        private const val HANDICAP_COEFFICIENT_BASE = 1.07
        private const val GROUP_RADIUS_BASE = 1.036
        private const val GROUP_RADIUS_EXPONENT_OFFSET = 12.9
    }

    fun setTargetDistance(distanceDimension: Dimension) {
        targetDistance = distanceDimension
        // Using toString() avoids float->double precision noise and preserves expected scale (e.g. "70.0", "13.716").
        metricDistance = BigDecimal(distanceDimension.convertTo(Dimension.Unit.METER).value.toString())
    }

    fun setTargetModel(targetModel: TargetModelBase) {
        this.targetModel = targetModel
    }

    fun setScoringStyle(scoringStyle: ScoringStyle) {
        this.scoringStyle = scoringStyle
    }

    fun setTargetSize(targetSize: Dimension) {
        this.targetSize = targetModel.getRealSize(targetSize)
    }

    fun setArrowRadius(arrowRadius: BigDecimal) {
        this.arrowRadius = arrowRadius.setScale(4, RoundingMode.HALF_UP)
    }

    fun setArrowCount(arrowCount: Int) {
        this.arrowCount = arrowCount.coerceAtLeast(1)
    }

    fun handicapCoefficient(handicap: Int): Double {
        return HANDICAP_TABLE_BASE_COEFFICIENT * HANDICAP_COEFFICIENT_BASE.pow(handicap.toDouble())
    }

    fun dispersionFactor(handicap: Int): BigDecimal {
        val d = metricDistance.toDouble()
        val factor = 1.0 + handicapCoefficient(handicap) * d * d
        return BigDecimal.valueOf(factor)
    }

    fun angularDeviation(handicap: Int): BigDecimal {
        return BigDecimal.valueOf(
            (GROUP_RADIUS_BASE.pow(handicap.toDouble() + GROUP_RADIUS_EXPONENT_OFFSET)) *
                5.0 * (10.0.pow(-4.0)) * 180.0 / Math.PI
        )
    }

    fun groupRadius(handicap: Int): BigDecimal {
        // sigma=groupRadiusCm==100*distance_in_metres*(1.036^(handicap+12.9))*5*(10^-4)*Dispersion_Factor
        return BigDecimal("0.05") *
            metricDistance *
            BigDecimal.valueOf(GROUP_RADIUS_BASE.pow(handicap.toDouble() + GROUP_RADIUS_EXPONENT_OFFSET)) *
            dispersionFactor(handicap)
    }

    fun averageArrowScoreForHandicap(handicap: Int): BigDecimal {
        val groupRadiusSquared = groupRadius(handicap).pow(2)
        val zoneMap = targetModel.getZoneSizeMap(scoringStyle, targetSize)
        val bestArrowScore = BigDecimal(zoneMap.keys.maxOrNull().toString())

        val zoneScoreStep = (zoneMap.keys.elementAt(0) - zoneMap.keys.elementAt(1))
        return if (zoneScoreStep == 2) {
            imperialCalc(zoneMap, groupRadiusSquared, bestArrowScore, zoneScoreStep)
        } else {
            metricCalc(zoneMap, groupRadiusSquared, bestArrowScore)
        }
    }

    private fun metricCalc(
        zoneMap: Map<Int, BigDecimal>,
        groupRadiusSquared: BigDecimal,
        bestArrowScore: BigDecimal
    ): BigDecimal {
        var exponentTotals = BigDecimal.ZERO
        for ((_, radius) in zoneMap) {
            val zoneRadiusSquared = (radius + arrowRadius).pow(2)
            exponentTotals += BigDecimal.valueOf(exp(-(zoneRadiusSquared / groupRadiusSquared).toDouble()))
        }
        return (bestArrowScore - exponentTotals)
    }

    private fun imperialCalc(
        zoneMap: Map<Int, BigDecimal>,
        groupRadiusSquared: BigDecimal,
        bestArrowScore: BigDecimal,
        zoneScoreStep: Int
    ): BigDecimal {
        var exponentTotals = BigDecimal.ZERO
        val lastEntry = zoneMap.entries.last()
        for ((_, radius) in zoneMap) {
            val zoneRadiusSquared = (radius + arrowRadius).pow(2)
            val expTerm = BigDecimal.valueOf(exp(-(zoneRadiusSquared / groupRadiusSquared).toDouble()))
            exponentTotals = if (radius == lastEntry.value) {
                exponentTotals - expTerm
            } else {
                exponentTotals + BigDecimal.valueOf(zoneScoreStep.toDouble()) * expTerm
            }
        }
        return (bestArrowScore - exponentTotals)
    }

    fun handicapScoresList(rounded: Boolean = true): List<BigDecimal> {
        val decimalPlaces: Int = if (rounded) 0 else 2
        val handicapList = ArrayList<BigDecimal>()
        for (handicap: Int in handicapLowerBound()..handicapUpperBound()) {
            val average = averageArrowScoreForHandicap(handicap)
            val roundScore = (BigDecimal(arrowCount) * average)
            handicapList.add(roundScore.setScale(decimalPlaces, RoundingMode.HALF_UP))
        }
        return handicapList
    }

    fun getHandicapForScore(totalScore: Int): Int {
        val score = BigDecimal(totalScore.toString())
        val scoreList = handicapScoresList(true)
        for (handicap: Int in handicapLowerBound()..handicapUpperBound()) {
            if (score >= scoreList[handicap]) {
                return handicap
            }
        }
        return handicapUpperBound()
    }

    fun getHandicap(): Int {
        return getHandicapForScore(reachedScore)
    }

}
