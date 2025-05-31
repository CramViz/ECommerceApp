package com.example.ecommerceappvicram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ecommerceappvicram.api.RetrofitInstance
import com.example.ecommerceappvicram.model.Product
import com.example.ecommerceappvicram.ui.theme.ECommerceAPPvicramTheme
import kotlinx.coroutines.launch
import com.example.ecommerceappvicram.model.CartManager
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.Color




class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ECommerceAPPvicramTheme {
                val context = this

                @OptIn(ExperimentalMaterial3Api::class)
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Recherche") },
                            navigationIcon = {
                                IconButton(onClick = { context.finish() }) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    SearchScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    val allProducts = remember { mutableStateListOf<Product>() }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var refreshTrigger by remember { mutableStateOf(false) }

    // Chargement initial des produits
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val fetched = RetrofitInstance.api.getProducts()
                allProducts.clear()
                allProducts.addAll(fetched)
            } catch (e: Exception) {
                error = "Erreur de chargement : ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Filtrage dynamique
    val filteredProducts = allProducts.filter {
        it.title.contains(query, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Recherche d'article") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(filteredProducts) { product ->
                    var isFavorite by remember { mutableStateOf(product.isFavorite) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                val intent = Intent(context, ProductDetailActivity::class.java)
                                intent.putExtra("productId", product.id)
                                context.startActivity(intent)
                            },
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = product.image),
                                contentDescription = product.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(end = 8.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = product.title, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${product.price} €",
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        CartManager.addToCart(product)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ajouter au panier")
                                }
                                Spacer(modifier = Modifier.height(4.dp))

                                IconButton(onClick = {
                                    isFavorite = !isFavorite
                                    product.isFavorite = isFavorite  // synchroniser si besoin
                                }) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favori",
                                        tint = if (isFavorite) Color.Red else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}