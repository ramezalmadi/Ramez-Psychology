package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookChapter
import com.example.data.model.CaseStudy
import com.example.data.repository.BookRepository
import com.example.ui.theme.GoldMedal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EbookScreen() {
    var selectedChapterId by remember { mutableStateOf(1) }
    var searchQuery by remember { mutableStateOf("") }
    var bookmarkedCaseNumbers by remember { mutableStateOf(setOf<Int>()) }
    var showOnlyBookmarks by remember { mutableStateOf(false) }

    val chapters = BookRepository.chapters
    val allCases = BookRepository.sampleCases

    val filteredCases = remember(selectedChapterId, searchQuery, showOnlyBookmarks, bookmarkedCaseNumbers) {
        allCases.filter { case ->
            val matchesChapter = case.chapterId == selectedChapterId || searchQuery.isNotBlank()
            val matchesQuery = searchQuery.isBlank() ||
                    case.question.contains(searchQuery, ignoreCase = true) ||
                    case.behavioralAnalysis.contains(searchQuery, ignoreCase = true) ||
                    case.practicalResponse.contains(searchQuery, ignoreCase = true) ||
                    case.psychologicalPrinciple.contains(searchQuery, ignoreCase = true)
            val matchesBookmark = !showOnlyBookmarks || bookmarkedCaseNumbers.contains(case.number)
            matchesChapter && matchesQuery && matchesBookmark
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("ebook_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "علم النفس السلوكي",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "الكتاب الإلكتروني الكامل • د. رامز",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showOnlyBookmarks = !showOnlyBookmarks }) {
                        Icon(
                            imageVector = if (showOnlyBookmarks) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "المحفوظات",
                            tint = if (showOnlyBookmarks) GoldMedal else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Book Hero Cover Banner
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0D1B2A)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GoldMedal.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = GoldMedal,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = BookRepository.bookTitle,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = BookRepository.bookSubtitle,
                            fontSize = 14.sp,
                            color = GoldMedal,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "R A M E Z",
                            fontSize = 13.sp,
                            letterSpacing = 4.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "”${BookRepository.motto}“",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Search Field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث في أسئلة وحالات علم النفس السلوكي...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "مسح")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("ebook_search_input"),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            // Chapters Selector Row
            item {
                Text(
                    text = "فهرس فصول الكتاب الأربعة:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    chapters.forEach { chapter ->
                        FilterChip(
                            selected = selectedChapterId == chapter.id && searchQuery.isBlank(),
                            onClick = {
                                selectedChapterId = chapter.id
                                searchQuery = ""
                            },
                            label = { Text("الفصل ${chapter.id}", fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Selected Chapter Details Card
            if (searchQuery.isBlank()) {
                val currentChapter = chapters.find { it.id == selectedChapterId }
                if (currentChapter != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = currentChapter.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = currentChapter.subtitle + " • الحالات (${currentChapter.caseRange})",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = currentChapter.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }

            // Case Studies List
            items(filteredCases, key = { it.number }) { caseItem ->
                val isBookmarked = bookmarkedCaseNumbers.contains(caseItem.number)
                CaseStudyCard(
                    caseStudy = caseItem,
                    isBookmarked = isBookmarked,
                    onToggleBookmark = {
                        bookmarkedCaseNumbers = if (isBookmarked) {
                            bookmarkedCaseNumbers - caseItem.number
                        } else {
                            bookmarkedCaseNumbers + caseItem.number
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun CaseStudyCard(
    caseStudy: CaseStudy,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("case_study_${caseStudy.number}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${caseStudy.number}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = caseStudy.question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }

                IconButton(onClick = onToggleBookmark, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "حفظ",
                        tint = if (isBookmarked) GoldMedal else MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Always show Psychological Principle as a highlighted chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("◆", color = GoldMedal, fontSize = 12.sp)
                    Text(
                        text = "المبدأ النفسي: ${caseStudy.psychologicalPrinciple}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Behavioral Analysis
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Text("التحليل السلوكي", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                        Text(
                            text = caseStudy.behavioralAnalysis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Practical Response
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Text("الاستجابة العملية", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                        Text(
                            text = caseStudy.practicalResponse,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "إخفاء التفاصيل" else "قراءة التحليل والاستجابة العملية",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
