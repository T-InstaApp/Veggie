package com.instaapp.veggietemp1.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.veggietemp1.network.repositories.CheckOutRepository
import com.instaapp.veggietemp1.viewModel.CheckOutModel

@Suppress("UNCHECKED_CAST")
class CheckOutViewModelFactory(private val checkOutRepository: CheckOutRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return CheckOutModel(checkOutRepository) as T
    }
}