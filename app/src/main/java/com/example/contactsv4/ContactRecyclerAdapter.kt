package com.example.contactsv4

import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsv4.data.Contact
import com.google.android.material.button.MaterialButton

/**
 * Adapter pour RecyclerView intégrant l'application dynamique des préférences utilisateur :
 * - Taille de police personnalisée
 * - Couleur du texte personnalisée
 * - Bouton d'appel direct sur la ligne activable/désactivable
 */
class ContactRecyclerAdapter(
    private var contacts: List<Contact>,
    private var taillePoliceSp: Float,
    private var couleurPrefereeInt: Int,
    private var appelDepuisListe: Boolean,
    private val onContactClick: (Contact) -> Unit,
    private val onCallClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactRecyclerAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val tvNomComplet: TextView = itemView.findViewById(R.id.tvNomComplet)
        val tvTelephone: TextView = itemView.findViewById(R.id.tvTelephone)
        val btnAppelLigne: MaterialButton = itemView.findViewById(R.id.btnAppelLigne)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact_v4, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.tvNomComplet.text = "${contact.nom} ${contact.prenom}"
        holder.tvTelephone.text = contact.telephone

        // Application dynamique des préférences
        holder.tvNomComplet.setTextSize(TypedValue.COMPLEX_UNIT_SP, taillePoliceSp)
        holder.tvNomComplet.setTextColor(couleurPrefereeInt)
        holder.tvTelephone.setTextSize(TypedValue.COMPLEX_UNIT_SP, maxOf(12f, taillePoliceSp - 2f))

        // Visibilité du bouton d'appel sur la ligne
        if (appelDepuisListe) {
            holder.btnAppelLigne.visibility = View.VISIBLE
            holder.btnAppelLigne.setOnClickListener {
                onCallClick(contact)
            }
        } else {
            holder.btnAppelLigne.visibility = View.GONE
        }

        if (!contact.photoUri.isNullOrEmpty()) {
            try {
                holder.ivAvatar.setImageURI(Uri.parse(contact.photoUri))
            } catch (e: Exception) {
                holder.ivAvatar.setImageResource(R.drawable.ic_person)
            }
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_person)
        }

        holder.itemView.setOnClickListener {
            onContactClick(contact)
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun mettreAJourDonnees(
        nouvelleListe: List<Contact>,
        tailleSp: Float,
        couleurInt: Int,
        appelActif: Boolean
    ) {
        this.contacts = nouvelleListe
        this.taillePoliceSp = tailleSp
        this.couleurPrefereeInt = couleurInt
        this.appelDepuisListe = appelActif
        notifyDataSetChanged()
    }
}
