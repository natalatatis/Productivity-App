package com.example.sp2.ai

import com.example.sp2.model.Priority
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

data class ParsedTask(
    val title: String,
    val description: String,
    val date: LocalDate?,
    val time: LocalTime?,
    val priority: Priority,
    val isConfident: Boolean
)

object TaskInstructionParser {

    private val weekdays = mapOf(
        "lunes" to DayOfWeek.MONDAY,
        "martes" to DayOfWeek.TUESDAY,
        "miércoles" to DayOfWeek.WEDNESDAY,
        "miercoles" to DayOfWeek.WEDNESDAY,
        "jueves" to DayOfWeek.THURSDAY,
        "viernes" to DayOfWeek.FRIDAY,
        "sábado" to DayOfWeek.SATURDAY,
        "sabado" to DayOfWeek.SATURDAY,
        "domingo" to DayOfWeek.SUNDAY
    )

    private val numberWords = mapOf(
        "un" to 1, "uno" to 1, "una" to 1,
        "dos" to 2, "tres" to 3, "cuatro" to 4, "cinco" to 5,
        "seis" to 6, "siete" to 7, "ocho" to 8, "nueve" to 9, "diez" to 10
    )

    private val leadingCommands = listOf(
        "orbi,", "orbi", "recuérdame", "recuerdame",
        "crea una tarea para", "crea una tarea",
        "agrega una tarea para", "agrega una tarea", "agrega",
        "añade una tarea para", "añade", "anade",
        "tengo"
    )

    private val highPriorityWords = listOf(
        "muy importante", "bastante importante", "importante",
        "urgente", "urgentísimo", "urgentisimo", "crucial", "prioridad alta"
    )

    private val mediumPriorityWords = listOf(
        "prioridad media", "prioridad normal", "media prioridad",
        "importancia media", "prioridad intermedia"
    )

    private val lowPriorityWords = listOf(
        "sin prisa", "cuando pueda", "no es urgente",
        "prioridad baja", "no es importante", "no es tan importante"
    )

