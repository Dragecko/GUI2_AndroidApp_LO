
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
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddingGradeScreen(){
    val context = LocalContext.current

    // --- Data "mock" a remplacer après par l'api mongoDaBé)
    val courses = remember { listOf("Français", "Mathématique", "GUI1") }
    val semesters = remember { listOf("Aucun semestre", "Semestre 1", "Semestre 2") }

    // --- UI State
    var selectedCourse by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("tests") }
    var note by remember { mutableStateOf("4.0") }
    var weight by remember { mutableStateOf("1.0") }
    var selectedSemester1 by remember { mutableStateOf("Aucun semestre") }
    var selectedSemester2 by remember { mutableStateOf("Aucun semestre") }
    var dateText by remember { mutableStateOf("24 / 11 / 2025") }

    // --- Menu
    var courseMenuExpanded by remember { mutableStateOf(false) }
    var semester1MenuExpanded by remember { mutableStateOf(false) }
    var semester2MenuExpanded by remember { mutableStateOf(false) }

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
            value = selectedSemester1,
            placeholder = null,
            expanded = semester1MenuExpanded,
            onExpandedChange = { semester1MenuExpanded = !semester1MenuExpanded },
            options = semesters,
            onSelect = { sem ->
                selectedSemester1 = sem
                semester1MenuExpanded = false
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

        Spacer(Modifier.height(16.dp))

        DropdownField(
            label = "Semestre (optionnel)",
            value = selectedSemester2,
            placeholder = null,
            expanded = semester2MenuExpanded,
            onExpandedChange = { semester2MenuExpanded = !semester2MenuExpanded },
            options = semesters,
            onSelect = { sem ->
                selectedSemester2 = sem
                semester2MenuExpanded = false
            }
        )

        Spacer(Modifier.height(24.dp))

        AddButton(
            onAdd = { /* TODO: Implémenter l'ajout de la note */ }
        )

        // Espace supplementaire en bas pour une meilleur visibilité du boutton
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

@Composable
private fun AddButton(
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onAdd) {
            Text("Ajouter")
        }
    }
}

private fun formatDate(day: Int, month: Int, year: Int): String {
    val d = day.toString().padStart(2, '0')
    val m = month.toString().padStart(2, '0')
    return "$d / $m / $year"
}