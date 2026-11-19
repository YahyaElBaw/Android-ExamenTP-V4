package com.example.contactsv4

import com.example.contactsv4.data.Contact
import com.example.contactsv4.data.ContactContract
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitaires validant le schéma et les règles de gestion de Contacts V4.
 */
class ContactV4Test {

    @Test
    fun testConstantesContract() {
        assertEquals("contacts.db", ContactContract.DATABASE_NAME)
        assertEquals(1, ContactContract.DATABASE_VERSION)
        assertEquals("contacts", ContactContract.ContactEntry.TABLE_NAME)
        assertEquals("_id", ContactContract.ContactEntry.COLUMN_ID)
        assertEquals("nom", ContactContract.ContactEntry.COLUMN_NOM)
        assertEquals("telephone", ContactContract.ContactEntry.COLUMN_TELEPHONE)
    }

    @Test
    fun testNomAvecApostrophe() {
        val contact = Contact(
            id = 10,
            nom = "O'Neil",
            prenom = "Patrick",
            telephone = "0612345678"
        )
        assertEquals("O'Neil", contact.nom)
        assertTrue(contact.nom.contains("'"))
    }

    @Test
    fun testValidationCoordonneesGps() {
        val contactValide = Contact(
            nom = "Ben Ali",
            prenom = "Mohamed",
            telephone = "0611223344",
            latitude = 36.8,
            longitude = 10.1
        )
        assertTrue(contactValide.aCoordonneesValides)

        val contactInvalide = Contact(
            nom = "Ben Ali",
            prenom = "Mohamed",
            telephone = "0611223344",
            latitude = 95.0, // > 90
            longitude = 10.1
        )
        assertFalse(contactInvalide.aCoordonneesValides)
    }
}
