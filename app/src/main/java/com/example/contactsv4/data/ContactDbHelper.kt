package com.example.contactsv4.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Helper SQLite gérant la création, l'initialisation et la mise à niveau de la base contacts.db.
 * Implémenté sous forme de Singleton thread-safe avec applicationContext.
 */
class ContactDbHelper private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    ContactContract.DATABASE_NAME,
    null,
    ContactContract.DATABASE_VERSION
) {

    companion object {
        @Volatile
        private var INSTANCE: ContactDbHelper? = null

        fun getInstance(context: Context): ContactDbHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ContactDbHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val sqlCreateContactsTable = """
            CREATE TABLE ${ContactContract.ContactEntry.TABLE_NAME} (
                ${ContactContract.ContactEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${ContactContract.ContactEntry.COLUMN_NOM} TEXT NOT NULL,
                ${ContactContract.ContactEntry.COLUMN_PRENOM} TEXT NOT NULL,
                ${ContactContract.ContactEntry.COLUMN_TELEPHONE} TEXT NOT NULL,
                ${ContactContract.ContactEntry.COLUMN_EMAIL} TEXT,
                ${ContactContract.ContactEntry.COLUMN_PHOTO_URI} TEXT,
                ${ContactContract.ContactEntry.COLUMN_LATITUDE} REAL,
                ${ContactContract.ContactEntry.COLUMN_LONGITUDE} REAL
            );
        """.trimIndent()

        db.execSQL(sqlCreateContactsTable)

        // Insertion des 5 contacts de démonstration (dont 2 sans coordonnées GPS)
        insererContactsDemonstration(db)
    }

    private fun insererContactsDemonstration(db: SQLiteDatabase) {
        val contactsDemo = listOf(
            Contact(nom = "Dupont", prenom = "Claire", telephone = "06 12 34 56 78", email = "claire.dupont@example.com", latitude = 48.8566, longitude = 2.3522),
            Contact(nom = "Martin", prenom = "Thomas", telephone = "07 23 45 67 89", email = "thomas.martin@example.com", latitude = 36.8065, longitude = 10.1815),
            Contact(nom = "Bernard", prenom = "Sophie", telephone = "06 98 76 54 32", email = "sophie.bernard@example.com", latitude = null, longitude = null),
            Contact(nom = "Petit", prenom = "Lucas", telephone = "07 11 22 33 44", email = "lucas.petit@example.com", latitude = 43.6047, longitude = 1.4442),
            Contact(nom = "Moreau", prenom = "Julien", telephone = "06 55 44 33 22", email = "julien.moreau@example.com", latitude = null, longitude = null)
        )

        for (c in contactsDemo) {
            val values = ContentValues().apply {
                put(ContactContract.ContactEntry.COLUMN_NOM, c.nom)
                put(ContactContract.ContactEntry.COLUMN_PRENOM, c.prenom)
                put(ContactContract.ContactEntry.COLUMN_TELEPHONE, c.telephone)
                put(ContactContract.ContactEntry.COLUMN_EMAIL, c.email)
                put(ContactContract.ContactEntry.COLUMN_PHOTO_URI, c.photoUri)
                put(ContactContract.ContactEntry.COLUMN_LATITUDE, c.latitude)
                put(ContactContract.ContactEntry.COLUMN_LONGITUDE, c.longitude)
            }
            db.insert(ContactContract.ContactEntry.TABLE_NAME, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // En cas de changement de schéma
        db.execSQL("DROP TABLE IF EXISTS ${ContactContract.ContactEntry.TABLE_NAME}")
        onCreate(db)
    }
}
