package com.example.geoquiz

import android.R.attr.onClick
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geoquiz.ui.theme.GeoQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeoQuizTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GeoQuizScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GeoQuizPreview() {
    GeoQuizTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            GeoQuizScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeoQuizScreen(modifier: Modifier, viewModel: GeoQuizViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column() {
        TopAppBar(
            title = { Text("GeoQuiz", fontSize = 22.sp) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF3D35B1),
                titleContentColor = Color.White,
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier
                .padding(vertical = 15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = state.questions[state.currentIndex].text,
                textAlign = TextAlign.Center,
                color = Color(0xFF4F4F4F)
            )
        }

        if (!state.questionAnswered) {
            Row(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.onAnswer(true) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xFFFFFFFF),
                        containerColor = Color(0xFF3D35B1)
                    )
                ) { Text("TRUE", fontSize = 13.sp) }
                Button(
                    onClick = { viewModel.onAnswer(false) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xFFFFFFFF),
                        containerColor = Color(0xFF3D35B1)
                    )
                ) { Text("FALSE", fontSize = 13.sp) }
            }
        }

        if (state.questionAnswered && state.currentIndex < state.questions.lastIndex) {
            Row(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { viewModel.onNextQuestion() },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xFFFFFFFF),
                        containerColor = Color(0xFF3D35B1)
                    )
                ) { Text("NEXT >", fontSize = 13.sp) }
            }
        }

        if (state.questionAnswered && state.currentIndex == state.questions.lastIndex) {
            Button(
                onClick = { viewModel.onNextQuestion() },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(10.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color(0xFFFFFFFF),
                    containerColor = Color(0xFF3D35B1)
                )
            ) {
                Text("Показать результат")
            }
        }

        if (state.showResultDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissResultDialog() },
                title = { Text("Результат") },
                text = { Text(viewModel.resultText) },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissResultDialog() }) {
                        Text("OK")
                    }
                }
            )
        }

    }
}