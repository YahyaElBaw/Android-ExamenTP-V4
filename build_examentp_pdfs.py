import os
import base64
from playwright.sync_api import sync_playwright

def get_b64(path):
    with open(path, "rb") as f:
        return f"data:image/png;base64,{base64.b64encode(f.read()).decode('utf-8')}"

def generate_pdf(html_content, output_path):
    with sync_playwright() as p:
        browser = p.chromium.launch()
        page = browser.new_page()
        page.set_content(html_content)
        page.pdf(
            path=output_path,
            format="A4",
            print_background=True,
            margin={"top": "15mm", "bottom": "15mm", "left": "15mm", "right": "15mm"}
        )
        browser.close()
    print(f"PDF generated: {output_path}")

BASE_CSS = """
    @page {
        size: A4;
        margin: 15mm;
        @bottom-right {
            content: counter(page);
        }
    }
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Segoe UI', Helvetica, Arial, sans-serif; }
    body { color: #1E293B; line-height: 1.6; font-size: 13px; background: white; }
    h1 { color: #0F172A; font-size: 24px; margin-bottom: 8px; border-bottom: 2px solid #3B82F6; padding-bottom: 6px; }
    h2 { color: #1D4ED8; font-size: 17px; margin-top: 18px; margin-bottom: 8px; border-bottom: 1px solid #E2E8F0; padding-bottom: 4px; }
    h3 { color: #334155; font-size: 14px; margin-top: 12px; margin-bottom: 4px; }
    p { margin-bottom: 8px; text-align: justify; }
    ul, ol { margin-left: 20px; margin-bottom: 10px; }
    li { margin-bottom: 4px; }
    .badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-weight: bold; font-size: 11px; }
    .badge-green { background: #DCFCE7; color: #166534; }
    .badge-blue { background: #DBEAFE; color: #1E40AF; }
    .badge-red { background: #FEE2E2; color: #991B1B; }
    table { width: 100%; border-collapse: collapse; margin: 12px 0; font-size: 12px; }
    th, td { border: 1px solid #CBD5E1; padding: 6px 10px; text-align: left; }
    th { background: #F1F5F9; color: #0F172A; font-weight: 600; }
    tr:nth-child(even) { background: #F8FAFC; }
    code { font-family: 'Consolas', monospace; background: #F1F5F9; color: #2563EB; padding: 2px 5px; border-radius: 4px; font-size: 12px; }
    pre { background: #0F172A; color: #F8FAFC; padding: 12px; border-radius: 6px; overflow-x: auto; font-size: 11px; line-height: 1.4; margin: 10px 0; font-family: 'Consolas', monospace; }
    .img-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; margin: 15px 0; }
    .img-card { background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 8px; padding: 10px; text-align: center; }
    .img-card img { width: 100%; max-height: 270px; object-fit: contain; border-radius: 6px; }
    .img-caption { font-size: 11px; font-weight: 600; color: #475569; margin-top: 6px; }
    .header-box { background: linear-gradient(135deg, #0F172A, #1E293B); color: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
    .header-box h1 { color: white; border: none; font-size: 26px; }
    .header-meta { display: flex; gap: 20px; margin-top: 10px; font-size: 12px; opacity: 0.9; }
    .note-box { background: #EFF6FF; border-left: 4px solid #3B82F6; padding: 10px 14px; border-radius: 4px; margin: 10px 0; }
    .page-break { page-break-before: always; }
"""

