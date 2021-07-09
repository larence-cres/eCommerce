package com.sample.ecommerce.ui.fragment.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sample.ecommerce.data.api.model.Status
import com.sample.ecommerce.databinding.FragmentRegisterBinding
import com.sample.ecommerce.dialog.ProgressDialog
import com.sample.ecommerce.utils.isEmailValid
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import timber.log.Timber

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by inject()

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
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
                        progressDialog.startLoading("Signing Up...")
                    }
                    Status.SUCCESS -> {
                        progressDialog.dismissDialog()
                        if (it.data != null) viewModel.saveAuthCredentials(it.data)
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

    private fun getEmail(): String {
        return binding.etEmail.text.toString()
    }

    private fun getAddress(): String {
        return binding.etAddress.text.toString()
    }

    private fun getPassword(): String {
        return binding.etPassword.text.toString()
    }

    private fun getConfirmPassword(): String {
        return binding.etConfirmPassword.text.toString()
    }

    private fun isSeller(): Boolean {
        return binding.cbSeller.isChecked
    }

    private fun registerParams(): RegisterParams {
        return RegisterParams(getUsername(), getEmail(), getAddress(), getPassword(), isSeller())
    }

    fun register() {
        when {
            isValid() -> viewModel.register(registerParams())
        }
    }

    private fun isValid(): Boolean = when {
        getUsername().isEmpty() -> {
            binding.etUsername.error = "Username required"
            false
        }
        getEmail().isEmpty() -> {
            etEmail.error = "Email required"
            false
        }
        !isEmailValid(getEmail()) -> {
            etEmail.error = "Email not valid"
            false
        }
        getAddress().isEmpty() -> {
            binding.etAddress.error = "Address required"
            false
        }
        getPassword().isEmpty() -> {
            binding.etPassword.error = "Password required"
            false
        }
        !getPassword().equals(getConfirmPassword(), ignoreCase = false) -> {
            etConfirmPassword.error = "Password doesn't match"
            false
        }
        else -> true
    }

}