    fun parse(rawText: String): ParsedTask {

        var text = rawText.trim()
        var confident = true

        val lowerStart = text.lowercase(Locale.getDefault())
        for (cmd in leadingCommands.sortedByDescending { it.length }) {
            if (lowerStart.startsWith(cmd)) {
                text = text.substring(cmd.length).trim().trimStart(',', ' ')
                break
            }
        }

        val today = LocalDate.now()
        var date: LocalDate? = null
        var time: LocalTime? = null

        var lowerText = text.lowercase(Locale.getDefault())

        // --- Priority ---
        val priority = when {
            highPriorityWords.any { lowerText.contains(it) } -> Priority.HIGH
            mediumPriorityWords.any { lowerText.contains(it) } -> Priority.MEDIUM
            lowPriorityWords.any { lowerText.contains(it) } -> Priority.LOW
            else -> Priority.NONE
        }

        val matchedPriorityWord = (highPriorityWords + mediumPriorityWords + lowPriorityWords)
            .firstOrNull { lowerText.contains(it) }

        if (matchedPriorityWord != null) {
            text = removePhrase(text, matchedPriorityWord)

            // Cleans up dangling connector words left behind, e.g.
            // "...que es bastante importante" -> "...que es" -> ""
            // "...en 4 días con prioridad media" -> "...en 4 días con" -> ""
            text = text
                .replace(Regex("""\bque\s+es\b""", RegexOption.IGNORE_CASE), "")
                .replace(Regex("""\by\s+es\b""", RegexOption.IGNORE_CASE), "")
                .replace(Regex("""\bcon\s*$"""), "")
                .replace(Regex("""\s{2,}"""), " ")
                .trim()

            lowerText = text.lowercase(Locale.getDefault())
        }

        // --- Date: "en N días/semanas" ---
        val inNRegex = Regex("""en\s+(\d+|\p{L}+)\s+(d[ií]as?|semanas?)""", RegexOption.IGNORE_CASE)
        val inMatch = inNRegex.find(lowerText)

        if (inMatch != null) {
            val qtyRaw = inMatch.groupValues[1].lowercase(Locale.getDefault())
            val qty = qtyRaw.toIntOrNull() ?: numberWords[qtyRaw]
            val unit = inMatch.groupValues[2].lowercase(Locale.getDefault())

            if (qty != null) {
                date = if (unit.startsWith("semana")) {
                    today.plusWeeks(qty.toLong())
                } else {
                    today.plusDays(qty.toLong())
                }
                text = text.replace(Regex(Regex.escape(inMatch.value), RegexOption.IGNORE_CASE), "").trim()
                lowerText = text.lowercase(Locale.getDefault())
            }
        }

        // --- Date: simple keywords ---
        if (date == null) {
            when {
                lowerText.contains("pasado mañana") || lowerText.contains("pasado manana") -> {
                    date = today.plusDays(2)
                    text = removePhrase(text, "pasado mañana", "pasado manana")
                }
                lowerText.contains("mañana") || lowerText.contains("manana") -> {
                    date = today.plusDays(1)
                    text = removePhrase(text, "mañana", "manana")
                }
                lowerText.contains("hoy") -> {
                    date = today
                    text = removePhrase(text, "hoy")
                }
                else -> {
                    val weekdayMatch = weekdays.entries.firstOrNull { lowerText.contains(it.key) }
                    if (weekdayMatch != null) {
                        var candidate = today
                        do {
                            candidate = candidate.plusDays(1)
                        } while (candidate.dayOfWeek != weekdayMatch.value)
                        date = candidate
                        text = removePhrase(text, weekdayMatch.key)
                    } else if (lowerText.trim().split(" ").size > 6) {
                        confident = false
                    }
                }
            }
        }

        // --- Time ---
        val timeRegex = Regex(
            """a\s+las\s+(\d{1,2})(?::(\d{2}))?\s*(de\s+la\s+(mañana|manana|tarde|noche))?""",
            RegexOption.IGNORE_CASE
        )

        val match = timeRegex.find(text)
        if (match != null) {
            var hour = match.groupValues[1].toIntOrNull() ?: 0
            val minute = match.groupValues[2].toIntOrNull() ?: 0
            val period = match.groupValues[4].lowercase(Locale.getDefault())

            if ((period == "tarde" || period == "noche") && hour in 1..11) {
                hour += 12
            }
            if ((period == "mañana" || period == "manana") && hour == 12) {
                hour = 0
            }

            if (hour in 0..23 && minute in 0..59) {
                time = LocalTime.of(hour, minute)
                text = text.replace(match.value, "").trim()
            }
        } else {
            val plainTime = Regex("""\b(\d{1,2}):(\d{2})\b""").find(text)
            if (plainTime != null) {
                val h = plainTime.groupValues[1].toIntOrNull()
                val m = plainTime.groupValues[2].toIntOrNull()
                if (h != null && m != null && h in 0..23 && m in 0..59) {
                    time = LocalTime.of(h, m)
                    text = text.replace(plainTime.value, "").trim()
                }
            }
        }

        val cleanTitle = text
            .replace(Regex("""\s{2,}"""), " ")
            .trim(',', ' ', '.')
            .replaceFirstChar { it.uppercaseChar() }

        if (cleanTitle.isBlank()) confident = false

        val wordCount = rawText.trim().split(" ").size
        if (wordCount > 16 || rawText.contains(" y ", ignoreCase = true)) {
            confident = false
        }

        return ParsedTask(
            title = cleanTitle.ifBlank { rawText.trim() },
            description = rawText.trim(),
            date = date,
            time = time,
            priority = priority,
            isConfident = confident
        )
    }

    private fun removePhrase(text: String, vararg phrases: String): String {
        var result = text
        for (phrase in phrases) {
            result = result.replace(Regex(Regex.escape(phrase), RegexOption.IGNORE_CASE), "")
        }
        return result.replace(Regex("""\s{2,}"""), " ").trim()
    }
}