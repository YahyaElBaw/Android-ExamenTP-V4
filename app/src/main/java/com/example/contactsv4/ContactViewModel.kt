package com.example.contactsv4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.contactsv4.data.Contact
import com.example.contactsv4.data.ContactDao
import com.example.contactsv4.data.ContactDbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel partagé gérant l'accès asynchrone aux données SQLite hors du thread principal.
 */
class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ContactDao(ContactDbHelper.getInstance(application))

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

    private var dernierTexteRecherche: String = ""

    init {
        chargerContacts()
    }

    fun chargerContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            val liste = if (dernierTexteRecherche.isEmpty()) {
                dao.getAll()
            } else {
                dao.search(dernierTexteRecherche)
            }
            withContext(Dispatchers.Main) {
                _contacts.value = liste
            }
        }
    }

    fun rechercher(texte: String) {
        dernierTexteRecherche = texte.trim()
        chargerContacts()
    }

    fun inserer(contact: Contact, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(contact)
            val updated = dao.getAll()
            withContext(Dispatchers.Main) {
                _contacts.value = updated
                onComplete()
            }
        }
    }

    fun mettreAJour(contact: Contact, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(contact)
            val updated = dao.getAll()
            withContext(Dispatchers.Main) {
                _contacts.value = updated
                onComplete()
            }
        }
    }

    fun supprimer(id: Long, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(id)
            val updated = dao.getAll()
            withContext(Dispatchers.Main) {
                _contacts.value = updated
                onComplete()
            }
        }
    }
}
