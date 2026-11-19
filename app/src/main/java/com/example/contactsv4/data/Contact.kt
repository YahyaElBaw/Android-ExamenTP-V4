package com.example.contactsv4.data

import java.io.Serializable

/**
 * Entité Contact pour la Version 4 persistée en base SQLite.
 */
data class Contact(
    var id: Long = 0,
    var nom: String,
    var prenom: String,
    var telephone: String,
    var email: String? = null,
    var photoUri: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null
) : Serializable {

    val aCoordonneesValides: Boolean
        get() = latitude != null && longitude != null &&
                latitude!! in -90.0..90.0 && longitude!! in -180.0..180.0
}
