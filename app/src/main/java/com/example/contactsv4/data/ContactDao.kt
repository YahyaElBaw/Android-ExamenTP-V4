package com.example.contactsv4.data

import android.content.ContentValues
import android.database.Cursor
import com.example.contactsv4.data.ContactContract.ContactEntry

/**
 * Data Access Object (DAO) assurant l'ensemble des requêtes SQL sur contacts.db.
 * Sécurisé contre les injections SQL (aucun texte concaténé, usage de selectionArgs).
 */
class ContactDao(private val dbHelper: ContactDbHelper) {

    /**
     * Insère un nouveau contact et renvoie l'identifiant _id attribué.
     */
    fun insert(contact: Contact): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContactEntry.COLUMN_NOM, contact.nom)
            put(ContactEntry.COLUMN_PRENOM, contact.prenom)
            put(ContactEntry.COLUMN_TELEPHONE, contact.telephone)
            put(ContactEntry.COLUMN_EMAIL, contact.email)
            put(ContactEntry.COLUMN_PHOTO_URI, contact.photoUri)
            put(ContactEntry.COLUMN_LATITUDE, contact.latitude)
            put(ContactEntry.COLUMN_LONGITUDE, contact.longitude)
        }
        val id = db.insert(ContactEntry.TABLE_NAME, null, values)
        contact.id = id
        return id
    }

    /**
     * Met à jour un contact existant identifié par son id.
     */
    fun update(contact: Contact): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContactEntry.COLUMN_NOM, contact.nom)
            put(ContactEntry.COLUMN_PRENOM, contact.prenom)
            put(ContactEntry.COLUMN_TELEPHONE, contact.telephone)
            put(ContactEntry.COLUMN_EMAIL, contact.email)
            put(ContactEntry.COLUMN_PHOTO_URI, contact.photoUri)
            put(ContactEntry.COLUMN_LATITUDE, contact.latitude)
            put(ContactEntry.COLUMN_LONGITUDE, contact.longitude)
        }
        return db.update(
            ContactEntry.TABLE_NAME,
            values,
            "${ContactEntry.COLUMN_ID} = ?",
            arrayOf(contact.id.toString())
        )
    }

    /**
     * Supprime le contact d'identifiant id.
     */
    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            ContactEntry.TABLE_NAME,
            "${ContactEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    /**
     * Récupère tous les contacts triés par nom puis prénom, sans tenir compte de la casse.
     */
    fun getAll(): List<Contact> {
        val db = dbHelper.readableDatabase
        val orderBy = "${ContactEntry.COLUMN_NOM} COLLATE NOCASE ASC, ${ContactEntry.COLUMN_PRENOM} COLLATE NOCASE ASC"

        val contacts = mutableListOf<Contact>()
        db.query(
            ContactEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            orderBy
        ).use { cursor ->
            while (cursor.moveToNext()) {
                contacts.add(cursorToContact(cursor))
            }
        }
        return contacts
    }

    /**
     * Récupère un contact par son id unique.
     */
    fun getById(id: Long): Contact? {
        val db = dbHelper.readableDatabase
        return db.query(
            ContactEntry.TABLE_NAME,
            null,
            "${ContactEntry.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursorToContact(cursor)
            } else {
                null
            }
        }
    }

    /**
     * Recherche les contacts contenant le texte dans nom, prénom ou téléphone (insensible à la casse).
     */
    fun search(texte: String): List<Contact> {
        val query = "%${texte.trim()}%"
        val db = dbHelper.readableDatabase
        val selection = "${ContactEntry.COLUMN_NOM} LIKE ? OR ${ContactEntry.COLUMN_PRENOM} LIKE ? OR ${ContactEntry.COLUMN_TELEPHONE} LIKE ?"
        val selectionArgs = arrayOf(query, query, query)
        val orderBy = "${ContactEntry.COLUMN_NOM} COLLATE NOCASE ASC, ${ContactEntry.COLUMN_PRENOM} COLLATE NOCASE ASC"

        val resultats = mutableListOf<Contact>()
        db.query(
            ContactEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            orderBy
        ).use { cursor ->
            while (cursor.moveToNext()) {
                resultats.add(cursorToContact(cursor))
            }
        }
        return resultats
    }

    private fun cursorToContact(cursor: Cursor): Contact {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_ID))
        val nom = cursor.getString(cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_NOM))
        val prenom = cursor.getString(cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_PRENOM))
        val tel = cursor.getString(cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_TELEPHONE))

        val emailIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_EMAIL)
        val email = if (cursor.isNull(emailIndex)) null else cursor.getString(emailIndex)

        val photoIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_PHOTO_URI)
        val photo = if (cursor.isNull(photoIndex)) null else cursor.getString(photoIndex)

        val latIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_LATITUDE)
        val lat = if (cursor.isNull(latIndex)) null else cursor.getDouble(latIndex)

        val lonIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_LONGITUDE)
        val lon = if (cursor.isNull(lonIndex)) null else cursor.getDouble(lonIndex)

        return Contact(
            id = id,
            nom = nom,
            prenom = prenom,
            telephone = tel,
            email = email,
            photoUri = photo,
            latitude = lat,
            longitude = lon
        )
    }
}
