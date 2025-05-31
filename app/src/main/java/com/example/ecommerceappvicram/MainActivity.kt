package com.example.ecommerceappvicram

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ecommerceappvicram.api.RetrofitInstance
import com.example.ecommerceappvicram.model.Product
import com.example.ecommerceappvicram.ui.theme.ECommerceAPPvicramTheme
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.example.ecommerceappvicram.model.CartManager
import kotlinx.coroutines.delay

var allProductsGlobal = mutableStateListOf<Product>()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ECommerceAPPvicramTheme {
                Surface(color = Color.White) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MainMenu(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenu(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Charger les produits une seule fois
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val fetched = RetrofitInstance.api.getProducts()
                allProductsGlobal.clear()
                allProductsGlobal.addAll(fetched)
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    context.startActivity(Intent(context, SearchActivity::class.java))
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Rechercher", tint = Color.White)
            }

            IconButton(
                onClick = {
                    context.startActivity(Intent(context, QRCodeScannerActivity::class.java))
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Scanner", tint = Color.White)
            }

            IconButton(
                onClick = {
                    context.startActivity(Intent(context, CartActivity::class.java))
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Panier", tint = Color.White)
            }

            IconButton(
                onClick = {
                    context.startActivity(Intent(context, FavoritesActivity::class.java))
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Favoris", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allProductsGlobal) { product ->
                ProductCard(product = product, context = context)
            }
        }
    }
}


@Composable
fun ProductCard(
    product: Product,
    context: android.content.Context,
    onFavoriteToggle: (() -> Unit)? = null // Ajouté ici
) {
    var isFavorite by remember { mutableStateOf(product.isFavorite) }
    val scope = rememberCoroutineScope()
    var isAddedToCart by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("productId", product.id)
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.title,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            Text(
                text = "${product.price} €",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(
                onClick = {
                    isFavorite = !isFavorite
                    product.isFavorite = isFavorite
                    onFavoriteToggle?.invoke() // ← Appelé pour forcer la mise à jour si fourni
                }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favori",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
            Button(
                onClick = {
                    CartManager.addToCart(product)
                    isAddedToCart = true
                    scope.launch {
                        delay(1500)
                        isAddedToCart = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Text("Ajouter au panier")
            }

            AnimatedVisibility(
                visible = isAddedToCart,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "✅ Ajouté au panier !",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}


