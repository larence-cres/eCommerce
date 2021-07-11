package com.sample.ecommerce.ui.fragment.dashboard

import android.os.Bundle
import android.view.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.ecommerce.data.AppConstants
import com.sample.ecommerce.data.api.model.*
import com.sample.ecommerce.databinding.FragmentDashboardBinding
import com.sample.ecommerce.dialog.ProgressDialog
import com.sample.ecommerce.ui.base.BaseFragment
import com.sample.ecommerce.ui.adapter.MainAdapter
import com.sample.ecommerce.utils.getBackStackData
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {

    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by inject()

    private lateinit var mainAdapter: MainAdapter
    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var progressDialog: ProgressDialog

    var userId = ""
    var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        return getPersistentView(binding)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getBackStackData<Cart>("cart") {
            Timber.e("Cart data on dashboard : $it")
            navigateToProductList("cart")
        }

        initView()
    }

    private fun initView() {
        viewModel.getAllProducts()
        binding.fragment = this
        progressDialog = ProgressDialog(requireActivity())
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenResumed {
            isLoading = true
            viewModel.getAllProducts()
        }
        lifecycleScope.launchWhenStarted {
            viewModel.token.collect {
                AppConstants.TOKEN = it
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.user.collect {
                when {
                    it.isNotEmpty() -> userData(viewModel.user(it))
                    else -> binding.loggedIn = false
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.productsStateFlow.collect { it ->
                when (it.status) {
                    Status.LOADING -> {
                        if (!isLoading) loading(true)
                    }
                    Status.SUCCESS -> {
                        isLoading = false
                        binding.swipeRefresh.isRefreshing = false
                        loading(false)
                        notifyAdapter(it.data)
                        viewModel.saveProducts(it.data)
                    }
                    Status.ERROR -> {
                        loading(false)
                        when (it.code) {
                            0 -> viewModel.products.collect {
                                when {
                                    it.isNotEmpty() -> notifyAdapter(viewModel.products(it))
                                }
                            }
                        }
                    }
                    Status.EMPTY -> {

                    }
                }
            }
        }
    }

    private fun userData(user: User) {
        with(binding) {
            Timber.e("Seller ${user.seller}")
            userId = user.id
            loggedIn = true
            tvName.text = user.username.capitalize(Locale.ROOT)
            ivAdd.visibility = when {
                user.seller -> View.VISIBLE
                else -> View.GONE
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        concatAdapter = ConcatAdapter()

        binding.rvMain.apply {
            layoutManager = linearLayoutManager
            adapter = concatAdapter
        }
    }

    private fun notifyAdapter(data: Products?) {
        initRecyclerView()
        if (data != null) {
            val titles = ArrayList<String>()
            val products = ArrayList<List<Product>>()

            mainAdapter = MainAdapter(requireContext(), { navigateToDetail(it) }) {
                navigateToProductList(it)
            }

            data.products.forEach {
                titles.add(it.key)
                products.add(data.products[it.key]!!)
            }

            mainAdapter.setData(titles, products)
            concatAdapter.addAdapter(mainAdapter)
            concatAdapter.notifyDataSetChanged()
        }
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = when {
            isLoading -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun onRefresh() {
        isLoading = true
        viewModel.getAllProducts()
    }

    private fun navigateToDetail(product: Product) {
        val action = DashboardFragmentDirections
            .actionDashboardFragmentToDetailFragment(userId, product)
        findNavController().navigate(action)
    }

    fun navigateToSearch() {
        val action = DashboardFragmentDirections
            .actionDashboardFragmentToSearchFragment()
        findNavController().navigate(action)
    }

    fun navigateToAddProduct() {
        val action = DashboardFragmentDirections
            .actionDashboardFragmentToAddProductFragment(EmptyProduct)
        findNavController().navigate(action)
    }

    fun navigateToProductList(category: String) {
        val action = DashboardFragmentDirections
            .actionDashboardFragmentToProductListFragment(category)
        findNavController().navigate(action)
    }

    fun navigateToLogin() {
        val action = DashboardFragmentDirections
            .actionDashboardFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    fun navigateToProfile() {
        val action = DashboardFragmentDirections
            .actionDashboardFragmentToProfileFragment()
        findNavController().navigate(action)
    }
}