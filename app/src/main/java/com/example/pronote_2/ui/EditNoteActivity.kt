package com.example.pronote_2.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pronote_2.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen() {
    val context = LocalContext.current

    var selectedTest by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("Selecte") }
    var note by remember { mutableStateOf("4.0") }
    var weight by remember { mutableStateOf("1.0") }
    var selectedSemester by remember { mutableStateOf("Aucun") }
    var dateText by remember { mutableStateOf("24 / 11 / 2025") }

    val tests = listOf("Test 1", "Test 2")
    val semesters = listOf("Aucun", "Semestre 1", "Semestre 2")

    val calendar = Calendar.getInstance()

    val openDatePicker = {
        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val day = dayOfMonth.toString().padStart(2, '0')
                val m = (month + 1).toString().padStart(2, '0')
                dateText = "$day / $m / $year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {  },
                    icon = { },
                    label = { Text("Cours") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {  },
                    icon = { },
                    label = { Text("Ajouter une note") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { },
                    label = { Text("Paramètres") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "ProNote 2",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Modifier une note - Anglais",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Sélectionner le test",
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))

            var testMenuExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = testMenuExpanded,
                onExpandedChange = { testMenuExpanded = !testMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedTest,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sélectionner…") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = testMenuExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = testMenuExpanded,
                    onDismissRequest = { testMenuExpanded = false }
                ) {
                    tests.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedTest = option
                                title = option
                                testMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Titre", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { title = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Effacer")
                    }
                }
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Notes", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text("Poids", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Semestre (optionnel)", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))

            var semesterExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = semesterExpanded,
                onExpandedChange = { semesterExpanded = !semesterExpanded }
            ) {
                OutlinedTextField(
                    value = selectedSemester,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = semesterExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = semesterExpanded,
                    onDismissRequest = { semesterExpanded = false }
                ) {
                    semesters.forEach { sem ->
                        DropdownMenuItem(
                            text = { Text(sem) },
                            onClick = {
                                selectedSemester = sem
                                semesterExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Date", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = dateText,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                readOnly = false,
                trailingIcon = {
                    IconButton(onClick = { openDatePicker() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Choisir la date")
                    }
                }
            )

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = { }) {
                    Text("Annuler")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                }) {
                    Text("Modifier")
                }
            }
        }
    }
}

