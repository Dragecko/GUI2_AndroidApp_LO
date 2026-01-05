package com.example.pronote_2.data

import kotlinx.serialization.Serializable

@Serializable
data class Grade(
    val _id: String? = null,
    val course: String,
    val title: String,
    val note: Double,
    val weight: Double,
    val semester: String? = null,
    val date: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class GradeResponse(
    val success: Boolean,
    val message: String? = null,
    val data: Grade? = null,
    val count: Int? = null
)

@Serializable
data class GradesListResponse(
    val success: Boolean,
    val count: Int,
    val data: List<Grade>
)

