package com.sample.ecommerce.ui.fragment.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.data.api.model.Status
import com.sample.ecommerce.databinding.FragmentSearchBinding
import com.sample.ecommerce.ui.adapter.SearchAdapter
import com.sample.ecommerce.ui.base.BaseFragment
import com.sample.ecommerce.utils.hideKeyboard
import com.sample.ecommerce.utils.showKeyboard
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import timber.log.Timber

class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by inject()

    private lateinit var searchAdapter: SearchAdapter

    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return getPersistentView(binding)
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
                        showSearchField(false)
                        initRecyclerView(it.data!!.products)
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
            with(binding) {
                etSearch.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        if (p0.isNullOrEmpty())
                            ivClear.visibility = View.INVISIBLE
                        else
                            ivClear.visibility = View.VISIBLE
                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }
                })
            }
        }

        lifecycleScope.launchWhenStarted {
            with(binding) {
                showKeyboard()
                etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        requireActivity().hideKeyboard()
                        val searchText = etSearch.text.toString()
                        if (searchText.isNotEmpty()) {
                            binding.searchText = searchText
                            search(searchText)
                        }
                        return@OnEditorActionListener true
                    }
                    false
                })
            }
        }
    }

    private fun initRecyclerView(products: List<Product>) {
        if (products.isNotEmpty()) {
            val linearLayoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

            searchAdapter = SearchAdapter {
                navigateToDetail(it)
            }
            searchAdapter.setData(products as ArrayList<Product>)
            searchAdapter.notifyDataSetChanged()
            binding.rvSearchList.apply {
                layoutManager = linearLayoutManager
                adapter = searchAdapter
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = when {
            isLoading -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun showSearchField(toShow: Boolean) {
        with(binding) {
            ivClear.visibility = View.INVISIBLE
            when {
                toShow -> {
                    etSearch.setText(binding.searchText)
                    etSearch.visibility = View.VISIBLE
                    tvSearchName.visibility = View.GONE
                    ivSearch.visibility = View.GONE
                    etSearch.requestFocus()
                    showKeyboard()
                }
                else -> {
                    tvSearchName.text = binding.searchText
                    tvSearchName.visibility = View.VISIBLE
                    etSearch.visibility = View.GONE
                    ivSearch.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showKeyboard() {
        requireActivity().showKeyboard(binding.etSearch)
    }

    fun clearSearchField() {
        with(binding) {
            etSearch.setText("")
        }
    }

    private fun search(searchText: String) {
        viewModel.searchProducts(searchText)
    }

    private fun navigateToDetail(product: Product) {
        val action = SearchFragmentDirections
            .actionSearchFragmentToDetailFragment(userId, product)
        findNavController().navigate(action)
    }

    fun navigateUp() {
        findNavController().navigateUp()
    }
}