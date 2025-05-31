package com.example.ecommerceappvicram

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.ecommerceappvicram.model.Product
import com.example.ecommerceappvicram.ui.theme.ECommerceAPPvicramTheme
import androidx.compose.ui.Alignment


class FavoritesActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECommerceAPPvicramTheme {
                val context = this

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("❤️ Favoris") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                                }
                            }
                        )
                    }
                ) { padding ->
                    FavoriteScreen(modifier = Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
fun FavoriteScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val refreshTrigger = remember { mutableStateOf(false) }

    val favorites by remember(refreshTrigger.value) {
        derivedStateOf { allProductsGlobal.filter { it.isFavorite } }
    }

    if (favorites.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aucun article en favori.")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favorites) { product ->
                ProductCard(
                    product = product,
                    context = context,
                    onFavoriteToggle = {
                        refreshTrigger.value = !refreshTrigger.value // ← Mise à jour visuelle
                    }
                )
            }
        }
    }
}
