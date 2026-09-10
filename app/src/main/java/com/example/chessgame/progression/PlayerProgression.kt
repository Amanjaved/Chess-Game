package com.example.chessgame.progression

import android.content.Context
import android.content.SharedPreferences
import com.example.chessgame.ai.AIDifficulty
import com.example.chessgame.engine.GameOutcome
import com.example.chessgame.engine.OutcomeReason
import com.example.chessgame.engine.PieceColor

/**
 * Player Achievement definition with tracking criteria and XP reward.
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val badgeIcon: String,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null
)

/**
 * Player Progression & Profile model.
 */
data class PlayerProfile(
    val playerName: String = "Player19599905",
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpForNextLevel: Int = 300,
    val levelProgress: Float = 0f,
    val title: String = "Pawn Striker",
    val coins: Int = 500,
    val gems: Int = 35,
    val claimedDailyGiftToday: Boolean = false,
    val claimedChestToday: Boolean = false,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalGames: Int = 0,
    val puzzlesSolved: Int = 0,
    val unlockedThemeIds: Set<String> = emptySet(),
    val achievements: List<Achievement> = emptyList()
) {
    val winRate: Int
        get() = if (totalGames > 0) ((wins.toFloat() / totalGames) * 100).toInt() else 0
}

/**
 * XP & Currency Gain breakdown after concluding a match.
 */
data class XpGainSummary(
    val totalXpGained: Int,
    val matchXp: Int,
    val difficultyBonus: Int,
    val streakBonus: Int,
    val coinsGained: Int = 0,
    val gemsGained: Int = 0,
    val achievementsUnlocked: List<Achievement>,
    val didLevelUp: Boolean,
    val oldLevel: Int,
    val newLevel: Int
)

/**
 * Centralized, 100% offline Player Progression Manager.
 * Handles XP calculations, Level milestones, Achievements, and persistent local storage.
 */
object PlayerProgressionManager {
    private const val PREFS_NAME = "chess_player_progression"
    private var prefs: SharedPreferences? = null

    // Base achievements catalog
    private val DEFAULT_ACHIEVEMENTS = listOf(
        Achievement(
            id = "first_blood",
            title = "First Blood",
            description = "Win your first chess game",
            badgeIcon = "⚔️",
            xpReward = 100
        ),
        Achievement(
            id = "checkmate_delivery",
            title = "Checkmate!",
            description = "Deliver a decisive checkmate",
            badgeIcon = "👑",
            xpReward = 150
        ),
        Achievement(
            id = "tactician_5",
            title = "Tactician",
            description = "Win 5 matches",
            badgeIcon = "♟️",
            xpReward = 250
        ),
        Achievement(
            id = "unstoppable_streak",
            title = "Unstoppable",
            description = "Win 3 matches consecutively",
            badgeIcon = "🔥",
            xpReward = 300
        ),
        Achievement(
            id = "grand_strategist",
            title = "Grand Strategist",
            description = "Defeat the Expert AI",
            badgeIcon = "🧠",
            xpReward = 500
        ),
        Achievement(
            id = "master_analyst",
            title = "Scholar's Eye",
            description = "Analyse a completed match",
            badgeIcon = "📜",
            xpReward = 100
        ),
        Achievement(
            id = "speed_demon",
            title = "Lightning Fast",
            description = "Win a match in under 4 minutes",
            badgeIcon = "⚡",
            xpReward = 200
        ),
        Achievement(
            id = "theme_collector",
            title = "The Collector",
            description = "Equip a custom board and piece theme",
            badgeIcon = "🎨",
            xpReward = 150
        ),
        Achievement(
            id = "puzzle_solver",
            title = "Tactical Insight",
            description = "Solve your first daily puzzle",
            badgeIcon = "🧩",
            xpReward = 100
        )
    )

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun xpNeededForLevel(level: Int): Int {
        return 200 + (level * 100)
    }

    private fun getTitleForLevel(level: Int): String {
        return when {
            level >= 30 -> "Grandmaster"
            level >= 22 -> "Master Strategist"
            level >= 16 -> "Chess Tactician"
            level >= 11 -> "Queen's Vanguard"
            level >= 7  -> "Rook Guardian"
            level >= 4  -> "Bishop's Scholar"
            level >= 2  -> "Knight Errant"
            else        -> "Pawn Striker"
        }
    }

