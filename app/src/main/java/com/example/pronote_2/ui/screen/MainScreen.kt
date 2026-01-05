package com.example.pronote_2.ui.screen

import android.widget.Toast
import androidx.annotation.DrawableRes
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pronote_2.R
import org.w3c.dom.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(){
    data class CarouselItem(
        val id: Int,
        val grade: Float,
        val contentDescription: String
    )

    val items = remember {
        listOf(
            CarouselItem(1, 5.0f, "donut"),
            CarouselItem(1, 3.0f,"donut"),
            CarouselItem(1, 4.5f,"donut"),
            CarouselItem(1, 6.0f,"donut"),
            CarouselItem(1, 2.5f,"donut"),
        )
    }

    var nbrGrades = 8

    Column(){
        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState {items.count()},
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 16.dp, bottom = 16.dp),
            preferredItemWidth = 186.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { index ->
            val item = items[index]
            Block(grade = item.grade, modifier = Modifier.maskClip(MaterialTheme.shapes.extraLarge).fillMaxWidth(), "Web")
        }

        TitleSection("Moyennes")

        SimpleLazyColumn()

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
        Column() {
            if (module != null){
                Text(
                    text = module,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
            Text(
                text = grade.toString(),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }

    }
}

@Composable
fun AverageGradeList(){
    Row(
        horizontalArrangement = Arrangement.Absolute.Right,
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Row(
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(25.dp),
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .padding(end = 25.dp)
            ) {
                Block(5f, modifier = Modifier.size(75.dp), null)
                Column(
                    modifier = Modifier
                        .padding(end = 10.dp)
                ) {
                    Text("Anglais")
                    Text("Dernière note enregistrée : 5")
                    Text("Ee 17.11.2025")
                }
            }


            MinimalDropdownMenu()
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
fun MinimalDropdownMenu() {
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
                onClick = { /* Do something... */ }
            )
            DropdownMenuItem(
                text = { Text("Supprimer") },
                onClick = { /* Do something... */ }
            )
        }
    }
}

@Composable
fun SimpleLazyColumn()
{
    LazyColumn() {
        items(8)
        {
            AverageGradeList()
        }
    }
}