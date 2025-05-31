package com.example.ecommerceappvicram.model

object CartManager {
    private val cartItems = mutableListOf<Product>()

    fun addToCart(product: Product) {
        cartItems.add(product)
        println("Produit ajouté : ${product.title}")
    }

    fun getCart(): List<Product> {
        return cartItems
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun removeFromCart(product: Product) {
        cartItems.remove(product)
    }

}
