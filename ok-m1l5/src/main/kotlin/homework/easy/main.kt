package homework.easy

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main() {
    val time = measureTimeMillis {
        runBlocking {
            val numbers = coroutineScope{async {generateNumbers()}}.await()
            val foundNumbers = coroutineScope {
                (0..10)
                    .map {
                        async {
                            findNumberInList(it, numbers)
                        }
                    }.awaitAll()
            }
//            val num1 = async {  findNumberInList(10, numbers) }
//            val num2 = async {  findNumberInList(1000, numbers) }
//            val foundNumbers = listOf(
//                num1.await(), num2.await()
//            )
            println(foundNumbers)
            foundNumbers.forEach {
                if (it != -1) {
                    println("Your number found $it")
                } else {
                    println("Not found number $it")
                }
            }
        }
    }
    println("Time: $time")
}