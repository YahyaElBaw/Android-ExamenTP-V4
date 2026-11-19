package com.example.contactsv4.data

import android.provider.BaseColumns

/**
 * Schéma et constantes de la base SQLite contacts.db.
 * Aucun nom de colonne n'est écrit en dur ailleurs dans le projet.
 */
object ContactContract {
    const val DATABASE_NAME = "contacts.db"
    const val DATABASE_VERSION = 1

    object ContactEntry : BaseColumns {
        const val TABLE_NAME = "contacts"
        const val COLUMN_ID = "_id"
        const val COLUMN_NOM = "nom"
        const val COLUMN_PRENOM = "prenom"
        const val COLUMN_TELEPHONE = "telephone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHOTO_URI = "photo_uri"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
    }
}
