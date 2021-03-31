package de.dreier.mytargets.features.settings.migrate.model

data class User (
    val email: String,
    val username: String,
    val password: String,
    val user_secret_key: String,
    val user_pk: String
)

data class AuthenticatedUser (
        val user_pk: Int,
        val username: String,
        val password: String,
        val user_secret_key: String
)
