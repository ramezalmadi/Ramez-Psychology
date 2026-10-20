package com.example.data.model

data class CaseStudy(
    val number: Int,
    val question: String,
    val behavioralAnalysis: String,
    val practicalResponse: String,
    val psychologicalPrinciple: String,
    val chapterId: Int
)

data class BookChapter(
    val id: Int,
    val title: String,
    val subtitle: String,
    val caseRange: String,
    val description: String
)
