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
fun EditingGradeScreen(
    onGradeUpdated: () -> Unit = {}
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- Data
    val semesters = remember { listOf("Aucun", "Semestre 1", "Semestre 2") }

    // --- API State
    var grades by remember { mutableStateOf<List<Grade>>(emptyList()) }
    var selectedGradeId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingGrades by remember { mutableStateOf(false) }

    // --- UI State
    var selectedTest by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("4.0") }
    var weight by remember { mutableStateOf("1.0") }
    var selectedSemester by remember { mutableStateOf("Aucun") }
    var dateText by remember { mutableStateOf("24 / 11 / 2025") }

    // --- Menu
    var testMenuExpanded by remember { mutableStateOf(false) }
    var semesterMenuExpanded by remember { mutableStateOf(false) }

    // Charger les notes au démarrage
    LaunchedEffect(Unit) {
        isLoadingGrades = true
        ApiService.getAllGrades().onSuccess {
            grades = it
            isLoadingGrades = false
        }.onFailure {
            isLoadingGrades = false
        }
    }

    // Mettre à jour les champs quand une note est sélectionnée
    LaunchedEffect(selectedGradeId) {
        selectedGradeId?.let { id ->
            grades.find { it._id == id }?.let { grade ->
                selectedTest = grade.course
                title = grade.title
                note = grade.note.toString()
                weight = grade.weight.toString()
                selectedSemester = grade.semester ?: "Aucun"
                // Convertir la date ISO en format affichage
                dateText = convertISOToDate(grade.date)
            }
        }
    }

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
            text = "Modifier une note",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Dropdown (selectionner une note)
        if (isLoadingGrades) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            GradeSelectionDropdown(
                label = "Sélectionner une note a modifier",
                grades = grades,
                selectedGradeId = selectedGradeId,
                onGradeSelected = { id ->
                    selectedGradeId = id
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        // Champ cours (lecture seule, basé sur la note sélectionnée)
        Text("Cours", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = selectedTest,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedGradeId != null
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

        DateField(
            dateText = dateText,
            onOpenPicker = openDatePicker
        )

        Spacer(Modifier.height(24.dp))

        ActionsRow(
            onCancel = {
                selectedGradeId = null
                selectedTest = ""
                title = ""
                note = "4.0"
                weight = "1.0"
                selectedSemester = "Aucun"
            },
            onSave = {
                if (selectedGradeId == null) {
                    return@ActionsRow
                }
                if (title.isEmpty()) {
                    return@ActionsRow
                }

                isLoading = true

                scope.launch {
                    try {
                        val noteValue = note.toDoubleOrNull() ?: 0.0
                        val weightValue = weight.toDoubleOrNull() ?: 1.0
                        val semester = if (selectedSemester != "Aucun") selectedSemester else null
                        val isoDate = convertDateToISO(dateText)

                        val grade = Grade(
                            course = selectedTest,
                            title = title,
                            note = noteValue,
                            weight = weightValue,
                            semester = semester,
                            date = isoDate
                        )

                        val result = ApiService.updateGrade(selectedGradeId!!, grade)
                        result.onSuccess {
                            // Recharger la liste des notes
                            ApiService.getAllGrades().onSuccess { updatedGrades ->
                                grades = updatedGrades
                            }
                            onGradeUpdated()
                        }
                    } catch (e: Exception) {
                    } finally {
                        isLoading = false
                    }
                }
            },
            isLoading = isLoading,
            enabled = selectedGradeId != null
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
            IconButton(onClick = onClear) {
                Icon(Icons.Default.Clear, contentDescription = "Effacer")
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
        onValueChange = { /* date affichée seulement */ },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = onOpenPicker) {
                Icon(Icons.Default.DateRange, contentDescription = "Choisir la date")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradeSelectionDropdown(
    label: String,
    grades: List<Grade>,
    selectedGradeId: String?,
    onGradeSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Text(text = label, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = grades.find { it._id == selectedGradeId }?.let { "${it.course} - ${it.title}" } ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Sélectionner une note...") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (grades.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Aucune note disponible") },
                    onClick = { expanded = false }
                )
            } else {
                grades.forEach { grade ->
                    DropdownMenuItem(
                        text = { Text("${grade.course} - ${grade.title} (${grade.note})") },
                        onClick = {
                            onGradeSelected(grade._id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionsRow(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onCancel,
            enabled = !isLoading && enabled
        ) {
            Text("Annuler")
        }
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = onSave,
            enabled = !isLoading && enabled
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(if (isLoading) "Modification..." else "Modifier")
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

private fun convertISOToDate(isoDate: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = sdf.parse(isoDate)
        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            formatDate(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
            )
        } else {
            formatDate(
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.YEAR)
            )
        }
    } catch (e: Exception) {
        formatDate(
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
            Calendar.getInstance().get(Calendar.MONTH) + 1,
            Calendar.getInstance().get(Calendar.YEAR)
        )
    }
}