def create_v4_compte_rendu():
    base_dir = r"D:/L3MDW/Mobile/ExamenTP/screenshots"
    img1 = get_b64(f"{base_dir}/v4_01_liste_initiale_sqlite.png")
    img2 = get_b64(f"{base_dir}/v4_02_ecran_parametres.png")
    img3 = get_b64(f"{base_dir}/v4_03_preferences_appliquees.png")
    img4 = get_b64(f"{base_dir}/v4_04_confirmation_suppression.png")

    html = f"""
    <!DOCTYPE html>
    <html>
    <head><meta charset="utf-8"><style>{BASE_CSS}</style></head>
    <body>
        <div class="header-box">
            <h1>Compte Rendu Examen TP — Contacts Version 4</h1>
            <p>Application Android Native en Kotlin : Persistance SQLite native, Préférences SharedPreferences et CRUD complet</p>
            <div class="header-meta">
                <span><strong>Examen :</strong> Contacts V4</span>
                <span><strong>Niveau :</strong> L3 MDW</span>
                <span><strong>Technologies :</strong> SQLiteOpenHelper, DAO, Coroutines IO, PreferenceFragmentCompat</span>
                <span><strong>Statut :</strong> 100% Validé</span>
            </div>
        </div>

        <h2>1. Présentation du Travail Réalisé</h2>
        <p>L'application <strong>Contacts V4</strong> représente l'aboutissement de la suite de projets mobiles. Elle introduit la persistance permanente locale au travers d'une base de données SQLite native (<code>contacts.db</code>) et un système d'écran de préférences utilisateur hautement configurable :</p>
        <ul>
            <li><strong>Base SQLite native :</strong> Conception conforme avec <code>ContactContract</code>, <code>ContactDbHelper</code> (Singleton) et <code>ContactDao</code>.</li>
            <li><strong>CRUD Persistant :</strong> Création via bouton flottant FAB « + », lecture triée par nom (COLLATE NOCASE), modification et suppression avec confirmation par <code>AlertDialog</code>.</li>
            <li><strong>Sécurité et Performance :</strong> Requêtes paramétrées (<code>selectionArgs</code>) protégeant contre les apostrophes et injections, exécution asynchrone hors du thread principal via Kotlin Coroutines (<code>Dispatchers.IO</code>).</li>
            <li><strong>Préférences utilisateur :</strong> Réglage de la taille de police, de la couleur de texte et activation optionnelle d'un bouton d'appel direct par ligne dans la RecyclerView.</li>
        </ul>

        <h2>2. Galerie des Captures d'Écran de l'Application</h2>
        <div class="img-grid">
            <div class="img-card">
                <img src="{img1}" alt="Liste initiale SQLite" />
                <div class="img-caption">Vue 1 : Liste initiale chargée depuis SQLite (Tri alphabétique)</div>
            </div>
            <div class="img-card">
                <img src="{img2}" alt="Écran Paramètres" />
                <div class="img-caption">Vue 2 : Écran des Préférences (Taille, Couleur, Appel)</div>
            </div>
        </div>

        <div class="page-break"></div>

        <div class="img-grid">
            <div class="img-card">
                <img src="{img3}" alt="Préférences appliquées" />
                <div class="img-caption">Vue 3 : Préférences appliquées (Couleur bleue + Boutons Appel actifs)</div>
            </div>
            <div class="img-card">
                <img src="{img4}" alt="Confirmation suppression" />
                <div class="img-caption">Vue 4 : Dialogue AlertDialog de confirmation de suppression</div>
            </div>
        </div>

        <h2>3. Réponses aux Questions de l'Examen (Section 10 de l'Énoncé)</h2>
        <ol>
            <li><strong>Quand les méthodes onCreate et onUpgrade d'un SQLiteOpenHelper sont-elles appelées ? Que se passe-t-il si l'on modifie onCreate sans changer la version ?</strong><br>
            <code>onCreate()</code> n'est exécutée <em>qu'une seule fois</em>, lors de la toute première création physique du fichier <code>contacts.db</code> sur l'appareil. <code>onUpgrade()</code> est appelée quand le numéro de version (<code>DATABASE_VERSION</code>) est incrémenté. Si l'on modifie <code>onCreate()</code> sans incrémenter la version, Android détecte que le fichier existe déjà et n'exécute pas le nouveau code : les nouvelles tables ou colonnes ne seront jamais créées.</li>

            <li><strong>Pourquoi utiliser des arguments ? plutôt que concaténer les valeurs dans une requête SQL ?</strong><br>
            La concaténation provoque des erreurs de syntaxe dès qu'une chaîne contient une apostrophe (ex: <code>"O'Neil"</code> ferme prématurément le délimiteur SQL) et expose l'application aux failles d'injection SQL. Les points d'interrogation <code>?</code> (requêtes préparées avec <code>selectionArgs</code>) séparent strictement le code SQL des données et neutralisent automatiquement tout caractère spécial.</li>

            <li><strong>Que renvoient insert(), update() et delete() ?</strong><br>
            <code>insert()</code> renvoie le <code>row ID</code> (_id) de la ligne nouvellement insérée (ou -1 en cas d'erreur). <code>update()</code> et <code>delete()</code> renvoient le nombre de lignes effectivement affectées par l'opération (permettant de confirmer à l'utilisateur que l'opération a réussi).</li>

            <li><strong>Pourquoi l'accès à la base ne doit-il pas se faire sur le thread principal ?</strong><br>
            Les accès au stockage disque peuvent être lents. Bloquer le thread principal (UI Thread) gèle l'interface, empêche le traitement des clics et déclenche la redoutée boîte de dialogue système <strong>ANR (Application Not Responding)</strong>. Nous l'avons évité en déléguant toutes les requêtes du DAO à <code>Dispatchers.IO</code> via <code>viewModelScope.launch(Dispatchers.IO)</code>.</li>

            <li><strong>Pourquoi le helper doit-il être unique et construit avec applicationContext ?</strong><br>
            Afin d'éviter d'ouvrir de multiples connexions concurrentes sur le même fichier SQLite (risquant de corrompre la base ou de lever des <code>SQLiteDatabaseLockedException</code>) et pour éviter les fuites de mémoire (Memory Leaks) en ne conservant pas de référence vers une Activité détruite.</li>

            <li><strong>SharedPreferences ou SQLite : critères de choix ?</strong><br>
            <code>SharedPreferences</code> est un fichier XML simple idéal pour stocker des couples clé/valeur primitifs légers (réglages d'affichage, booléens). <code>SQLite</code> est un SGBD relationnel robuste indispensable pour gérer des données structurées, volumineuses, relationnelles et interrogeables avec des filtres et des critères de tri complexes (les contacts).</li>

            <li><strong>Comment les préférences sont-elles appliquées sans redémarrer ?</strong><br>
            Dans <code>ListeContactsFragment.onResume()</code>, le fragment relit les préférences à jour via <code>PreferencesManager</code> et met à jour les propriétés du <code>ContactRecyclerAdapter</code> qui déclenche <code>notifyDataSetChanged()</code>.</li>

            <li><strong>Différence entre ACTION_DIAL et ACTION_CALL :</strong><br>
            <code>ACTION_DIAL</code> ouvre l'application téléphone avec le numéro pré-composé, laissant à l'utilisateur le soin de lancer l'appel (ne nécessite <strong>aucune permission</strong>). <code>ACTION_CALL</code> déclenche l'appel immédiatement et requiert impérativement la permission dangereuse <code>android.permission.CALL_PHONE</code> accordée à l'exécution.</li>

            <li><strong>Devenir de la base et des préférences après désinstallation / effacement des données :</strong><br>
            Dans les deux cas, la base de données <code>contacts.db</code> et le fichier XML des <code>SharedPreferences</code> sont <strong>définitivement supprimés</strong> car ils résident dans le stockage interne privé de l'application (<code>/data/data/com.example.contactsv4/</code>).</li>
        </ol>
    </body>
    </html>
    """
    generate_pdf(html, r"D:\L3MDW\Mobile\ExamenTP\Compte_Rendu_ExamenTP.pdf")

