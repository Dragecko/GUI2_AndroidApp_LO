package com.example.pronote_2

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pronote_2.ui.theme.ProNote_2Theme

import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.rememberNavController
import com.example.pronote_2.ui.AddingGrade
import com.example.pronote_2.ui.Main
import com.example.pronote_2.ui.Parameters
import com.example.pronote_2.ui.ProNote2NavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ProNote_2Theme {
                Scaffold(topBar = {
                    ProNote2TopAppBar(name = "ProNote 2")
                }, bottomBar = {
                    ProNote2BottomAppBar(
                        onMainClick = { navController.navigate(Main) },
                        onAddingGradeClick = { navController.navigate(AddingGrade) },
                        onParametersClick = { navController.navigate(Parameters) })
                }) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)

                    ) {
                        ProNote2NavHost(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = name)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProNote_2Theme {
        Greeting("ProNote2")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProNote2TopAppBar(name: String) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                name, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = { /* do something */ }) {
                (Image(
                    painter = painterResource(R.drawable.cat_looking_good),
                    contentDescription = null
                ))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProNote2BottomAppBar(
    onMainClick: () -> Unit,
    onAddingGradeClick: () -> Unit,
    onParametersClick: () -> Unit,
) {
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarItem(selected = true, onClick = { onMainClick() }, icon = {
            Icon(
                imageVector = Icons.Filled.Home, contentDescription = null
            )
        }, label = { Text(text = "Home") })
        NavigationBarItem(selected = false, onClick = { onAddingGradeClick() }, icon = {
            Icon(
                imageVector = Icons.Filled.Add, contentDescription = null
            )
        }, label = { Text(text = "Ajouter une note") })
        NavigationBarItem(selected = false, onClick = { onParametersClick() }, icon = {
            Icon(
                imageVector = Icons.Filled.Settings, contentDescription = null
            )
        }, label = { Text(text = "Paramètres") })
    }
}

