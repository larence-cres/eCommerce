package com.sample.ecommerce.di

import com.sample.ecommerce.data.DataStorePreference
import com.sample.ecommerce.data.NetworkController
import com.sample.ecommerce.data.api.ApiService
import com.sample.ecommerce.repository.MainRepository
import com.sample.ecommerce.ui.fragment.addProduct.AddProductViewModel
import com.sample.ecommerce.ui.fragment.dashboard.DashboardViewModel
import com.sample.ecommerce.ui.fragment.detail.DetailViewModel
import com.sample.ecommerce.ui.fragment.login.LoginViewModel
import com.sample.ecommerce.ui.fragment.productList.ProductListViewModel
import com.sample.ecommerce.ui.fragment.profile.ProfileViewModel
import com.sample.ecommerce.ui.fragment.register.RegisterViewModel
import com.sample.ecommerce.ui.fragment.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {

    fun getModules() = module {
        single { DataStoreModule(get()).providePreference() }
        single { ApiService(get()) }
        single { DataStorePreference(get()) }
        single { NetworkController(get()) }

        single { NetworkModule().client }

        single { MainRepository(get()) }

        viewModel { DashboardViewModel(get(), get(), get()) }
        viewModel { LoginViewModel(get(), get(), get()) }
        viewModel { RegisterViewModel(get(), get(), get()) }
        viewModel { DetailViewModel(get(), get(), get()) }
        viewModel { ProductListViewModel(get(), get(), get()) }
        viewModel { SearchViewModel(get(), get(), get()) }
        viewModel { ProfileViewModel(get()) }
        viewModel { AddProductViewModel(get(), get(), get()) }
    }

}