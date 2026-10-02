package com.example.calculimc

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPoids: EditText
    private lateinit var editTextTaille: EditText
    private lateinit var textViewImc: TextView
    private lateinit var textViewCategorie: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Récupération des composants graphiques
        editTextPoids = findViewById(R.id.editTextPoids)
        editTextTaille = findViewById(R.id.editTextTaille)
        textViewImc = findViewById(R.id.textViewImc)
        textViewCategorie = findViewById(R.id.textViewCategorie)
        val buttonCalculer = findViewById<Button>(R.id.buttonCalculer)
        val buttonEffacer = findViewById<Button>(R.id.buttonEffacer)

        // (Facultatif) Restauration du dernier résultat après rotation de l'écran
        savedInstanceState?.let {
            textViewImc.text = it.getString(CLE_IMC, "")
            textViewCategorie.text = it.getString(CLE_CATEGORIE, "")
            val couleur = it.getInt(CLE_COULEUR, Color.BLACK)
            textViewImc.setTextColor(couleur)
            textViewCategorie.setTextColor(couleur)
        }

        buttonCalculer.setOnClickListener { calculerImc() }
        buttonEffacer.setOnClickListener { effacer() }
    }

    private fun calculerImc() {
        // 1. Récupérer le poids et la taille saisis
        val poidsTexte = editTextPoids.text.toString().trim()
        val tailleTexte = editTextTaille.text.toString().trim()

        // 2. Vérifier que les deux champs sont renseignés
        if (poidsTexte.isEmpty() || tailleTexte.isEmpty()) {
            afficherErreur(getString(R.string.erreur_champ_vide))
            return
        }

        // 3. Convertir en nombres décimaux (la virgule est acceptée)
        val poids = poidsTexte.replace(',', '.').toDoubleOrNull()
        val taille = tailleTexte.replace(',', '.').toDoubleOrNull()
        if (poids == null || taille == null) {
            afficherErreur(getString(R.string.erreur_valeur_invalide))
            return
        }

        // 4. Vérifier que les valeurs sont strictement positives
        if (poids <= 0.0 || taille <= 0.0) {
            afficherErreur(getString(R.string.erreur_valeur_non_positive))
            return
        }

        // 5. Calculer l'IMC
        val imc = poids / (taille * taille)

        // 6. Arrondir à deux chiffres après la virgule
        val imcArrondi = Math.round(imc * 100) / 100.0

        // 7. Déterminer la catégorie (if / else if / else)
        val categorie: String
        val couleur: Int
        if (imcArrondi < 18.5) {
            categorie = getString(R.string.categorie_insuffisance)
            couleur = COULEUR_ORANGE
        } else if (imcArrondi < 25) {
            categorie = getString(R.string.categorie_normale)
            couleur = COULEUR_VERT
        } else if (imcArrondi < 30) {
            categorie = getString(R.string.categorie_surpoids)
            couleur = COULEUR_ORANGE
        } else if (imcArrondi < 35) {
            categorie = getString(R.string.categorie_obesite_moderee)
            couleur = COULEUR_ROUGE
        } else if (imcArrondi < 40) {
            categorie = getString(R.string.categorie_obesite_severe)
            couleur = COULEUR_ROUGE
        } else {
            categorie = getString(R.string.categorie_obesite_morbide)
            couleur = COULEUR_ROUGE_FONCE
        }

        // 8. Afficher la valeur, la catégorie et la couleur adaptée
        textViewImc.text = getString(R.string.imc_resultat, imcArrondi)
        textViewCategorie.text = categorie
        textViewImc.setTextColor(couleur)
        textViewCategorie.setTextColor(couleur)
    }

    // Efface l'ancien résultat et affiche l'erreur dans un Toast, sans fermer l'application
    private fun afficherErreur(message: String) {
        viderResultat()
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun viderResultat() {
        textViewImc.text = ""
        textViewCategorie.text = ""
    }

    // Bouton Effacer : vide les champs et le résultat, curseur dans le champ du poids
    private fun effacer() {
        editTextPoids.text.clear()
        editTextTaille.text.clear()
        editTextPoids.error = null
        editTextTaille.error = null
        viderResultat()
        editTextPoids.requestFocus()
    }

    // (Facultatif) Sauvegarde du dernier résultat avant la rotation de l'écran
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CLE_IMC, textViewImc.text.toString())
        outState.putString(CLE_CATEGORIE, textViewCategorie.text.toString())
        outState.putInt(CLE_COULEUR, textViewImc.currentTextColor)
    }

    companion object {
        private const val CLE_IMC = "cle_imc"
        private const val CLE_CATEGORIE = "cle_categorie"
        private const val CLE_COULEUR = "cle_couleur"

        private val COULEUR_ORANGE = Color.rgb(255, 152, 0)
        private val COULEUR_VERT = Color.rgb(46, 125, 50)
        private val COULEUR_ROUGE = Color.rgb(211, 47, 47)
        private val COULEUR_ROUGE_FONCE = Color.rgb(139, 0, 0)
    }
}