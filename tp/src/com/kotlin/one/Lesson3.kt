package com.android.one

data class Weapon(
    val name: String,
    val power: Int
) {
    override fun toString(): String = "$name (Power: $power)"
}

interface Attacker {
    fun attack(target: Character)
}

interface Healer {
    fun heal(target: Character)
}

abstract class Character(
    val name: String,
    val type: String,
    maxHp: Int,
    val weapon: Weapon
) : Attacker {

    var hp: Int = maxHp
        private set

    val maxHp: Int = maxHp

    val isAlive: Boolean get() = hp > 0

    fun takeDamage(amount: Int) {
        hp = (hp - amount).coerceAtLeast(0)
    }

    fun receiveHeal(amount: Int) {
        hp = (hp + amount).coerceAtMin(maxHp)
    }

    override fun attack(target: Character) {
        val damage = weapon.power
        target.takeDamage(damage)
        println("   ⚔️  $name attacks ${target.name} with ${weapon.name} for $damage damage!")
        println("   💔 ${target.name} HP: ${target.hp}/${target.maxHp}")
        if (!target.isAlive) {
            println("   💀 ${target.name} has been slain!")
        }
    }

    abstract fun describeActions(): String

    fun status(): String {
        val state = if (isAlive) "❤️ $hp/$maxHp HP" else "💀 DEAD"
        return "[$type] $name — $state — ${weapon.name}"
    }

    override fun toString(): String = "$name ($type)"
}

private fun Int.coerceAtMin(max: Int): Int = if (this > max) max else this

class Warrior(name: String) : Character(
    name = name,
    type = "Warrior",
    maxHp = 120,
    weapon = Weapon("Steel Sword", 25)
) {
    override fun describeActions(): String = "1. Attack"
}

class Magus(name: String) : Character(
    name = name,
    type = "Magus",
    maxHp = 140,
    weapon = Weapon("Magic Staff", 15)
), Healer {

    private val healPower = 30

    override fun heal(target: Character) {
        target.receiveHeal(healPower)
        println("   ✨ $name heals ${target.name} for $healPower HP!")
        println("   💚 ${target.name} HP: ${target.hp}/${target.maxHp}")
    }

    override fun describeActions(): String = "1. Attack  |  2. Heal ally"
}

class Colossus(name: String) : Character(
    name = name,
    type = "Colossus",
    maxHp = 200,
    weapon = Weapon("War Hammer", 20)
) {
    override fun describeActions(): String = "1. Attack"
}

class Dwarf(name: String) : Character(
    name = name,
    type = "Dwarf",
    maxHp = 80,
    weapon = Weapon("Battle Axe", 40)
) {
    override fun describeActions(): String = "1. Attack"
}

class Player(val name: String) {
    val team: MutableList<Character> = mutableListOf()

    fun aliveCharacters(): List<Character> = team.filter { it.isAlive }

    fun isDefeated(): Boolean = aliveCharacters().isEmpty()

    fun displayTeam() {
        println("   📋 $name's team:")
        team.forEachIndexed { i, c ->
            println("      ${i + 1}. ${c.status()}")
        }
    }
}

class BattleArena {

    private lateinit var player1: Player
    private lateinit var player2: Player
    private var turnCount = 0

    private val usedNames = mutableSetOf<String>()

    private val availableTypes = listOf("Warrior", "Magus", "Colossus", "Dwarf")

    fun start() {
        println("═══════════════════════════════════════════════")
        println("       🕹️  BATTLE ARENA — Console Prototype")
        println("═══════════════════════════════════════════════\n")

        // Step 1: Create teams
        player1 = createPlayer(1)
        player2 = createPlayer(2)

        println("\n⚔️  Both teams are ready! Let the battle begin!\n")
        player1.displayTeam()
        println()
        player2.displayTeam()
        println()

        combat()

        endGame()
    }