    fun getProfile(): PlayerProfile {
        val p = prefs ?: return PlayerProfile()

        val name = p.getString("player_name", "Player19599905") ?: "Player19599905"
        val level = p.getInt("level", 1)
        val currentXp = p.getInt("current_xp", 0)
        val coins = p.getInt("coins", 500)
        val gems = p.getInt("gems", 35)
        val claimedDailyGiftToday = p.getBoolean("claimed_daily_gift_today", false)
        val claimedChestToday = p.getBoolean("claimed_chest_today", false)
        val wins = p.getInt("wins", 0)
        val losses = p.getInt("losses", 0)
        val draws = p.getInt("draws", 0)
        val currentStreak = p.getInt("current_streak", 0)
        val bestStreak = p.getInt("best_streak", 0)
        val totalGames = p.getInt("total_games", 0)
        val puzzlesSolved = p.getInt("puzzles_solved", 0)

        val xpNeeded = xpNeededForLevel(level)
        val progress = if (xpNeeded > 0) (currentXp.toFloat() / xpNeeded).coerceIn(0f, 1f) else 0f

        val unlockedThemeIds = p.getStringSet("unlocked_themes", emptySet()) ?: emptySet()

        val achievements = DEFAULT_ACHIEVEMENTS.map { ach ->
            val unlocked = p.getBoolean("ach_${ach.id}", false)
            val date = p.getString("ach_${ach.id}_date", null)
            ach.copy(isUnlocked = unlocked, unlockedDate = date)
        }

        return PlayerProfile(
            playerName = name,
            level = level,
            currentXp = currentXp,
            xpForNextLevel = xpNeeded,
            levelProgress = progress,
            title = getTitleForLevel(level),
            coins = coins,
            gems = gems,
            claimedDailyGiftToday = claimedDailyGiftToday,
            claimedChestToday = claimedChestToday,
            wins = wins,
            losses = losses,
            draws = draws,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            totalGames = totalGames,
            puzzlesSolved = puzzlesSolved,
            unlockedThemeIds = unlockedThemeIds,
            achievements = achievements
        )
    }

    fun setPlayerName(name: String) {
        prefs?.edit()?.putString("player_name", name.trim().take(20))?.apply()
    }

    fun addCoins(amount: Int) {
        val p = prefs ?: return
        val current = p.getInt("coins", 500)
        p.edit().putInt("coins", (current + amount).coerceAtLeast(0)).apply()
    }

    fun addGems(amount: Int) {
        val p = prefs ?: return
        val current = p.getInt("gems", 35)
        p.edit().putInt("gems", (current + amount).coerceAtLeast(0)).apply()
    }

    fun spendCoins(amount: Int): Boolean {
        val p = prefs ?: return false
        val current = p.getInt("coins", 500)
        if (current >= amount) {
            p.edit().putInt("coins", current - amount).apply()
            return true
        }
        return false
    }

    fun spendGems(amount: Int): Boolean {
        val p = prefs ?: return false
        val current = p.getInt("gems", 35)
        if (current >= amount) {
            p.edit().putInt("gems", current - amount).apply()
            return true
        }
        return false
    }

    fun claimDailyGift(): Pair<Int, Int>? {
        val p = prefs ?: return null
        val already = p.getBoolean("claimed_daily_gift_today", false)
        if (already) return null
        val giftCoins = 75
        val giftGems = 5
        val curCoins = p.getInt("coins", 500)
        val curGems = p.getInt("gems", 35)
        p.edit()
            .putBoolean("claimed_daily_gift_today", true)
            .putInt("coins", curCoins + giftCoins)
            .putInt("gems", curGems + giftGems)
            .apply()
        return Pair(giftCoins, giftGems)
    }

    fun claimRewardsChest(): Pair<Int, Int>? {
        val p = prefs ?: return null
        val already = p.getBoolean("claimed_chest_today", false)
        if (already) return null
        val chestCoins = 150
        val chestGems = 10
        val curCoins = p.getInt("coins", 500)
        val curGems = p.getInt("gems", 35)
        p.edit()
            .putBoolean("claimed_chest_today", true)
            .putInt("coins", curCoins + chestCoins)
            .putInt("gems", curGems + chestGems)
            .apply()
        addXp(150)
        return Pair(chestCoins, chestGems)
    }

    /**
     * Records match conclusion and calculates XP and unlocked achievements.
     */
    fun recordMatchConclusion(
        outcome: GameOutcome,
        humanColor: PieceColor,
        isAIMode: Boolean,
        difficulty: AIDifficulty?,
        durationSeconds: Int
    ): XpGainSummary {
        val p = prefs ?: return XpGainSummary(0, 0, 0, 0, 0, 0, emptyList(), false, 1, 1)

        val isWin = outcome.winner == humanColor
        val isLoss = outcome.winner != null && !isWin
        val isDraw = outcome.isDraw || outcome.winner == null

        var oldLevel = p.getInt("level", 1)
        var currentXp = p.getInt("current_xp", 0)
        var wins = p.getInt("wins", 0)
        var losses = p.getInt("losses", 0)
        var draws = p.getInt("draws", 0)
        var currentStreak = p.getInt("current_streak", 0)
        var bestStreak = p.getInt("best_streak", 0)
        var totalGames = p.getInt("total_games", 0)

        totalGames++
        if (isWin) {
            wins++
            currentStreak++
            if (currentStreak > bestStreak) bestStreak = currentStreak
        } else if (isLoss) {
            losses++
            currentStreak = 0
        } else {
            draws++
        }

        // Calculate XP
        val matchXp = when {
            isWin -> 100
            isDraw -> 50
            else -> 25
        }

        val difficultyBonus = if (isWin && isAIMode && difficulty != null) {
            when (difficulty) {
                AIDifficulty.EXPERT -> 75
                AIDifficulty.HARD -> 40
                AIDifficulty.MEDIUM -> 20
                AIDifficulty.EASY -> 0
            }
        } else 0

        val streakBonus = if (isWin && currentStreak >= 2) (currentStreak * 10).coerceAtMost(50) else 0

        var totalXpGained = matchXp + difficultyBonus + streakBonus

        // Check Achievements
        val newAchievements = mutableListOf<Achievement>()

        fun unlock(id: String) {
            if (!p.getBoolean("ach_$id", false)) {
                val found = DEFAULT_ACHIEVEMENTS.find { it.id == id }
                if (found != null) {
                    val date = java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault()).format(java.util.Date())
                    p.edit()
                        .putBoolean("ach_$id", true)
                        .putString("ach_${id}_date", date)
                        .apply()
                    newAchievements.add(found.copy(isUnlocked = true, unlockedDate = date))
                    totalXpGained += found.xpReward
                }
            }
        }

