/*
 * Copyright (C) 2018 Florian Dreier
 *
 * This file is part of MyTargets.
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

package de.dreier.mytargets.features.training.overview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.base.db.RoundRepository
import de.dreier.mytargets.base.db.TrainingRepository
import de.dreier.mytargets.shared.models.db.Training


class TrainingsViewModel(app: Application) : AndroidViewModel(app) {

    val trainings: LiveData<List<Training>>

    private val trainingDAO: de.dreier.mytargets.base.db.dao.TrainingDAO
    private val roundDAO: de.dreier.mytargets.base.db.dao.RoundDAO
    private val trainingRepository: TrainingRepository

    init {
        ApplicationInstance.ensureDbInitialized(app.applicationContext)
        val database = ApplicationInstance.db
        trainingDAO = database.trainingDAO()
        roundDAO = database.roundDAO()
        val roundRepository = RoundRepository(database)
        trainingRepository = TrainingRepository(
            database,
            trainingDAO,
            roundDAO,
            roundRepository,
            database.signatureDAO()
        )
        trainings = trainingDAO.loadTrainingsLive()
    }

    fun deleteTraining(item: Training): () -> Training {
        val training = trainingRepository.loadAugmentedTraining(item.id)
        trainingDAO.deleteTraining(item)
        return {
            trainingRepository.insertTraining(training)
            item
        }
    }

    fun getRoundIds(ids: List<Long>) = ids
        .map { trainingDAO.loadTraining(it) }
        .flatMap { t -> roundDAO.loadRounds(t.id) }
        .map { it.id }

    fun getAllRoundIds() = trainingDAO.loadTrainings()
        .flatMap { training -> roundDAO.loadRounds(training.id) }
        .map { it.id }

}
