package com.instaapp.veggietemp1.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.veggietemp1.network.repositories.CartRepository
import com.instaapp.veggietemp1.viewModel.CartViewModel
@Suppress("UNCHECKED_CAST")
class CartViewModelFactory(private val cartRepository: CartRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return CartViewModel(cartRepository) as T
    }
}