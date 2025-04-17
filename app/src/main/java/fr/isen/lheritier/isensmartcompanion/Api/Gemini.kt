package fr.isen.lheritier.isensmartcompanion.Api

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object Gemini {

    private const val TAG = "Gemini"
    private const val API_KEY = "AIzaSyCRpCN5LUn_a-gXe-RND56MfpsJFtK5mHU"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY
    )

    suspend fun getGeminiResponse(prompt: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Envoi de la requête : $prompt")
                val response = generativeModel.generateContent(content { text(prompt) })
                val result = response.text
                Log.d(TAG, "Réponse reçue : $result")
                result
            } catch (e: Exception) {
                Log.e(TAG, "Erreur lors de la requête Gemini : ${e.message}")
                null
            }
        }
    }
}