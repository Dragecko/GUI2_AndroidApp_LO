package com.example.pronote_2.ui.screen

import android.os.Build
import kotlin.collections.mapIndexed
import kotlin.collections.groupBy
import androidx.compose.runtime.derivedStateOf
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pronote_2.R
import com.example.pronote_2.data.ApiService
import com.example.pronote_2.data.Grade
import com.example.pronote_2.ui.AddingGrade
import com.example.pronote_2.ui.EditingGrade
import com.example.pronote_2.ui.Main
import com.example.pronote_2.ui.ProNote2NavHost

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onEditGradeClick: () -> Unit){
    data class CarouselItem(
        val id: Int,
        val grade: Float,
        val title: String,
    )


    /*val items =
        mutableListOf(
            CarouselItem(1, 5.0f, "donut"),
            CarouselItem(1, 3.0f,"donut"),
            CarouselItem(1, 4.5f,"donut"),
            CarouselItem(1, 6.0f,"donut"),
            CarouselItem(1, 2.5f,"donut"),
        )

     */

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    LaunchedEffect(Unit) {
        isLoadingGrades = true
        ApiService.getAllGrades().onSuccess {
            grades = it
            isLoadingGrades = false
        }.onFailure {
            isLoadingGrades = false
        }
    }

    val carouselItems by remember {
        derivedStateOf {
            grades
                .groupBy { it.course }
                .entries
                .mapIndexed { index, entry ->
                    val course = entry.key
                    val courseGrades = entry.value

                    val average = courseGrades
                        .map { it.note.toFloat() }
                        .average()
                        .toFloat()

                    CarouselItem(
                        id = index,
                        grade = average,
                        title = course
                    )
                }
        }
    }

    Column(){
        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState {carouselItems.count()},
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 16.dp, bottom = 16.dp)
                .align(alignment = Alignment.CenterHorizontally),
            preferredItemWidth = 150.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { index ->
            val item = carouselItems[index]
            Block(grade = item.grade, modifier = Modifier.maskClip(MaterialTheme.shapes.extraLarge).fillMaxWidth().align(Alignment.CenterHorizontally), item.title)
        }

        TitleSection("Dernières notes")

        SimpleLazyColumn(
            onEditGradeClick = onEditGradeClick,
            grades = ArrayList(grades),
            onDeleteGrade = { gradeId ->
                scope.launch {
                    isLoading = true
                    ApiService.deleteGrade(gradeId).onSuccess {
                        // Rafraîchir la liste après suppression
                        ApiService.getAllGrades().onSuccess {
                            grades = it
                            Toast.makeText(context, "Note supprimée avec succès", Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            Toast.makeText(context, "Erreur lors du rafraîchissement", Toast.LENGTH_SHORT).show()
                        }
                    }.onFailure { error ->
                        Toast.makeText(context, "Erreur: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                }
            }
        )

    }
}

@Composable
fun Block(grade: Float, modifier: Modifier = Modifier, module: String?){

    Box(
        modifier = if (grade >= 5.0f) {
            modifier
                //.padding(8.dp)
                .height(120.dp)
                .background(
                    Brush.verticalGradient(0f to Color(0xFF008004), 1000f to Color(0x9900D30E)),
                    shape = RoundedCornerShape(12.dp)
                )
        } else if(grade < 5.0f && grade >= 4.0f) {
            modifier
                //.padding(8.dp)
                .height(120.dp)
                .background(
                    Brush.verticalGradient(0f to Color(0xFF488300), 1000f to Color(0x9A44FF00)),
                    shape = RoundedCornerShape(12.dp)
                )
        } else {
            modifier
                //.padding(16.dp)
                .height(120.dp)
                .background(
                    Brush.verticalGradient(0f to Color(0xFF770000), 1000f to Color(0x99FF0000)),
                    shape = RoundedCornerShape(12.dp)
                )
        },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (module != null){
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .widthIn(max = 120.dp)
                ) {
                    Text(
                        text = module,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        fontSize = 15.sp,
                        color = Color.White

                    )
                }
            }
            Text(
                text = grade.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 25.sp,
                color = Color.White
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AverageGradeList(
    onEditGradeClick: () -> Unit,
    grade: Grade,
    onDeleteGrade: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Absolute.Right,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row {
            Row(
                horizontalArrangement = Arrangement.spacedBy(25.dp),
                modifier = Modifier
                    .padding(vertical = 10.dp)
            ) {
                Block(grade.note.toFloat(), modifier = Modifier.size(75.dp), null)
                Column(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .widthIn(min = 240.dp, max = 240.dp)
                ) {
                    Text(
                        text = grade.course,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = grade.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text("Le " + convertISOToDate(grade.date))
                }
            }

            MinimalDropdownMenu(
                onEditGradeClick = onEditGradeClick,
                onDeleteGradeClick = {
                    grade._id?.let { onDeleteGrade(it) }
                }
            )
        }
    }

    HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 10.dp))
}

@Composable
fun TitleSection(title: String){
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Left,
        modifier = Modifier.padding(15.dp,50.dp, 0.dp, 15.dp)
    )
}

@Composable
fun MinimalDropdownMenu(
    onEditGradeClick: () -> Unit,
    onDeleteGradeClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Modifier") },
                onClick = {
                    expanded = false
                    onEditGradeClick()
                }
            )
            DropdownMenuItem(
                text = { Text("Supprimer") },
                onClick = {
                    expanded = false
                    onDeleteGradeClick()
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun SimpleLazyColumn(
    onEditGradeClick: () -> Unit,
    grades: ArrayList<Grade>,
    onDeleteGrade: (String) -> Unit
) {
    LazyColumn() {
        items(grades.count()) { index ->
            if (index < grades.size) {
                AverageGradeList(
                    onEditGradeClick = onEditGradeClick,
                    grade = grades[index],
                    onDeleteGrade = onDeleteGrade
                )
            }
        }
    }
}