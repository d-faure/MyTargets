package de.dreier.mytargets.features.settings.migrate.model

data class Post (
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)