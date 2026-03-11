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

package de.dreier.mytargets.features.arrows

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.shared.models.db.Arrow

class ArrowListViewModel(app: Application) : AndroidViewModel(app) {

    private val arrowDAO: de.dreier.mytargets.base.db.dao.ArrowDAO
    val arrows: androidx.lifecycle.LiveData<List<Arrow>>

    init {
        ApplicationInstance.ensureDbInitialized(app.applicationContext)
        arrowDAO = ApplicationInstance.db.arrowDAO()
        arrows = arrowDAO.loadArrowsLive()
    }

    fun deleteArrow(item: Arrow): () -> Arrow {
        val images = arrowDAO.loadArrowImages(item.id)
        arrowDAO.deleteArrow(item)
        return {
            arrowDAO.saveArrow(item, images)
            item
        }
    }

}
