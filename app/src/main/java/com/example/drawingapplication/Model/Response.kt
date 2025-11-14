package com.example.drawingapplication.Model

import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

// Gemini helped define all of the property names for the JSON objects

@Serializable
data class VisionApiResponse(val responses: List<Response>)

@Serializable
data class Response(
//    val labelAnnotations: List<LabelAnnotation>? = null,
    val localizedObjectAnnotations: List<LocalizedObjectAnnotation>? = null
)

//@Serializable
//data class LabelAnnotation(
//    val description: String,
//    val score: Double
//)

@Serializable
data class LocalizedObjectAnnotation(
    val name: String,
    val score: Double,
    val boundingPoly: BoundingPoly
)

@Serializable
data class BoundingPoly(
    val normalizedVertices: List<NormalizedVertex>
)

@Serializable
data class NormalizedVertex(
    val x: Double? = null,
    val y: Double? = null
)

data class ImageStats(val labels: Map<String, Pair<Double, BoundingPoly>>)