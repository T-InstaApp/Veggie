package com.instaapp.yashaswinifresh.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.yashaswinifresh.network.repositories.HomeRepository
import com.instaapp.yashaswinifresh.viewModel.HomeViewModel

class HomeViewModelFactory(private val homeRepository: HomeRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return HomeViewModel(homeRepository) as T
    }
}