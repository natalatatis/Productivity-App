package com.example.sp2.ai

import com.example.sp2.ai.schema.TaskExtractionResult
import com.google.mlkit.genai.prompt.GenerateContentRequest
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateTypedContentRequest
import java.time.LocalDate

// Wraps Google's on-device Gemini Nano model (via ML Kit's Prompt API).
// Every call is defensively wrapped: any failure — model not present on
// this device, quota exceeded, parsing failure — simply returns null,
// so the caller can fall back to the simple parser instead of crashing.
class GeminiNanoTaskExtractor {

    private val model by lazy { Generation.getClient() }

    suspend fun isAvailable(): Boolean {
        return try {
            model.isStructuredOutputFeatureAvailable()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun extract(instruction: String): TaskExtractionResult? {
        return try {

            val today = LocalDate.now()

            val promptText = """
                Eres un asistente que convierte instrucciones en español sobre
                tareas personales en datos estructurados.
                La fecha de hoy es $today (formato AAAA-MM-DD).
                Instrucción del usuario: "$instruction"
                Extrae:
                - Un título breve para la tarea (sin fechas, horas ni
                  palabras de prioridad dentro del título).
                - Una descripción de una frase con detalles relevantes.
                - La fecha mencionada, resuelta a partir de la fecha de hoy
                  (incluyendo expresiones como "en dos días" o "el próximo viernes").
                - La hora mencionada, si la hay.
                - La prioridad implícita en el tono ("importante", "urgente"
                  sugieren prioridad alta; si no se menciona nada, usa NONE).
            """.trimIndent()

            val baseRequest = GenerateContentRequest.Builder(TextPart(promptText)).build()

            val typedRequest = generateTypedContentRequest(
                generateContentRequest = baseRequest,
                outputClass = TaskExtractionResult::class,
                includeSchemaInPrompt = true
            )

            val response = model.generateContent(typedRequest)
            response.candidates.firstOrNull()?.response

        } catch (e: Exception) {
            null
        }
    }
}