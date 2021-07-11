package com.sample.ecommerce.ui.fragment.productList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.ecommerce.data.api.model.Cart
import com.sample.ecommerce.data.api.model.EmptyProduct
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.data.api.model.Status
import com.sample.ecommerce.databinding.FragmentProductListBinding
import com.sample.ecommerce.ui.adapter.CartsAdapter
import com.sample.ecommerce.ui.base.BaseFragment
import com.sample.ecommerce.ui.adapter.ProductListAdapter
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class ProductListFragment : BaseFragment<FragmentProductListBinding>() {

    private lateinit var binding: FragmentProductListBinding
    private val viewModel: ProductListViewModel by inject()
    private val args: ProductListFragmentArgs by navArgs()

    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var concatAdapter: ConcatAdapter

    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProductListBinding.inflate(layoutInflater)
        return getPersistentView(binding)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.category = args.category.capitalize(Locale.ROOT)
        when (args.category) {
            "cart" -> viewModel.getMyCarts()
            else -> viewModel.getProductsDetail(args.category)
        }
        binding.fragment = this
        observeViewModel()
        initRecyclerView()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.user.collect {
                when {
                    it.isNotEmpty() -> userId = viewModel.user(it).id
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.productsStateFlow.collect {
                when (it.status) {
                    Status.LOADING -> {
                        loading(true)
                    }
                    Status.SUCCESS -> {
                        notifyProductsAdapter(it.data!!.products)
                        loading(false)
                    }
                    Status.ERROR -> {
                        loading(false)
                    }
                    Status.EMPTY -> {

                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.cartStateFlow.collect {
                when (it.status) {
                    Status.LOADING -> {
                        loading(true)
                    }
                    Status.SUCCESS -> {
                        notifyCartsAdapter(it.data)
                        loading(false)
                    }
                    Status.ERROR -> {
                        loading(false)
                    }
                    Status.EMPTY -> {

                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        concatAdapter = ConcatAdapter()

        binding.rvProductList.apply {
            layoutManager = linearLayoutManager
            adapter = concatAdapter
        }
    }

    private fun notifyProductsAdapter(products: List<Product>) {
        when {
            products.isNotEmpty() -> {
                productListAdapter = ProductListAdapter(requireContext()) {
                    navigateToDetail(it)
                }
                productListAdapter.setData(products)
                concatAdapter.addAdapter(productListAdapter)
                concatAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun notifyCartsAdapter(carts: List<Cart>?) {
        when {
            !carts.isNullOrEmpty() -> {
                Timber.e("Carts : $carts")
                binding.checkout.visibility = View.VISIBLE
                val cartsAdapter = CartsAdapter {
                    navigateToDetail(EmptyProduct)
                }
                cartsAdapter.setData(carts as ArrayList<Cart>)
                concatAdapter.addAdapter(cartsAdapter)
                concatAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = when {
            isLoading -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun navigateToSearch() {
        val action = ProductListFragmentDirections
            .actionProductListFragmentToSearchFragment()
        findNavController().navigate(action)
    }

    private fun navigateToDetail(product: Product) {
        val action = ProductListFragmentDirections
            .actionProductListFragmentToDetailFragment(userId, product)
        findNavController().navigate(action)
    }

    fun navigateUp() {
        findNavController().navigateUp()
    }
}