package com.example.sp2.ai.schema

import com.google.mlkit.genai.schema.annotations.Generable
import com.google.mlkit.genai.schema.annotations.Guide

@Generable("Structured task extracted from a spoken instruction")
data class TaskExtractionResult(

    @Guide("Short, clear task title (a few words), in the same language as the instruction. Never include dates, times, or priority wording inside the title.")
    val title: String,

    @Guide("A brief one-sentence description capturing any relevant detail from the instruction beyond the title.")
    val description: String,

    @Guide("The date in ISO format YYYY-MM-DD, resolved using the current date given in the prompt — including relative expressions like 'in two days' or 'next Friday'. Empty string if no date was mentioned.")
    val date: String,

    @Guide("The time in 24-hour HH:mm format. Empty string if no time was mentioned.")
    val time: String,

    @Guide("Priority implied by the instruction's wording and tone (e.g. words like 'important' or 'urgent' imply HIGH).", enumValues = ["NONE", "LOW", "MEDIUM", "HIGH"])
    val priority: String
)