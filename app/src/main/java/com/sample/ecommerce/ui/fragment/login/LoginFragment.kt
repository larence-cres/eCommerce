package com.sample.ecommerce.ui.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sample.ecommerce.data.api.model.Status
import com.sample.ecommerce.databinding.FragmentLoginBinding
import com.sample.ecommerce.dialog.ProgressDialog
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import timber.log.Timber

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by inject()

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.fragment = this
        progressDialog = ProgressDialog(requireActivity())
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.authStateFlow.collect {
                when (it.status) {
                    Status.LOADING -> {
                        progressDialog.startLoading("Logging In...")
                    }
                    Status.SUCCESS -> {
                        progressDialog.dismissDialog()
                        when {
                            it.data != null -> viewModel.saveAuthCredentials(it.data)
                        }
                    }
                    Status.ERROR -> {
                        progressDialog.dismissDialog()
                    }
                    Status.EMPTY -> {

                    }
                }
            }
        }
    }

    private fun getUsername(): String {
        return binding.etUsername.text.toString()
    }

    private fun getPassword(): String {
        return binding.etPassword.text.toString()
    }

    private fun loginParams(): LoginParams {
        return LoginParams(getUsername(), getPassword())
    }

    fun login() {
        when {
            isValid() -> viewModel.login(loginParams())
        }
    }

    private fun isValid(): Boolean = when {
        getUsername().isEmpty() -> {
            binding.etUsername.error = "Username required"
            false
        }
        getPassword().isEmpty() -> {
            binding.etPassword.error = "Password required"
            false
        }
        else -> true
    }

    fun navigateToRegister() {
        val action = LoginFragmentDirections
            .actionLoginFragmentToRegisterFragment()
        findNavController().navigate(action)
    }
}