package com.android.one

// ✅ Exercise 0: Test runner
fun verify(name: String, block: () -> Boolean) {
    try {
        check(block()) { "❌ Test failed: $name" }
        println("✅ $name")
    } catch (e: Throwable) {
        println("❌ $name → ${e.message}")
    }
}


// ✅ Exercise 1 — Immutable List
fun ex1CreateImmutableList(): List<Int> {
    return listOf(10, 20, 30, 40, 50)
}


// ✅ Exercise 2 — Mutable List
fun ex2CreateMutableList(): MutableList<String> {
    val list = mutableListOf("Kotlin", "Java", "Swift")
    list.add("Dart")
    return list
}


// ✅ Exercise 3 — Filter Even Numbers
fun ex3FilterEvenNumbers(): List<Int> {
    val numbers = (1..10).toList()
    return numbers.filter { it % 2 == 0 }
}


// ✅ Exercise 4 — Filter and Map Ages
fun ex4FilterAndMapAges(ages: List<Int>): List<String> {
    return ages
        .filter { it >= 18 }
        .map { "Adult: $it" }
}


// ✅ Exercise 5 — Flatten Nested Lists
fun ex5FlattenList(): List<Int> {
    val nested = listOf(listOf(1, 2), listOf(3, 4), listOf(5))
    return nested.flatten()
}


// ✅ Exercise 6 — FlatMap Words
fun ex6FlatMapWords(): List<String> {
    val phrases = listOf("Kotlin is fun", "I love lists")
    return phrases.flatMap { it.split(" ") }
}


// ✅ Exercise 7 — Eager Processing
fun ex7EagerProcessing(): List<Int> {
    val start = System.currentTimeMillis()

    val result = (1..1_000_000)
        .toList()
        .filter { it % 3 == 0 }
        .map { it * it }
        .take(5)

    val end = System.currentTimeMillis()
    println("   ⏱ Eager time: ${end - start} ms")
    return result
}


// ✅ Exercise 8 — Lazy Processing
fun ex8LazyProcessing(): List<Int> {
    val start = System.currentTimeMillis()

    val result = (1..1_000_000)
        .asSequence()
        .filter { it % 3 == 0 }
        .map { it * it }
        .take(5)
        .toList()

    val end = System.currentTimeMillis()
    println("   ⏱ Lazy time: ${end - start} ms")
    return result
}


// ✅ Exercise 9 — Chain: Filter and Sort Names
fun ex9FilterAndSortNames(names: List<String>): List<String> {
    return names
        .filter { it.startsWith("A") }
        .map { it.uppercase() }
        .sorted()
}


fun main() {
    println("🔍 Running Kotlin List Processing Tests...\n")

    // Exercise 1
    verify("ex1 — Immutable list has 5 elements") {
        ex1CreateImmutableList().size == 5
    }

    // Exercise 2
    verify("ex2 — Mutable list has 4 elements after add") {
        ex2CreateMutableList().size == 4
    }
    verify("ex2 — Last element is 'Dart'") {
        ex2CreateMutableList().last() == "Dart"
    }

    // Exercise 3
    verify("ex3 — Even numbers from 1..10") {
        ex3FilterEvenNumbers() == listOf(2, 4, 6, 8, 10)
    }

    // Exercise 4
    verify("ex4 — Filter adults and format") {
        ex4FilterAndMapAges(listOf(12, 25, 17, 30, 8)) == listOf("Adult: 25", "Adult: 30")
    }
    verify("ex4 — No adults returns empty") {
        ex4FilterAndMapAges(listOf(5, 10, 16)).isEmpty()
    }

    // Exercise 5
    verify("ex5 — Flatten nested lists") {
        ex5FlattenList() == listOf(1, 2, 3, 4, 5)
    }

    // Exercise 6
    verify("ex6 — FlatMap extracts all words") {
        ex6FlatMapWords() == listOf("Kotlin", "is", "fun", "I", "love", "lists")
    }

    // Exercise 7 & 8
    val eagerResult = ex7EagerProcessing()
    val lazyResult = ex8LazyProcessing()

    verify("ex7 — Eager returns 5 results") {
        eagerResult.size == 5
    }
    verify("ex7 — First eager result is 9 (3²)") {
        eagerResult.first() == 9
    }
    verify("ex8 — Lazy returns 5 results") {
        lazyResult.size == 5
    }
    verify("ex8 — Lazy and Eager produce same results") {
        eagerResult == lazyResult
    }

    // Exercise 9
    verify("ex9 — Filter A-names, uppercase, sorted") {
        ex9FilterAndSortNames(listOf("Alice", "Bob", "Anna", "Charlie", "Alex")) ==
                listOf("ALEX", "ALICE", "ANNA")
    }
    verify("ex9 — No A-names returns empty") {
        ex9FilterAndSortNames(listOf("Bob", "Charlie")).isEmpty()
    }

    println("\n🎉 All list processing tests complete!")
}