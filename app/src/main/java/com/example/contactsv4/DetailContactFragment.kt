package com.example.contactsv4

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.example.contactsv4.data.Contact
import com.example.contactsv4.databinding.FragmentDetailContactBinding
import java.util.Locale

/**
 * Fragment de consultation, modification, création et suppression (CRUD) d'un contact SQLite.
 */
class DetailContactFragment : Fragment() {

    private var _binding: FragmentDetailContactBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels()
    private var contactId: Long = -1L
    private var contactActuel: Contact? = null
    private var photoSelectionneeUri: String? = null

    private val selectPictureLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoSelectionneeUri = it.toString()
            binding.ivDetailAvatar.setImageURI(it)
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            binding.ivDetailAvatar.setImageBitmap(it)
            photoSelectionneeUri = "captured_bitmap"
        }
    }

    companion object {
        private const val ARG_CONTACT_ID = "arg_contact_id"

        fun newInstance(id: Long = -1L): DetailContactFragment {
            val fragment = DetailContactFragment()
            val args = Bundle().apply {
                putLong(ARG_CONTACT_ID, id)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactId = arguments?.getLong(ARG_CONTACT_ID, -1L) ?: -1L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isModeCreation = (contactId == -1L)

        if (isModeCreation) {
            binding.tvTitreDetail.text = getString(R.string.titre_creation)
            binding.btnSupprimer.visibility = View.GONE
            binding.btnSupprimerEntete.visibility = View.GONE
        } else {
            binding.tvTitreDetail.text = getString(R.string.titre_detail)
            binding.btnSupprimer.visibility = View.VISIBLE
            binding.btnSupprimerEntete.visibility = View.VISIBLE

            // Recherche du contact dans le ViewModel
            contactActuel = viewModel.contacts.value?.find { it.id == contactId }
            contactActuel?.let { c ->
                binding.etNom.setText(c.nom)
                binding.etPrenom.setText(c.prenom)
                binding.etTelephone.setText(c.telephone)
                binding.etEmail.setText(c.email.orEmpty())
                binding.etLatitude.setText(c.latitude?.toString() ?: "")
                binding.etLongitude.setText(c.longitude?.toString() ?: "")
                photoSelectionneeUri = c.photoUri

                if (!c.photoUri.isNullOrEmpty()) {
                    try {
                        binding.ivDetailAvatar.setImageURI(Uri.parse(c.photoUri))
                    } catch (e: Exception) {
                        binding.ivDetailAvatar.setImageResource(R.drawable.ic_person)
                    }
                }
            }
        }

        binding.btnRetour.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnAnnuler.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Action Appel
        binding.btnAppeler.setOnClickListener {
            val tel = binding.etTelephone.text?.toString()?.trim().orEmpty()
            if (tel.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), R.string.msg_app_non_disponible, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Action Email
        binding.btnEmail.setOnClickListener {
            val mail = binding.etEmail.text?.toString()?.trim().orEmpty()
            if (mail.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$mail"))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), R.string.msg_app_non_disponible, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnChoisirPhoto.setOnClickListener {
            selectPictureLauncher.launch("image/*")
        }

        binding.btnPrendrePhoto.setOnClickListener {
            try {
                takePictureLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Caméra non disponible", Toast.LENGTH_SHORT).show()
            }
        }

        // Action Voir / Choisir sur la Carte
        binding.btnVoirCarte.setOnClickListener {
            val latStr = binding.etLatitude.text?.toString()?.trim().orEmpty()
            val lonStr = binding.etLongitude.text?.toString()?.trim().orEmpty()

            val lat = latStr.toDoubleOrNull() ?: 36.8065
            val lon = lonStr.toDoubleOrNull() ?: 10.1815

            val nomComplet = "${binding.etNom.text} ${binding.etPrenom.text}".trim().ifEmpty { "Nouveau contact" }
            val carteFragment = CarteContactFragment.newInstance(contactId, lat, lon, nomComplet)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, carteFragment)
                .addToBackStack(null)
                .commit()
        }

        // Récupération de la position choisie interactivement sur la carte
        setFragmentResultListener(CarteContactFragment.REQUEST_KEY_LOCATION) { _, bundle ->
            val selectedLat = bundle.getDouble(CarteContactFragment.RESULT_LAT)
            val selectedLon = bundle.getDouble(CarteContactFragment.RESULT_LON)
            binding.etLatitude.setText(String.format(Locale.US, "%.5f", selectedLat))
            binding.etLongitude.setText(String.format(Locale.US, "%.5f", selectedLon))
            Toast.makeText(
                requireContext(),
                "Position sélectionnée : %.4f, %.4f".format(Locale.US, selectedLat, selectedLon),
                Toast.LENGTH_SHORT
            ).show()
        }

        // Action Supprimer avec AlertDialog (CRUD Delete)
        val actionSuppression = View.OnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_suppression_titre)
                .setMessage(R.string.dialog_suppression_msg)
                .setPositiveButton(R.string.dialog_oui) { _, _ ->
                    viewModel.supprimer(contactId) {
                        Toast.makeText(requireContext(), R.string.msg_contact_supprime, Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                }
                .setNegativeButton(R.string.dialog_non, null)
                .show()
        }

        binding.btnSupprimer.setOnClickListener(actionSuppression)
        binding.btnSupprimerEntete.setOnClickListener(actionSuppression)

        // Action Enregistrer (CRUD Create / Update)
        binding.btnEnregistrer.setOnClickListener {
            enregistrerContact(isModeCreation)
        }
    }

    private fun enregistrerContact(isCreation: Boolean) {
        val nom = binding.etNom.text?.toString()?.trim().orEmpty()
        val prenom = binding.etPrenom.text?.toString()?.trim().orEmpty()
        val tel = binding.etTelephone.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim()
        val lat = binding.etLatitude.text?.toString()?.trim()?.toDoubleOrNull()
        val lon = binding.etLongitude.text?.toString()?.trim()?.toDoubleOrNull()

        if (nom.isEmpty() || prenom.isEmpty() || tel.isEmpty()) {
            Toast.makeText(requireContext(), R.string.erreur_champs_obligatoires, Toast.LENGTH_LONG).show()
            return
        }

        if ((lat != null && lat !in -90.0..90.0) || (lon != null && lon !in -180.0..180.0)) {
            Toast.makeText(requireContext(), R.string.erreur_coordonnees_invalides, Toast.LENGTH_LONG).show()
            return
        }

        val contact = Contact(
            id = if (isCreation) 0L else contactId,
            nom = nom,
            prenom = prenom,
            telephone = tel,
            email = if (email.isNullOrEmpty()) null else email,
            photoUri = photoSelectionneeUri,
            latitude = lat,
            longitude = lon
        )

        if (isCreation) {
            viewModel.inserer(contact) {
                Toast.makeText(requireContext(), R.string.msg_contact_enregistre, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        } else {
            viewModel.mettreAJour(contact) {
                Toast.makeText(requireContext(), R.string.msg_contact_enregistre, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
