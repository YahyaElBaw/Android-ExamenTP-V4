package com.example.contactsv4

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactsv4.databinding.FragmentListeContactsBinding

/**
 * Fragment principal affichant la liste des contacts issus de SQLite avec filtrage et CRUD.
 */
class ListeContactsFragment : Fragment() {

    private var _binding: FragmentListeContactsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels()
    private lateinit var adapter: ContactRecyclerAdapter
    private lateinit var prefsManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListeContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager(requireContext())

        adapter = ContactRecyclerAdapter(
            contacts = emptyList(),
            taillePoliceSp = prefsManager.taillePoliceSp,
            couleurPrefereeInt = prefsManager.couleurPrefereeInt,
            appelDepuisListe = prefsManager.appelDepuisListe,
            onContactClick = { contact ->
                // Clic sur la ligne : ouverture du détail par son id
                val detailFragment = DetailContactFragment.newInstance(contact.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onCallClick = { contact ->
                // Clic direct sur le bouton Appeler de la ligne sans ouvrir le détail
                if (contact.telephone.isNotBlank()) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.telephone}"))
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), R.string.msg_app_non_disponible, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvContacts.adapter = adapter

        // Observation des contacts chargés depuis SQLite
        viewModel.contacts.observe(viewLifecycleOwner) { liste ->
            adapter.mettreAJourDonnees(
                nouvelleListe = liste,
                tailleSp = prefsManager.taillePoliceSp,
                couleurInt = prefsManager.couleurPrefereeInt,
                appelActif = prefsManager.appelDepuisListe
            )
            binding.tvAucunContact.visibility = if (liste.isEmpty()) View.VISIBLE else View.GONE
        }

        // Filtrage en direct
        binding.etRecherche.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.rechercher(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Bouton flottant « + » : Création d'un contact (CRUD Create)
        binding.fabAjouter.setOnClickListener {
            val creationFragment = DetailContactFragment.newInstance(-1L)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, creationFragment)
                .addToBackStack(null)
                .commit()
        }

        // Bouton Paramètres : ouverture du fragment de préférences
        binding.btnParametres.setOnClickListener {
            val parametresFragment = ParametresFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, parametresFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        // Relecture immédiate des préférences lors du retour à la liste
        val listeActuelle = viewModel.contacts.value ?: emptyList()
        adapter.mettreAJourDonnees(
            nouvelleListe = listeActuelle,
            tailleSp = prefsManager.taillePoliceSp,
            couleurInt = prefsManager.couleurPrefereeInt,
            appelActif = prefsManager.appelDepuisListe
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
