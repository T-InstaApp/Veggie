package com.instaapp.veggietemp1.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.veggietemp1.network.repositories.OrderRepository
import com.instaapp.veggietemp1.viewModel.OrderViewModel
@Suppress("UNCHECKED_CAST")
class OrderViewModelFactory(private val orderRepository: OrderRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return OrderViewModel(orderRepository) as T
    }
}