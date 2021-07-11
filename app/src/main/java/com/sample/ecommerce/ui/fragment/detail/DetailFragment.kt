package com.sample.ecommerce.ui.fragment.detail

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sample.ecommerce.R
import com.sample.ecommerce.custom.LineIndicator
import com.sample.ecommerce.data.api.model.Cart
import com.sample.ecommerce.data.api.model.EmptyProduct
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.data.api.model.Status
import com.sample.ecommerce.databinding.FragmentDetailBinding
import com.sample.ecommerce.dialog.ProgressDialog
import com.sample.ecommerce.ui.base.BaseFragment
import com.sample.ecommerce.ui.adapter.ColorsAdapter
import com.sample.ecommerce.ui.adapter.ImageAdapter
import com.sample.ecommerce.ui.adapter.SizesAdapter
import com.sample.ecommerce.utils.currencyFormat
import com.sample.ecommerce.utils.getBackStackData
import com.sample.ecommerce.utils.setBackStackData
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import timber.log.Timber

class DetailFragment : BaseFragment<FragmentDetailBinding>() {

    private lateinit var binding: FragmentDetailBinding
    private val viewModel: DetailViewModel by inject()
    private val args: DetailFragmentArgs by navArgs()

    private lateinit var progressDialog: ProgressDialog
    private lateinit var product: Product

    private var availableQuantity = 0
    private var quantityToAdd = 0
    private var discountedPrice = 0
    private var selectedColor = ""
    private var selectedSize = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailBinding.inflate(layoutInflater)
        return getPersistentView(binding)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.fragment = this
        binding.userId = args.userId
        viewModel.getProductDetail(args.product.id)
        progressDialog = ProgressDialog(requireActivity())
        this.product = args.product
        when {
            args.product != EmptyProduct -> setData(args.product)
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.productStateFlow.collect {
                when (it.status) {
                    Status.LOADING -> {
                        loading(true)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null) setData(it.data)
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
                        if (it.data != null) navigateToCart(it.data)
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
                tvQuantity.doOnTextChanged { text, _, _, _ ->
                    quantityToAdd = Integer.parseInt(text.toString().trim())
                    when (quantityToAdd) {
                        0 -> {
                            enableAddToCart(false)
                            ivDecrease.setColorFilter(
                                ContextCompat.getColor(requireContext(), R.color.disabled),
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        }

                        availableQuantity -> ivIncrease.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.disabled),
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )

                        else -> {
                            when {
                                !product.size.isNullOrEmpty() && !product.color.isNullOrEmpty() ->
                                    enableAddToCart(quantityToAdd > 0 && selectedSize.isNotEmpty() && selectedColor.isNotEmpty())
                                !product.size.isNullOrEmpty() -> enableAddToCart(quantityToAdd > 0 && selectedSize.isNotEmpty())
                                !product.color.isNullOrEmpty() -> enableAddToCart(quantityToAdd > 0 && selectedColor.isNotEmpty())
                                else -> enableAddToCart(quantityToAdd > 0)
                            }
                            ivIncrease.setColorFilter(
                                ContextCompat.getColor(requireContext(), R.color.black),
                                android.graphics.PorterDuff.Mode.SRC_IN

                            )
                            ivDecrease.setColorFilter(
                                ContextCompat.getColor(requireContext(), R.color.black),
                                android.graphics.PorterDuff.Mode.SRC_IN

                            )
                        }
                    }
                    quantity = quantityToAdd.toString()
                }
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = when {
            isLoading -> View.VISIBLE
            else -> View.GONE
        }
    }

    private fun setData(prod: Product) {
        product = prod
        availableQuantity = prod.availableQuantity
        with(binding) {
            productModel = prod
            quantity = quantityToAdd.toString()

            val discountAmount = (prod.price * prod.discountPercent) / 100
            discountedPrice = prod.price - discountAmount

            when {
                prod.discountPercent != 0 -> {
                    tvOriginalPrice.visibility = View.VISIBLE
                    tvOriginalPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvOriginalPrice.text = currencyFormat(prod.price)
                    tvDiscount.text = "(${prod.discountPercent}% OFF)"
                }
            }
            tvPrice.text = currencyFormat(discountedPrice)

            when {
                prod.color.isNullOrEmpty() -> colorsLayout.visibility = View.GONE
                else -> {
                    colorsLayout.visibility = View.VISIBLE
                    initColorRecyclerView(prod.color as ArrayList<String>)
                }
            }

            when {
                prod.size.isNullOrEmpty() -> sizesLayout.visibility = View.GONE
                else -> {
                    sizesLayout.visibility = View.VISIBLE
                    initSizeRecyclerView(prod.size as ArrayList<String>)
                }
            }
        }

        initRecyclerView(prod)
    }

    private fun initRecyclerView(data: Product) {
        val images: ArrayList<String> = data.image as ArrayList<String>
        val imageAdapter = ImageAdapter(images, data.id)
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL,
            false
        )
        val snapHelper = PagerSnapHelper()

        binding.rvProductImage.apply {
            setItemViewCacheSize(4)
            layoutManager = linearLayoutManager
            adapter = imageAdapter
            addItemDecoration(LineIndicator())
            when (onFlingListener) {
                null -> snapHelper.attachToRecyclerView(this)
            }
        }
    }

    private fun initColorRecyclerView(colors: ArrayList<String>) {
        binding.rvColors.apply {
            val colorsAdapter = ColorsAdapter(requireContext(), true, {
                selectedColor = it
                when {
                    !product.size.isNullOrEmpty() -> enableAddToCart(quantityToAdd > 0 && selectedSize.isNotEmpty())
                    else -> enableAddToCart(quantityToAdd > 0)
                }
            }) {}
            colorsAdapter.setData(colors)
            colorsAdapter.notifyDataSetChanged()
            adapter = colorsAdapter
        }

    }

    private fun initSizeRecyclerView(sizes: ArrayList<String>) {
        binding.rvSizes.apply {
            val sizesAdapter = SizesAdapter {
                selectedSize = it
                when {
                    !product.color.isNullOrEmpty() -> enableAddToCart(quantityToAdd > 0 && selectedColor.isNotEmpty())
                    else -> enableAddToCart(quantityToAdd > 0)
                }
            }
            sizesAdapter.setData(sizes)
            sizesAdapter.notifyDataSetChanged()
            adapter = sizesAdapter
        }
    }

    fun decreaseQuantity() {
        when {
            quantityToAdd > 0 -> {
                quantityToAdd--
                binding.quantity = quantityToAdd.toString()
            }
        }
    }

    fun increaseQuantity() {
        when {
            quantityToAdd < availableQuantity -> {
                quantityToAdd++
                binding.quantity = quantityToAdd.toString()
            }
        }
    }

    private fun enableAddToCart(toEnable: Boolean) {
        with(binding) {
            btnAddToCart.setBackgroundColor(ContextCompat.getColor(requireContext(), when {
                toEnable -> R.color.colorPrimary
                else -> R.color.disabled
            }))
            btnAddToCart.isClickable = toEnable
        }
    }

    fun addToCart() {
        viewModel.addToCart(CartParams(
            discountedPrice,
            product.name,
            selectedSize,
            product.image[0],
            selectedColor,
            quantityToAdd,
            args.userId,
            product.category,
            product.id
        ))
    }

    fun navigateToUpdate() {
        val action = DetailFragmentDirections
            .actionDetailFragmentToAddProductFragment(product)
        findNavController().navigate(action)
    }

    private fun navigateToCart(cart: Cart) {
        setBackStackData("cart", cart, true)
    }

    fun navigateUp() {
        findNavController().navigateUp()
    }
}