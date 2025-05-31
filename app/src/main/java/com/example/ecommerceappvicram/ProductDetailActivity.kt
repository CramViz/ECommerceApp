package com.example.ecommerceappvicram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import kotlinx.coroutines.delay

class ProductDetailActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val productId = intent.getIntExtra("productId", -1)

        val context = this

        setContent {
            ECommerceAPPvicramTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Détail du produit") },
                            navigationIcon = {
                                IconButton(onClick = { context.finish() }) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                                }
                            }
                        )
                    }
                ) { padding ->
                    if (productId != -1) {
                        ProductDetailScreen(productId, Modifier.padding(padding))
                    } else {
                        Text("Produit introuvable", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailScreen(productId: Int, modifier: Modifier = Modifier) {
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    var isAddedToCart by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                product = RetrofitInstance.api.getProductById(productId)
            } catch (_: Exception) {
            }
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        product?.let {
            Box(modifier = modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(it.image),
                        contentDescription = it.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(it.title, style = MaterialTheme.typography.titleLarge)
                    Text(
                        "${it.price} €",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                    Text(it.description)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            CartManager.addToCart(it)
                            isAddedToCart = true
                            scope.launch {
                                delay(1500)
                                isAddedToCart = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🛒 Ajouter au panier")
                    }
                }


                AnimatedVisibility(
                    visible = isAddedToCart,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = "✅ Ajouté au panier !",
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}