def create_v4_guide_etudiant():
    base_dir = r"D:/L3MDW/Mobile/ExamenTP/screenshots"
    img1 = get_b64(f"{base_dir}/v4_01_liste_initiale_sqlite.png")

    html = f"""
    <!DOCTYPE html>
    <html>
    <head><meta charset="utf-8"><style>{BASE_CSS}</style></head>
    <body>
        <div class="header-box">
            <h1>Guide Étudiant : Persistance SQLite Native et Préférences</h1>
            <p>Maîtriser SQLiteOpenHelper, le DAO, les requêtes préparées, Coroutines et AndroidX Preference</p>
            <div class="header-meta">
                <span><strong>Projet #7 :</strong> Contacts V4 (Examen)</span>
                <span><strong>Niveau :</strong> Avancé / Examen Final</span>
                <span><strong>Concepts :</strong> SQLite, Cursor, DAO, Coroutines IO, SharedPreferences</span>
            </div>
        </div>

        <h2>1. Présentation du TP d'Examen</h2>
        <p>Félicitations pour être arrivé au projet ultime de la série ! Ce TP d'examen rassemble l'ensemble des compétences indispensables d'un développeur Android professionnel : rendre les données <strong>permanentes</strong> grâce à une base relationnelle locale <strong>SQLite</strong>, exécuter les requêtes de manière asynchrone avec les <strong>Coroutines</strong>, et offrir un panneau de <strong>Préférences Utilisateur</strong> réactif en temps réel.</p>

        <div style="display: flex; gap: 20px; align-items: center; margin: 15px 0;">
            <div style="flex: 1;">
                <p><strong>L'architecture en couches de l'application :</strong></p>
                <ul>
                    <li><strong>Couche Données (Data Layer) :</strong> <code>ContactContract</code>, <code>ContactDbHelper</code> et <code>ContactDao</code> manipulent le SQL brut avec <code>ContentValues</code> et <code>Cursor</code>.</li>
                    <li><strong>Couche Métier (ViewModel) :</strong> <code>ContactViewModel</code> lance les requêtes en tâche de fond sur <code>Dispatchers.IO</code> et expose des données observables à l'UI.</li>
                    <li><strong>Couche Présentation (UI Layer) :</strong> Les fragments affichent la <code>RecyclerView</code> et appliquent dynamiquement les préférences de l'utilisateur.</li>
                </ul>
            </div>
            <div style="width: 140px; text-align: center;">
                <img src="{img1}" style="width: 130px; border-radius: 8px; border: 1px solid #CBD5E1;" alt="SQLite Contacts" />
            </div>
        </div>

        <h2>2. Les Étapes Clés du Développement SQLite</h2>

        <h3>Étape 1 : Le Contrat de Table (ContactContract.kt)</h3>
        <p>Ne codez JAMAIS de noms de colonnes en dur dans votre code. Déclarez-les tous sous forme de constantes :</p>
        <pre>object ContactContract {{
    object ContactEntry : BaseColumns {{
        const val TABLE_NAME = "contacts"
        const val COLUMN_ID = "_id"
        const val COLUMN_NOM = "nom"
        const val COLUMN_PRENOM = "prenom"
        const val COLUMN_TELEPHONE = "telephone"
    }}
}}</pre>

        <div class="page-break"></div>

        <h3>Étape 2 : Le DAO et les Requêtes Paramétrées (ContactDao.kt)</h3>
        <p>Pour insérer un contact et sécuriser les requêtes contre les erreurs d'apostrophes (ex: <em>"O'Neil"</em>) :</p>
        <pre>fun insert(contact: Contact): Long {{
    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {{
        put(ContactEntry.COLUMN_NOM, contact.nom)
        put(ContactEntry.COLUMN_PRENOM, contact.prenom)
        put(ContactEntry.COLUMN_TELEPHONE, contact.telephone)
    }}
    return db.insert(ContactEntry.TABLE_NAME, null, values)
}}

fun search(texte: String): List&lt;Contact&gt; {{
    val query = "%$texte%"
    // Utiliser des arguments ? obligatoires !
    val selection = "${{ContactEntry.COLUMN_NOM}} LIKE ? OR ${{ContactEntry.COLUMN_TELEPHONE}} LIKE ?"
    val selectionArgs = arrayOf(query, query)
    return db.query(ContactEntry.TABLE_NAME, null, selection, selectionArgs, ...).use {{ cursor ->
        // Lecture avec use pour garantir la fermeture du curseur
    }}
}}</pre>

        <h3>Étape 3 : Exécution Asynchrone avec Coroutines</h3>
        <p>Règle d'or absolue sous Android : ne jamais toucher au disque sur le thread principal !</p>
        <pre>fun supprimer(id: Long, onComplete: () -> Unit) {{
    viewModelScope.launch(Dispatchers.IO) {{
        dao.delete(id)
        val nouvelleListe = dao.getAll()
        withContext(Dispatchers.Main) {{
            _contacts.value = nouvelleListe
            onComplete()
        }}
    }}
}}</pre>

        <h3>Étape 4 : Les Préférences Utilisateur (AndroidX Preference)</h3>
        <p>Créer un <code>ParametresFragment</code> héritant de <code>PreferenceFragmentCompat</code> et appliquer les modifications dans <code>onResume()</code> sans redémarrer l'application.</p>

        <h2>3. Conseils Décisifs pour l'Examen</h2>
        <div class="note-box">
            <ul>
                <li><strong>Toujours fermer les Curseurs :</strong> Utilisez l'extension Kotlin <code>cursor.use {{ ... }}</code> qui garantit la fermeture automatique du curseur, même si une exception survient !</li>
                <li><strong>Singleton avec applicationContext :</strong> Votre <code>ContactDbHelper</code> doit toujours recevoir <code>context.applicationContext</code> pour éviter les fuites de mémoire d'activité.</li>
            </ul>
        </div>
    </body>
    </html>
    """
    generate_pdf(html, r"D:\L3MDW\Mobile\pdfs\07_Guide_Etudiant_Contacts_V4_SQLite_Preferences.pdf")

if __name__ == "__main__":
    create_v4_compte_rendu()
    create_v4_guide_etudiant()
