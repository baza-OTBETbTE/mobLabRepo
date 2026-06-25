package com.example.photogallery

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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

    fun toggleFavorite(photoId: String, url: String, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                dao.deleteById(photoId)
            } else {
                dao.insert(FavoritePhoto(id = photoId, url = url))
            }
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PhotoGalleryScreen(viewModel: PhotoGalleryViewModel = viewModel()) {
    val photos by viewModel.photos.collectAsState()
    val favorites by viewModel.favoritePhotos.collectAsState(initial = emptyList())

    val favoriteIds = remember(favorites) { favorites.map { it.id }.toSet() }

    var searchQuery by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf(false) }
    var showFavorites by remember { mutableStateOf(false) }

    var enlargedPhotoData by remember { mutableStateOf<Pair<String, String>?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PhotoGallery") },
                actions = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Поиск...") },
                        modifier = Modifier.width(180.dp).padding(end = 8.dp),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.search(searchQuery)
                                showFavorites = false
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
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .combinedClickable(
                                    onClick = { viewModel.toggleFavorite(favPhoto.id, favPhoto.url, true) },
                                    onLongClick = { enlargedPhotoData = Pair(favPhoto.id, favPhoto.url) }
                                )
                        ) {
                            AsyncImage(
                                model = favPhoto.url,
                                contentDescription = "Favorite Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Liked",
                                tint = Color.Red,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(28.dp)
                                    .background(Color.Black.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
                                    .padding(4.dp)
                            )
                        }
                    }
                } else {
                    items(photos) { photo ->
                        val isLiked = favoriteIds.contains(photo.id)

                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .combinedClickable(
                                    onClick = { viewModel.toggleFavorite(photo.id, photo.urls.small, isLiked) },
                                    onLongClick = { enlargedPhotoData = Pair(photo.id, photo.urls.small) }
                                )
                        ) {
                            AsyncImage(
                                model = photo.urls.small,
                                contentDescription = "Net Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like Status",
                                tint = if (isLiked) Color.Red else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(28.dp)
                                    .background(Color.Black.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }

            enlargedPhotoData?.let { (photoId, photoUrl) ->
                val isLiked = favoriteIds.contains(photoId)

                Dialog(onDismissRequest = { enlargedPhotoData = null }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Enlarged Photo",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.toggleFavorite(photoId, photoUrl, isLiked)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isLiked) Color.Red.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(if (isLiked) "Убрать" else "В избранное")
                                }

                                OutlinedButton(onClick = { enlargedPhotoData = null }) {
                                    Text("Назад")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}