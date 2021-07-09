package com.sample.ecommerce.ui.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.sample.ecommerce.R
import com.sample.ecommerce.data.api.model.User
import com.sample.ecommerce.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.fragment = this
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.user.collect {
                setData(viewModel.user(it))
            }
        }
    }

    private fun setData(user: User) {
        with(binding) {
            profile = user
            // TODO :: Need to add avatar
            ivAvatar.load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRJ68BC95Cxd0en03FNqNcFee7Y-b6Tjju_RA&usqp=CAU") {
                placeholder(R.drawable.ic_baseline_person_24)
                error(R.drawable.ic_baseline_person_24)
            }
        }
    }

    fun navigateUp() {
        findNavController().navigateUp()
    }
}