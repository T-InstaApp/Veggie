package com.instaapp.yashaswinifresh.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.yashaswinifresh.network.repositories.CartRepository
import com.instaapp.yashaswinifresh.viewModel.CartViewModel

class CartViewModelFactory(private val cartRepository: CartRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return CartViewModel(cartRepository) as T
    }
}