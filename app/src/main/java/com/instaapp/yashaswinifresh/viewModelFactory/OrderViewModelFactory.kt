package com.instaapp.yashaswinifresh.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.yashaswinifresh.network.repositories.OrderRepository
import com.instaapp.yashaswinifresh.viewModel.OrderViewModel

class OrderViewModelFactory(private val orderRepository: OrderRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return OrderViewModel(orderRepository) as T
    }
}