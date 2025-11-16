package com.example.drawingapplication.Model

import kotlinx.serialization.Serializable

// Gemini helped define all of the property names for the JSON objects

@Serializable
data class VisionApiResponse(val responses: List<Response>)

@Serializable
data class Response(
//    val labelAnnotations: List<LabelAnnotation>? = null,
    val localizedObjectAnnotations: List<LocalizedObjectAnnotation>? = null,
    val labelAnnotations: List<LabelAnnotation>? = null
)

@Serializable
data class LabelAnnotation(
    val description: String,
    val score: Double
)

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
    val x: Float = -1f,
    val y: Float = -1f
)

data class ImageStats(val labelsNoLoc: List<LabelAnnotation> = emptyList(), val labels: List<LocalizedObjectAnnotation> = emptyList())