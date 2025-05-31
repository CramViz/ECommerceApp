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
import com.example.ecommerceappvicram.model.CartManager
import com.example.ecommerceappvicram.model.Product
import com.example.ecommerceappvicram.ui.theme.ECommerceAPPvicramTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api


class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ECommerceAPPvicramTheme {
                @OptIn(ExperimentalMaterial3Api::class)
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Panier") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    CartScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CartScreen(modifier: Modifier = Modifier) {
    var cartItems by remember { mutableStateOf(CartManager.getCart().toMutableList()) }
    var showSnackbar by remember { mutableStateOf(false) }

    // Total recalculé automatiquement à chaque recomposition
    val total = cartItems.sumOf { it.price }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar("✅ Paiement validé")
            showSnackbar = false
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "🛒 Panier",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (cartItems.isEmpty()) {
                Text("Le panier est vide.")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(cartItems) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(product.image),
                                    contentDescription = product.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(end = 8.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.title, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${product.price} €", color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            CartManager.removeFromCart(product)
                                            cartItems = CartManager.getCart().toMutableList()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("❌ Supprimer")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Total : %.2f €".format(total),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.End)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        CartManager.clearCart()
                        cartItems = CartManager.getCart().toMutableList()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🗑️ Vider le panier")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        showSnackbar = true
                        CartManager.clearCart()
                        cartItems = CartManager.getCart().toMutableList()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("💳 Payer")
                }
            }
        }
    }
}


