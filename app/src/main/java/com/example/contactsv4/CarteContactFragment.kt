package com.example.contactsv4

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.contactsv4.databinding.FragmentCarteContactBinding
import java.util.Locale

/**
 * Fragment cartographique affichant les coordonnées GPS enregistrées en base SQLite.
 * Utilise les tuiles CARTO Voyager / OpenStreetMap (qui ne bloquent pas avec l'erreur 418),
 * permet la sélection interactive d'un lieu au clic ou au glissement de repère,
 * et propose l'ouverture directe dans l'application Google Maps native.
 */
class CarteContactFragment : Fragment() {

    private var _binding: FragmentCarteContactBinding? = null
    private val binding get() = _binding!!

    private var contactId: Long = -1L
    private var initialLatitude: Double = 0.0
    private var initialLongitude: Double = 0.0
    private var contactNom: String = ""

    // Coordonnées sélectionnées en direct par l'utilisateur sur la carte
    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_LAT = "arg_lat"
        private const val ARG_LON = "arg_lon"
        private const val ARG_NOM = "arg_nom"

        const val REQUEST_KEY_LOCATION = "request_key_location"
        const val RESULT_LAT = "selected_latitude"
        const val RESULT_LON = "selected_longitude"

        fun newInstance(id: Long, lat: Double, lon: Double, nom: String): CarteContactFragment {
            val fragment = CarteContactFragment()
            val args = Bundle().apply {
                putLong(ARG_ID, id)
                putDouble(ARG_LAT, lat)
                putDouble(ARG_LON, lon)
                putString(ARG_NOM, nom)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactId = it.getLong(ARG_ID)
            initialLatitude = it.getDouble(ARG_LAT)
            initialLongitude = it.getDouble(ARG_LON)
            contactNom = it.getString(ARG_NOM).orEmpty()
        }
        selectedLatitude = initialLatitude
        selectedLongitude = initialLongitude
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarteContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCoordonneesLabel(selectedLatitude, selectedLongitude)

        binding.btnRetourCarte.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Configuration de la carte réelle interactive avec sélection de lieu
        setupRealMap()

        // Bouton pour valider et renvoyer la nouvelle position sélectionnée au formulaire
        binding.btnValiderPosition.setOnClickListener {
            val resultBundle = Bundle().apply {
                putDouble(RESULT_LAT, selectedLatitude)
                putDouble(RESULT_LON, selectedLongitude)
            }
            setFragmentResult(REQUEST_KEY_LOCATION, resultBundle)
            Toast.makeText(
                requireContext(),
                "Position sélectionnée : %.4f, %.4f".format(Locale.US, selectedLatitude, selectedLongitude),
                Toast.LENGTH_SHORT
            ).show()
            parentFragmentManager.popBackStack()
        }

        // Bouton permettant d'ouvrir la vraie application Google Maps installée sur le téléphone
        binding.btnOuvrirGoogleMaps.setOnClickListener {
            ouvrirDansGoogleMapsApp()
        }
    }

    private fun updateCoordonneesLabel(lat: Double, lon: Double) {
        binding.tvCoordonneesDetails.text = String.format(Locale.US, "Latitude : %.5f | Longitude : %.5f", lat, lon)
    }

