package com.instaapp.yashaswinifresh.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.yashaswinifresh.network.repositories.PaymentRepository
import com.instaapp.yashaswinifresh.viewModel.PaymentViewModel

class PaymentViewModelFactory(private val paymentRepository: PaymentRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return PaymentViewModel(paymentRepository) as T
    }
}