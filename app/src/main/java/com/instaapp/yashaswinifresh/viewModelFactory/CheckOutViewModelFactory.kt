package com.instaapp.yashaswinifresh.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.yashaswinifresh.network.repositories.CheckOutRepository
import com.instaapp.yashaswinifresh.viewModel.CheckOutModel

class CheckOutViewModelFactory(private val checkOutRepository: CheckOutRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return CheckOutModel(checkOutRepository) as T
    }
}