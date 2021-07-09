package com.sample.ecommerce.ui.base

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.sample.ecommerce.databinding.FragmentDashboardBinding
import java.lang.Exception

abstract class BaseFragment<T> : Fragment() {

    var hasInitializedRootView = false
    private var rootView: View? = null

    fun getPersistentView(binding: T): View {
        when (rootView) {
            null -> {
                binding as ViewDataBinding
                rootView = binding.root
            }
        }
        return rootView!!
    }
    
}