package com.example.photogallery

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import coil.compose.AsyncImage
import com.example.photogallery.api.UnsplashPhoto
import com.example.photogallery.api.api
import com.example.photogallery.db.AppDatabase
import com.example.photogallery.db.FavoritePhoto
import com.example.photogallery.ui.theme.PhotoGalleryTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


const val API_KEY = "FlsssTXWRFadgsDvbPgtlmV82XNMEPSMyLZZdHIsXkw"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoGalleryTheme {
                PhotoGalleryScreen()
            }
        }
    }
}

class PhotoGalleryViewModel (application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, AppDatabase::class.java, "photos.db").build()
    private val dao = db.favoritePhotoDao()

    private val _photos = MutableStateFlow<List<UnsplashPhoto>>(emptyList())
    val photos: StateFlow<List<UnsplashPhoto>> = _photos
    val favoritePhotos = dao.getAll()

    init {
        loadPhotos()
    }

    private fun loadPhotos(){
        viewModelScope.launch {
            try {
                _photos.value = api.getPhotos(API_KEY)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun search(query: String){
        if (query.isBlank()) return
        viewModelScope.launch {
            try {
                _photos.value = api.searchPhotos(query, API_KEY).results
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveToFavorites(photo: UnsplashPhoto) {
        viewModelScope.launch {
            dao.insert(FavoritePhoto(id = photo.id, url = photo.urls.small))
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(viewModel: PhotoGalleryViewModel = viewModel()) {
    val photos by viewModel.photos.collectAsState()
    val favorites by viewModel.favoritePhotos.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf(false) }
    var showFavorites by remember { mutableStateOf(false) } // Переключатель режимов

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PhotoGallery") },
                actions = {
                    // Поле поиска
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Поиск...") },
                        modifier = Modifier.width(180.dp).padding(end = 8.dp),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.search(searchQuery)
                                showFavorites = false // При поиске возвращаемся в режим сети
                            }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    )

                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (showFavorites) "Показать сеть" else "Показать избранное") },
                            onClick = {
                                showFavorites = !showFavorites
                                expandedMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Удалить все из БД") },
                            onClick = {
                                viewModel.clearFavorites()
                                expandedMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.LightGray)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (showFavorites) {
                    items(favorites) { favPhoto ->
                        AsyncImage(
                            model = favPhoto.url,
                            contentDescription = "Favorite Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.padding(4.dp).aspectRatio(1f)
                        )
                    }
                } else {
                    items(photos) { photo ->
                        AsyncImage(
                            model = photo.urls.small,
                            contentDescription = "Net Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .clickable { viewModel.saveToFavorites(photo) }
                        )
                    }
                }
            }
        }
    }
}