package com.example.pronote_2.ui.screen

import androidx.compose.runtime.Composable
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pronote_2.data.ApiService
import com.example.pronote_2.data.Grade
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddingGradeScreen(
    onGradeAdded: () -> Unit = {}
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- Data "mock" a remplacer après par l'api mongoDaBé)
    val courses = remember { listOf("Français", "Mathématique", "GUI1") }
    val semesters = remember { listOf("Aucun semestre", "Semestre 1", "Semestre 2") }

    // --- UI State
    var selectedCourse by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("4.0") }
    var weight by remember { mutableStateOf("1.0") }
    var selectedSemester by remember { mutableStateOf("Aucun semestre") }
    var dateText by remember { mutableStateOf(formatDate(
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
        Calendar.getInstance().get(Calendar.MONTH) + 1,
        Calendar.getInstance().get(Calendar.YEAR)
    )) }

    // --- Menu
    var courseMenuExpanded by remember { mutableStateOf(false) }
    var semesterMenuExpanded by remember { mutableStateOf(false) }

    // --- API State
    var isLoading by remember { mutableStateOf(false) }

    // DatePicker
    val openDatePicker = remember(context) {
        {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    dateText = formatDate(dayOfMonth, month + 1, year)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = "Ajouter une note",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        DropdownField(
            label = "Cours",
            value = selectedCourse,
            placeholder = "Sélectionner...",
            expanded = courseMenuExpanded,
            onExpandedChange = { courseMenuExpanded = !courseMenuExpanded },
            options = courses,
            onSelect = { option ->
                selectedCourse = option
                courseMenuExpanded = false
            }
        )

        Spacer(Modifier.height(16.dp))

        DropdownField(
            label = "Semestre (optionnel)",
            value = selectedSemester,
            placeholder = null,
            expanded = semesterMenuExpanded,
            onExpandedChange = { semesterMenuExpanded = !semesterMenuExpanded },
            options = semesters,
            onSelect = { sem ->
                selectedSemester = sem
                semesterMenuExpanded = false
            }
        )

        Spacer(Modifier.height(16.dp))

        TitleField(
            title = title,
            onTitleChange = { title = it },
            onClear = { title = "" }
        )

        Spacer(Modifier.height(16.dp))

        NoteAndWeightRow(
            note = note,
            onNoteChange = { note = it },
            weight = weight,
            onWeightChange = { weight = it }
        )

        Spacer(Modifier.height(16.dp))

        DateField(
            dateText = dateText,
            onOpenPicker = openDatePicker
        )

        Spacer(Modifier.height(24.dp))

        AddButton(
            onAdd = {
                if (selectedCourse.isEmpty()) {
                    return@AddButton
                }
                if (title.isEmpty()) {
                    return@AddButton
                }

                isLoading = true

                scope.launch {
                    try {
                        val noteValue = note.toDoubleOrNull() ?: 0.0
                        val weightValue = weight.toDoubleOrNull() ?: 1.0
                        val semester = if (selectedSemester != "Aucun semestre") selectedSemester else null

                        val isoDate = convertDateToISO(dateText)

                        val grade = Grade(
                            course = selectedCourse,
                            title = title,
                            note = noteValue,
                            weight = weightValue,
                            semester = semester,
                            date = isoDate
                        )

                        val result = ApiService.createGrade(grade)
                        result.onSuccess {
                            // Réinitialiser les champs
                            selectedCourse = ""
                            title = ""
                            note = "4.0"
                            weight = "1.0"
                            selectedSemester = "Aucun semestre"
                            dateText = formatDate(
                                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                                Calendar.getInstance().get(Calendar.MONTH) + 1,
                                Calendar.getInstance().get(Calendar.YEAR)
                            )
                            onGradeAdded()
                        }
                    } catch (e: Exception) {
                    } finally {
                        isLoading = false
                    }
                }
            },
            isLoading = isLoading
        )
        Spacer(Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    value: String,
    placeholder: String?,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    Text(text = label, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange() }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { if (placeholder != null) Text(placeholder) else null },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange() }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelect(option) }
                )
            }
        }
    }
}

@Composable
private fun TitleField(
    title: String,
    onTitleChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Text("Titre", fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))

    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            if (title.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "Effacer")
                }
            }
        }
    )
}

@Composable
private fun NoteAndWeightRow(
    note: String,
    onNoteChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text("Notes", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(Modifier.weight(1f)) {
            Text("Poids", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DateField(
    dateText: String,
    onOpenPicker: () -> Unit
) {
    Text("Date", fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))

    OutlinedTextField(
        value = dateText,
        onValueChange = { },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = onOpenPicker) {
                Icon(Icons.Default.DateRange, contentDescription = "Choisir la date")
            }
        }
    )
}

@Composable
private fun AddButton(
    onAdd: () -> Unit,
    isLoading: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAdd,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(if (isLoading) "Ajout..." else "Ajouter")
        }
    }
}

private fun formatDate(day: Int, month: Int, year: Int): String {
    val d = day.toString().padStart(2, '0')
    val m = month.toString().padStart(2, '0')
    return "$d / $m / $year"
}

private fun convertDateToISO(dateText: String): String {
    return try {
        // Format d'entrée: "dd / mm / yyyy"
        val parts = dateText.split(" / ")
        if (parts.size == 3) {
            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, day)
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.format(calendar.time)
        } else {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.format(Calendar.getInstance().time)
        }
    } catch (e: Exception) {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.format(Calendar.getInstance().time)
    }
}