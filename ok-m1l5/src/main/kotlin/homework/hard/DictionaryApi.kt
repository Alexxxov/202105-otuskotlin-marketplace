package homework.hard

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import homework.hard.dto.Dictionary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import okhttp3.Response

class DictionaryApi(
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) {

    suspend fun findWord(locale: Locale, word: String): Dictionary = withContext(Dispatchers.Default){ // make something with context
        val url = "$DICTIONARY_API/${locale.code}/$word"
        println("Searching $url")

        getBody(HttpClient.get(url).execute()).first()
    }


    private fun getBody(response: Response): List<Dictionary> {
        if (!response.isSuccessful) {
            throw RuntimeException("Not found word")
        }

        return response.body?.let {
            objectMapper.readValue(it.string())
        } ?: throw RuntimeException("Body is null by some reason")
    }
}