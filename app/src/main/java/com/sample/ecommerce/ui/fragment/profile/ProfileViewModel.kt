package com.sample.ecommerce.ui.fragment.profile

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.sample.ecommerce.data.AppConstants
import com.sample.ecommerce.data.DataStorePreference
import com.sample.ecommerce.data.api.model.User

class ProfileViewModel(
    private val preference: DataStorePreference
) : ViewModel() {

    val user = preference.fetchString(AppConstants.PROFILE_DATA)

    fun user(user: String): User = Gson().fromJson(user, User::class.java)

}