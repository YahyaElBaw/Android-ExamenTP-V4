package com.example.contactsv4

import android.content.Context
import android.graphics.Color
import androidx.preference.PreferenceManager

/**
 * Gestionnaire centralisé de lecture et conversion des préférences utilisateur.
 */
class PreferencesManager(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    companion object {
        const val KEY_TAILLE_POLICE = "taille_police"
        const val KEY_COULEUR_PREFEREE = "couleur_preferee"
        const val KEY_APPEL_DEPUIS_LISTE = "appel_depuis_liste"
    }

    val taillePoliceSp: Float
        get() {
            val str = prefs.getString(KEY_TAILLE_POLICE, "16") ?: "16"
            return str.toFloatOrNull() ?: 16f
        }

    val couleurPrefereeInt: Int
        get() {
            val str = prefs.getString(KEY_COULEUR_PREFEREE, "#000000") ?: "#000000"
            return try {
                Color.parseColor(str)
            } catch (e: Exception) {
                Color.BLACK
            }
        }

    val appelDepuisListe: Boolean
        get() = prefs.getBoolean(KEY_APPEL_DEPUIS_LISTE, false)
}
