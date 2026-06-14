package com.example.roomdemo

import MainViewModel
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomdemo.ui.theme.RoomDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val owner = LocalViewModelStoreOwner.current
                    owner?.let {
                        val viewModel: MainViewModel = viewModel(
                            it,
                            "MainViewModel",
                            MainViewModelFactory(
                                LocalContext.current.applicationContext
                                        as Application
                            )
                        )
                        ScreenSetup(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = viewModel
                        )
                    }
                }
            }
        }

    }

    @Composable
    fun ScreenSetup(modifier: Modifier = Modifier, viewModel: MainViewModel) {
        val allProducts by viewModel.allProducts.observeAsState(listOf())
        val searchResults by viewModel.searchResults.observeAsState(listOf())
        MainScreen(
            modifier = modifier,
            allProducts = allProducts,
            searchResults = searchResults,
            viewModel = viewModel
        )
    }

    @Composable
    fun MainScreen(
        modifier: Modifier = Modifier,
        allProducts: List<Product>,
        searchResults: List<Product>,
        viewModel: MainViewModel
    ) {
    }

    @Composable
    fun TitleRow(head1: String, head2: String, head3: String) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Text(
                head1, color = Color.White,
                modifier = Modifier
                    .weight(0.1f)
            )
            Text(
                head2, color = Color.White,
                modifier = Modifier
                    .weight(0.2f)
            )
            Text(
                head3, color = Color.White,
                modifier = Modifier.weight(0.2f)
            )
        }
    }

    @Composable
    fun ProductRow(id: Int, name: String, quantity: Int) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Text(
                id.toString(), modifier = Modifier
                    .weight(0.1f)
            )
            Text(name, modifier = Modifier.weight(0.2f))
            Text(quantity.toString(), modifier = Modifier.weight(0.2f))
        }
    }

    @Composable
    fun CustomTextField(
        title: String,
        textState: String,
        onTextChange: (String) -> Unit,
        keyboardType: KeyboardType
    ) {
        OutlinedTextField(
            value = textState,
            onValueChange = { onTextChange(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            singleLine = true,
            label = { Text(title) },
            modifier = Modifier.padding(10.dp),
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
        )
    }

    class MainViewModelFactory(val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }
}