    inner class MapWebAppInterface {
        @JavascriptInterface
        fun onLocationSelected(lat: Double, lon: Double) {
            activity?.runOnUiThread {
                selectedLatitude = lat
                selectedLongitude = lon
                updateCoordonneesLabel(lat, lon)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupRealMap() {
        val webView = binding.mapWebView
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        // Définir un User-Agent clair pour respecter les politiques d'utilisation des serveurs de tuiles
        webSettings.userAgentString = "ContactsManagerApp/4.0 (Linux; Android " + Build.VERSION.RELEASE + "; Mobile)"

        webView.addJavascriptInterface(MapWebAppInterface(), "AndroidInterface")
        webView.webViewClient = WebViewClient()

        val escapedName = contactNom.replace("'", "\\'").replace("\"", "\\\"")

        // Tuiles CARTO Voyager : Gratuites, haute résolution, cartes réelles mondiales avec noms de rues et sans blocage 418
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                <style>
                    html, body, #map {
                        height: 100%;
                        width: 100%;
                        margin: 0;
                        padding: 0;
                        background-color: #E2E8F0;
                    }
                    .leaflet-popup-content-wrapper {
                        border-radius: 8px;
                        font-family: 'Segoe UI', Roboto, sans-serif;
                    }
                    .popup-title {
                        font-size: 15px;
                        font-weight: bold;
                        color: #1E3A8A;
                    }
                    .popup-coords {
                        font-size: 12px;
                        color: #475569;
                        margin-top: 4px;
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    var lat = $initialLatitude;
                    var lon = $initialLongitude;
                    var nom = "$escapedName";

                    var map = L.map('map', {
                        zoomControl: true,
                        attributionControl: false
                    }).setView([lat, lon], 14);

                    // Vraies tuiles Google Maps (Plan & Satellite) et ESRI — 100% sans clé API, sans filigrane et sans blocage
                    var googleStreets = L.tileLayer('https://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}', {
                        maxZoom: 20,
                        subdomains: ['mt0', 'mt1', 'mt2', 'mt3']
                    });

                    var googleHybrid = L.tileLayer('https://{s}.google.com/vt/lyrs=y&x={x}&y={y}&z={z}', {
                        maxZoom: 20,
                        subdomains: ['mt0', 'mt1', 'mt2', 'mt3']
                    });

                    var esriStreets = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
                        maxZoom: 19
                    });

                    // Activer le plan Google Maps par défaut
                    googleStreets.addTo(map);

                    // Sélecteur de couches : permet de basculer entre Plan, Satellite et ESRI
                    L.control.layers({
                        "🗺️ Google Plan": googleStreets,
                        "🛰️ Satellite": googleHybrid,
                        "🏙️ ESRI": esriStreets
                    }, null, { position: 'topright' }).addTo(map);

                    // Marqueur déplaçable (draggable)
                    var marker = L.marker([lat, lon], { draggable: true }).addTo(map);
                    marker.bindPopup("<div class='popup-title'>" + nom + "</div><div class='popup-coords'>GPS: " + lat.toFixed(5) + ", " + lon.toFixed(5) + "</div>").openPopup();

                    function updatePosition(newLat, newLon, title) {
                        lat = newLat;
                        lon = newLon;
                        marker.setLatLng([lat, lon]);
                        marker.bindPopup("<div class='popup-title'>" + nom + "</div><div class='popup-coords'>" + title + "<br>Lat: " + lat.toFixed(5) + "<br>Lon: " + lon.toFixed(5) + "</div>").openPopup();
                        if (window.AndroidInterface) {
                            window.AndroidInterface.onLocationSelected(lat, lon);
                        }
                    }

                    // Clic n'importe où sur la carte pour choisir un nouveau lieu
                    map.on('click', function(e) {
                        updatePosition(e.latlng.lat, e.latlng.lng, "Position sélectionnée");
                    });

                    // Glisser-déposer du repère
                    marker.on('dragend', function(e) {
                        var pos = marker.getLatLng();
                        updatePosition(pos.lat, pos.lng, "Position déplacée");
                    });
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL("https://google.com", htmlContent, "text/html", "UTF-8", null)
    }

    private fun ouvrirDansGoogleMapsApp() {
        val label = Uri.encode(contactNom)
        val geoUri = Uri.parse("geo:$selectedLatitude,$selectedLongitude?q=$selectedLatitude,$selectedLongitude($label)")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(mapIntent)
        } catch (e: Exception) {
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$selectedLatitude,$selectedLongitude")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            try {
                startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(requireContext(), "Impossible d'ouvrir Google Maps", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.mapWebView?.destroy()
        _binding = null
    }
}
