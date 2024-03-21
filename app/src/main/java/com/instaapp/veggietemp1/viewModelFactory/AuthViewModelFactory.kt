package com.instaapp.veggietemp1.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.veggietemp1.network.repositories.UserRepository
import com.instaapp.veggietemp1.viewModel.AuthViewModel

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AuthViewModel(userRepository) as T
    }
}