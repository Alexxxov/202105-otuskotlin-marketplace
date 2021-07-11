package homework.easy

import kotlinx.coroutines.delay

suspend fun findNumberInList(toFind: Int, numbers: List<Int>): Int {
    delay(2000L)
    return numbers.firstOrNull { it == toFind } ?: -1
}

