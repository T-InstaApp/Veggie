package com.instaapp.yashaswinifresh

import android.app.Application
import com.instaapp.yashaswinifresh.network.MyApi
import com.instaapp.yashaswinifresh.network.NetworkConnectionInterceptor
import com.instaapp.yashaswinifresh.network.repositories.*
import com.instaapp.yashaswinifresh.utils.CartManager
import com.instaapp.yashaswinifresh.viewModelFactory.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class MyApplication : Application(), KodeinAware {
/* Test First Commit */
    override val kodein = Kodein.lazy {
        import(androidXModule(this@MyApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { UserRepository(instance()) }
        bind() from singleton { HomeRepository(instance()) }
        bind() from singleton { OrderRepository(instance()) }
        bind() from singleton { CartRepository(instance()) }
        bind() from singleton { CheckOutRepository(instance()) }
        bind() from singleton { PaymentRepository(instance()) }
        bind() from singleton { AddToCartRepository(instance()) }

        //Providers
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }
        bind() from provider { OrderViewModelFactory(instance()) }
        bind() from provider { CartViewModelFactory(instance()) }
        bind() from provider { CheckOutViewModelFactory(instance()) }
        bind() from provider { PaymentViewModelFactory(instance()) }
        bind() from provider { AddToCartViewModelFactory(instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize CartManager here
        CartManager.initialize()
    }
}