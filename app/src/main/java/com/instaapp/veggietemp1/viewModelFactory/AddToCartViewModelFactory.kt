package com.instaapp.veggietemp1.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.veggietemp1.network.repositories.AddToCartRepository
import com.instaapp.veggietemp1.viewModel.AddToCartViewModel

class AddToCartViewModelFactory (private val addToCartRepository: AddToCartRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AddToCartViewModel(addToCartRepository) as T
    }
}