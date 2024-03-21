package com.instaapp.yashaswinifresh.utils

// CartManager.kt

object CartManager {

    private var cartProductIds: MutableList<Triple<Int?, Int?, Int>> = mutableListOf()

    fun initialize() {
        // Your initialization logic here
        // This could be fetching data from a server, a local database, or any other source
    }

    fun getCartProductIds(): List<Triple<Int?, Int?, Int>> {
        return cartProductIds
    }

    fun updateCartProductIds(newCartProductIds: List<Triple<Int?, Int?, Int>>) {
        for ((cartItemId, productId, qty) in getCartProductIds()) {
            log(
                "updateCartProductIds ",
                "Before cart_item_id: $cartItemId, cart: $productId, qty: $qty"
            )
        }
        cartProductIds.clear() // Clear the existing list
        cartProductIds.addAll(newCartProductIds)
    }

    fun clearAllItem(){
        cartProductIds.clear()
    }

    // Function to check if a specific product ID is in the cart
    fun isProductInCart(productId: Int): Boolean {
        return cartProductIds.any { it.second == productId }
    }

    fun getCartForProduct(productId: Int): Triple<Int?, Int?, Int>? {
        return cartProductIds.find { it.second == productId }
    }

    // Function to update or add an item in cartProductIds
    fun updateOrAddCartItem(cartItemId: Int?, productId: Int?, quantity: Int) {
        // Check if the product is already in the cart
        val existingCartItem = cartProductIds.find { it.second == productId }

        if (existingCartItem != null) {
            // Update the existing item's quantity
            val updatedCartItem = Triple(cartItemId, productId!!, quantity)
            cartProductIds[cartProductIds.indexOf(existingCartItem)] = updatedCartItem
        } else {
            // Add a new item to the cart
            val newCartItem = Triple(cartItemId, productId!!, quantity)
            cartProductIds.add(newCartItem)
        }


        // Now, cartProductIds contains the triples with cart_item_id, cart, and product_id
        for ((cartItemId, cartId, productId) in getCartProductIds()) {
            // Do something with cartItemId, cartId, and productId
            // For example, print them

            log(
                "getCartProductIds ",
                "cart_item_id: $cartItemId, cart: $cartId, product_id: $productId"
            )
        }

        // Your update logic here
        // This could involve notifying observers, triggering UI updates, etc.
    }

    fun removeCartItem(productId: Int) {
        val index = cartProductIds.indexOfFirst { it.second == productId }
        if (index != -1) {
            cartProductIds.removeAt(index)
        }
    }


}