        if (isWin) {
            unlock("first_blood")
            if (outcome.reason == OutcomeReason.CHECKMATE) {
                unlock("checkmate_delivery")
            }
            if (wins >= 5) {
                unlock("tactician_5")
            }
            if (currentStreak >= 3) {
                unlock("unstoppable_streak")
            }
            if (isAIMode && difficulty == AIDifficulty.EXPERT) {
                unlock("grand_strategist")
            }
            if (durationSeconds < 240) { // under 4 minutes
                unlock("speed_demon")
            }
        }

        // Apply XP and calculate level progression
        var newLevel = oldLevel
        currentXp += totalXpGained

        var xpNeeded = xpNeededForLevel(newLevel)
        while (currentXp >= xpNeeded) {
            currentXp -= xpNeeded
            newLevel++
            xpNeeded = xpNeededForLevel(newLevel)
        }

        val didLevelUp = newLevel > oldLevel

        val coinsGained = if (isWin) 50 else if (isDraw) 25 else 10
        val gemsGained = if (didLevelUp) 5 else 0
        val curCoins = p.getInt("coins", 500)
        val curGems = p.getInt("gems", 35)

        // Persist
        p.edit()
            .putInt("level", newLevel)
            .putInt("current_xp", currentXp)
            .putInt("coins", curCoins + coinsGained)
            .putInt("gems", curGems + gemsGained)
            .putInt("wins", wins)
            .putInt("losses", losses)
            .putInt("draws", draws)
            .putInt("current_streak", currentStreak)
            .putInt("best_streak", bestStreak)
            .putInt("total_games", totalGames)
            .apply()

        return XpGainSummary(
            totalXpGained = totalXpGained,
            matchXp = matchXp,
            difficultyBonus = difficultyBonus,
            streakBonus = streakBonus,
            coinsGained = coinsGained,
            gemsGained = gemsGained,
            achievementsUnlocked = newAchievements,
            didLevelUp = didLevelUp,
            oldLevel = oldLevel,
            newLevel = newLevel
        )
    }

    /**
     * Unlocks specific achievement manually (e.g. analysis, tutorial, theme).
     */
    fun unlockAchievement(id: String): Achievement? {
        val p = prefs ?: return null
        if (!p.getBoolean("ach_$id", false)) {
            val found = DEFAULT_ACHIEVEMENTS.find { it.id == id } ?: return null
            val date = java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault()).format(java.util.Date())
            p.edit()
                .putBoolean("ach_$id", true)
                .putString("ach_${id}_date", date)
                .apply()

            // Award XP
            addXp(found.xpReward)
            return found.copy(isUnlocked = true, unlockedDate = date)
        }
        return null
    }

    fun addXp(amount: Int) {
        val p = prefs ?: return
        var level = p.getInt("level", 1)
        var currentXp = p.getInt("current_xp", 0) + amount
        var xpNeeded = xpNeededForLevel(level)

        while (currentXp >= xpNeeded) {
            currentXp -= xpNeeded
            level++
            xpNeeded = xpNeededForLevel(level)
        }

        p.edit()
            .putInt("level", level)
            .putInt("current_xp", currentXp)
            .apply()
    }

    fun recordPuzzleSolved(): Int {
        val p = prefs ?: return 50
        val solved = p.getInt("puzzles_solved", 0) + 1
        p.edit().putInt("puzzles_solved", solved).apply()
        unlockAchievement("puzzle_solver")
        val xpGain = 75
        addXp(xpGain)
        return xpGain
    }

    /**
     * Checks if a theme is unlocked by player level or progress.
     */
    fun isThemeUnlocked(themeId: String, requiredLevel: Int = 1): Boolean {
        val p = prefs ?: return true
        val playerLevel = p.getInt("level", 1)
        return playerLevel >= requiredLevel
    }
}
