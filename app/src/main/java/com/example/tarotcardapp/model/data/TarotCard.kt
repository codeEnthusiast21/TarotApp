package com.example.tarotcardapp.model.data

data class TarotCard(
    val type: String,
    val name_short: String,
    val name: String,
    val value: String,
    val value_int: Int,
    val meaning_up: String,
    val meaning_rev: String,
    val desc: String,
    var isSelected: Boolean = false,
    var isReversed: Boolean = false
) {
    fun getImageFileName(): String {
        // Convert the name to a format matching the GitHub repo file names
        return when {
            name.contains("The ", ignoreCase = true) -> {
                // For "The Magician" -> "TheMagician.jpg"
                name.replace("The ", "The").replace(" ", "") + ".jpg"
            }
            name.contains(" of ", ignoreCase = true) -> {
                // For "Ace of Cups" -> "aceofcups.jpeg"
                name.lowercase().replace(" ", "") + ".jpeg"
            }
            else -> {
                // Fallback
                name.replace(" ", "") + ".jpg"
            }
        }
    }

    fun getMeaning(): String {
        return if (isReversed) meaning_rev else meaning_up
    }
}

data class TarotCardResponse(
    val nhits: Int,
    val cards: List<TarotCard>
)