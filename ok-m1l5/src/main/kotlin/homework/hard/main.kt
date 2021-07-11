package homework.hard

import kotlinx.coroutines.*
import java.io.File
import kotlin.system.measureTimeMillis

fun main() = runBlocking{
    val time = measureTimeMillis {
        val dictionaryApi = DictionaryApi()
        val words = FileReader.readFile().split(" ", "\n").toSet()

        val dictionaries = async{findWords(dictionaryApi, words, Locale.EN)}

        dictionaries.await().map { dictionary ->
            print("For word ${dictionary.word} i found examples: ")
            println(dictionary.meanings.map { definition -> definition.definitions.map { it.example } })
        }
    }
    println("Time without couroutine ~ 30770")
    println("Time: $time")
}

private suspend fun findWords(dictionaryApi: DictionaryApi, words: Set<String>, locale: Locale) = supervisorScope{
    words.map {
        async{dictionaryApi.findWord(locale, it)}
    }.awaitAll()
}// make some suspensions and async


object FileReader {
    fun readFile(): String =
        File(this::class.java.classLoader.getResource("words.txt")?.toURI() ?: throw RuntimeException("Can't read file")).readText()
}
