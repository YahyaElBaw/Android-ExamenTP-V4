package com.example.contactsv4

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

/**
 * Fragment des réglages utilisateur basé sur AndroidX Preference.
 */
class ParametresFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