    private fun createPlayer(number: Int): Player {
        println("───────────────────────────────────────────────")
        println("👤 Player $number, enter your name:")
        print("   > ")
        val playerName = readln().trim().ifEmpty { "Player $number" }
        val player = Player(playerName)

        val typesLeft = availableTypes.toMutableList()

        for (i in 1..3) {
            println("\n   🎭 Create character $i/3 for $playerName")
            println("   Available types: ${typesLeft.mapIndexed { idx, t -> "${idx + 1}.$t" }.joinToString("  ")}")
            print("   Choose type (number): ")

            val typeIndex = readValidIndex(typesLeft.size)
            val chosenType = typesLeft.removeAt(typeIndex)

            print("   Enter a unique name for your $chosenType: ")
            val charName = readUniqueName()

            val character = createCharacter(chosenType, charName)
            player.team.add(character)
            println("   ✅ ${character.name} the $chosenType joined the team!")
        }
        return player
    }

    private fun createCharacter(type: String, name: String): Character {
        return when (type) {
            "Warrior"  -> Warrior(name)
            "Magus"    -> Magus(name)
            "Colossus" -> Colossus(name)
            "Dwarf"    -> Dwarf(name)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
    private fun combat() {
        var activePlayer = player1
        var opponent = player2

        while (!player1.isDefeated() && !player2.isDefeated()) {
            turnCount++
            println("═══════════════════════════════════════════════")
            println("📢 TURN $turnCount — ${activePlayer.name}'s turn")
            println("═══════════════════════════════════════════════")

            activePlayer.displayTeam()
            println()

            // 1. Pick an alive character
            val aliveChars = activePlayer.aliveCharacters()
            println("   Choose your character:")
            aliveChars.forEachIndexed { i, c ->
                println("      ${i + 1}. ${c.status()}")
            }
            print("   > ")
            val charIndex = readValidIndex(aliveChars.size)
            val chosen = aliveChars[charIndex]
            println("   → ${chosen.name} steps forward!\n")

            // 2. Choose action
            println("   ${chosen.describeActions()}")
            val canHeal = chosen is Healer

            var action: Int
            while (true) {
                print("   Choose action: ")
                action = readValidIndex(if (canHeal) 2 else 1) + 1
                break
            }

            when (action) {
                1 -> {
                    val enemies = opponent.aliveCharacters()
                    println("\n   🎯 Choose target to attack:")
                    enemies.forEachIndexed { i, e ->
                        println("      ${i + 1}. ${e.status()}")
                    }
                    print("   > ")
                    val targetIndex = readValidIndex(enemies.size)
                    val target = enemies[targetIndex]
                    println()

                    chosen.attack(target)
                }
                2 -> {
                    val allies = activePlayer.aliveCharacters()
                    println("\n   💚 Choose ally to heal:")
                    allies.forEachIndexed { i, a ->
                        println("      ${i + 1}. ${a.status()}")
                    }
                    print("   > ")
                    val allyIndex = readValidIndex(allies.size)
                    val ally = allies[allyIndex]
                    println()

                    (chosen as Healer).heal(ally)
                }
            }

            println()

            val temp = activePlayer
            activePlayer = opponent
            opponent = temp
        }
    }

    private fun endGame() {
        println("═══════════════════════════════════════════════")
        println("               🏁 GAME OVER")
        println("═══════════════════════════════════════════════\n")

        val winner = if (player1.isDefeated()) player2 else player1
        println("🏆 Winner: ${winner.name}!")
        println("🔄 Total turns: $turnCount\n")

        println("── Final Status ──────────────────────────────")
        player1.displayTeam()
        println()
        player2.displayTeam()

        println("\n═══════════════════════════════════════════════")
        println("       Thanks for playing Battle Arena!")
        println("═══════════════════════════════════════════════")
    }

    private fun readValidIndex(max: Int): Int {
        while (true) {
            val input = readln().trim().toIntOrNull()
            if (input != null && input in 1..max) return input - 1
            print("   ⚠️  Please enter a number between 1 and $max: ")
        }
    }
    private fun readUniqueName(): String {
        while (true) {
            val name = readln().trim()
            if (name.isBlank()) {
                print("   ⚠️  Name cannot be empty. Try again: ")
            } else if (name.lowercase() in usedNames.map { it.lowercase() }) {
                print("   ⚠️  '$name' is already taken. Choose another: ")
            } else {
                usedNames.add(name)
                return name
            }
        }
    }
}

fun main() {
    BattleArena().start()
}