package search

import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

val scanner = Scanner(System.`in`)

fun main(args: Array<String>) {
    val data = inputFile(args)
    menu(data)
}

fun inputFile(args: Array<String>): ArrayList<String> {
    val file = fileFinder(args)
    return ArrayList(file.readLines())
}

fun fileFinder(args: Array<String>): File {
    val fileName = args[args.indexOf("--data") + 1]
    return File(fileName)
}

fun getInvertedIndex(data: ArrayList<String>): MutableMap<String, MutableSet<Int>> {
    val map = mutableMapOf<String, MutableSet<Int>>()
    for (line in data) {
        for (word in line.trim().split(" ")) {
            if (!map.keys.contains(word.toLowerCase())) {
                map[word.toLowerCase()] = mutableSetOf(data.indexOf(line))
            } else {
                val temp = map[word.toLowerCase()]!!
                temp.add(data.indexOf(line))
                map[word.toLowerCase()] = temp
            }
        }
    }
    return map
}

fun menu(data: ArrayList<String>) {
    val invertedIndex = getInvertedIndex(data)
    while (true) {
        println(
                "=== Menu ===\n" +
                        "1. Find a person\n" +
                        "2. Print all people\n" +
                        "0. Exit"
        )
        val choose = scanner.next()!!.toInt()
        scanner.nextLine()
        when (choose) {
            1 -> find(data, invertedIndex)
            2 -> printAll(data)
            0 -> exit()
            else -> println("\nIncorrect option! Try again.\n")
        }
    }
}

fun find(data: ArrayList<String>, invertedIndex: MutableMap<String, MutableSet<Int>>) {
    println("Select a matching strategy: ALL, ANY, NONE")
    val strategy = scanner.next()
    scanner.nextLine()
    when (strategy) {
        "ALL" -> all(data, invertedIndex)
        "ANY" -> any(data, invertedIndex)
        "NONE" -> none(data, invertedIndex)
        else -> println("\nIncorrect option! Try again.\n")
    }
}

fun all(data: ArrayList<String>, invertedIndex: MutableMap<String, MutableSet<Int>>) {
    println("\nEnter a name or email to search all suitable people.")
    val searchQuery = scanner.nextLine()
    println()
    val resultList: ArrayList<String> = ArrayList()
    if (invertedIndex.containsKey(searchQuery.split(" ").first().toLowerCase())) {
        for (lineNumber in invertedIndex[searchQuery.split(" ").first().toLowerCase()]!!) {
            var correct = true
            for (word in searchQuery.split(" ")) {
                if (!data[lineNumber].contains(word)) {
                    correct = false
                }
            }
            if (correct) {
                resultList.add(data[lineNumber])
            }
        }
    } else {
        println("No matching people found.")
    }
    println("${resultList.size} persons found:")
    resultList.forEach { println(it) }
    println()
}

fun any(data: ArrayList<String>, invertedIndex: MutableMap<String, MutableSet<Int>>) {
    println("\nEnter a name or email to search all suitable people.")
    val searchQuery = scanner.nextLine()
    println()
    val resultSet: MutableSet<Int> = mutableSetOf()

    for (word in searchQuery.split(" ")) {
        if (invertedIndex.containsKey(word.toLowerCase())) {
            invertedIndex[word.toLowerCase()]?.let { resultSet.addAll(it) }
        }
    }
    if (resultSet.size == 0) {
        println("No matching people found.")
    } else {
        println("${resultSet.size} persons found:")
        resultSet.forEach { println(data[it]) }
    }
    println()
}

fun none(data: ArrayList<String>, invertedIndex: MutableMap<String, MutableSet<Int>>) {
    println("\nEnter a name or email to search all suitable people.")
    val searchQuery = scanner.nextLine()
    val containLines: MutableSet<Int> = mutableSetOf()
    for (queryWord in searchQuery.split(" ")) {
        if (invertedIndex.containsKey(queryWord.toLowerCase())) {
            invertedIndex[queryWord.toLowerCase()]?.let { containLines.addAll(it) }
        }
    }
    val resultLines: MutableSet<Int> = mutableSetOf()
    for (lines in invertedIndex.values) {
        resultLines.addAll(lines)
    }
    resultLines.removeAll(containLines)
    if (resultLines.size == 0) {
        println("No matching people found.")
    } else {
        println("${resultLines.size} persons found:")
        resultLines.forEach { println(data[it]) }
    }
    println()


}

fun printAll(data: ArrayList<String>) {
    println("\n=== List of people ===")
    data.forEach { line -> println(line) }
    println()
}

fun exit() {
    println("\nBye!")
    scanner.close()
    exitProcess(0)
}
