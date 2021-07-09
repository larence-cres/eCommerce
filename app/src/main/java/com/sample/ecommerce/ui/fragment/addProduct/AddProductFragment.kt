package com.sample.ecommerce.ui.fragment.addProduct

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sample.ecommerce.R
import com.sample.ecommerce.data.api.model.EmptyProduct
import com.sample.ecommerce.data.api.model.Status
import com.sample.ecommerce.databinding.FragmentAddProductBinding
import com.sample.ecommerce.dialog.ProgressDialog
import com.sample.ecommerce.ui.adapter.ColorsAdapter
import com.sample.ecommerce.ui.base.BaseFragment
import com.sample.ecommerce.ui.adapter.ImagesAdapter
import com.sample.ecommerce.utils.askPermissionDialog
import com.sample.ecommerce.utils.createListDialog
import com.sample.ecommerce.utils.setBackStackData
import com.sample.ecommerce.utils.snackMessage
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import io.ktor.util.*
import kotlinx.coroutines.flow.collect
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class AddProductFragment : BaseFragment<FragmentAddProductBinding>() {

    private lateinit var binding: FragmentAddProductBinding
    private val viewModel: AddProductViewModel by inject()
    private val args: AddProductFragmentArgs by navArgs()

    private lateinit var progressDialog: ProgressDialog

    private lateinit var colorsAdapter: ColorsAdapter
    private lateinit var imagesAdapter: ImagesAdapter

    private var categories = arrayOf<String>()

    // TODO :: Move the static array to the strings file
    private var sizes = arrayOf<String>("S", "M", "L", "XL", "XXL", "XXXL")
    private var selectedSizes = ArrayList<String>()
    private var checkedItems = BooleanArray(sizes.size)

    private var colors = ArrayList<String>()
    var imageSet = HashSet<String>()
    private var images = ArrayList<String>()
    private var uploadFiles = HashMap<String, File>()

    private var update = false
    private var productId = ""

    private val REQUEST_CODE_CHOOSE = 333

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddProductBinding.inflate(layoutInflater)
        return getPersistentView(binding)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        with(binding) {
            fragment = this@AddProductFragment
            if (args.product != EmptyProduct) {
                update = true
                product = args.product
                productId = args.product.id
                category = args.product.category.capitalize(Locale.ROOT)

                switchSize.isChecked = args.product.size.isNotEmpty()
                switchColor.isChecked = args.product.color.isNotEmpty()
                switchDiscount.isChecked = args.product.discountPercent != 0

                selectedSizes = args.product.size as ArrayList<String>
                size = selectedSizes.toString().removePrefix("[").removeSuffix("]")
                sizes.indices.forEach { i ->
                    when {
                        args.product.size.contains(sizes[i]) -> checkedItems[i] = true
                    }
                }

                images = args.product.image as ArrayList<String>
                imageSet.addAll(images)
                colors = args.product.color as ArrayList<String>
            }
        }
        progressDialog = ProgressDialog(requireActivity())
        observeViewModel()
        observeViewUpdate()
        initRecyclerViews()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            categories = viewModel.categories().toTypedArray()
        }
        lifecycleScope.launchWhenStarted {
            viewModel.uploadImagesStateFlow.collect {
                when (it.status) {
                    Status.LOADING -> {
                        progressDialog.startLoading("Uploading data...")
                    }
                    Status.SUCCESS -> {
                        if (it.data != null) {
                            viewModel.uploadProduct(
                                productParams(it.data.images),
                                update,
                                productId)
                        } else {
                            progressDialog.dismissDialog()
                            Timber.e("Data null -> $it")
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
        lifecycleScope.launchWhenStarted {
            viewModel.productAddStateFlow.collect {
                when (it.status) {
                    Status.LOADING -> {
                        Timber.e("Uploading product")
                    }
                    Status.SUCCESS -> {
                        progressDialog.dismissDialog()
                        navigateUp()
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

    fun showCategoriesDialog() {
        requireActivity().createListDialog(
            "Select Categories",
            categories,
        ) { dialogInterface, i ->
            binding.tvCategory.text = categories[i].capitalize(Locale.ROOT)
        }.show()
    }

    fun showSizesDialog() {
        AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
            .setTitle("Select size")
            .setMultiChoiceItems(sizes, checkedItems) { dialogInterface, i, isChecked ->
                checkedItems[i] = isChecked
                if (isChecked) selectedSizes.add(sizes[i]) else selectedSizes.remove(sizes[i])
            }
            .setPositiveButton("Ok") { dialogInterface, which ->
                binding.tvSize.text = selectedSizes.toString().removePrefix("[").removeSuffix("]")
            }.create().show()
    }

    private fun observeViewUpdate() {
        with(binding) {
            switchDiscount.setOnCheckedChangeListener { _, isChecked ->
                discountView.visibility = when {
                    isChecked -> View.VISIBLE
                    else -> View.GONE
                }
            }

            switchColor.setOnCheckedChangeListener { _, isChecked ->
                colorView.visibility = when {
                    isChecked -> View.VISIBLE
                    else -> View.GONE
                }
            }

            switchSize.setOnCheckedChangeListener { _, isChecked ->
                tvSize.visibility = when {
                    isChecked -> View.VISIBLE
                    else -> View.GONE
                }
            }
        }
    }

    fun openColorPalette() {
        ColorPickerDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
            .setTitle("ColorPicker")
            .setPreferenceName(getString(R.string.app_name))
            .setPositiveButton("Confirm",
                ColorEnvelopeListener { envelope, _ ->
                    colors.add("#${envelope.hexCode}")
                    notifyColorAdapter(colors)
                })
            .setNegativeButton(
                "Cancel"
            ) { dialogInterface, _ -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .show()
    }

    private fun initRecyclerViews() {
        with(binding) {
            rvColors.apply {
                colorsAdapter = ColorsAdapter(requireContext(), false, {
                    Toast.makeText(requireContext(),
                        "Long press to delete item",
                        Toast.LENGTH_SHORT).show()
                }) {
                    colors.removeAt(it)
                    colorsAdapter.notifyDataSetChanged()
                }
                colorsAdapter.setData(colors)
                adapter = colorsAdapter
            }
            rvImages.apply {
                imagesAdapter = ImagesAdapter {
                    images.removeAt(it)
                    imageSet.clear()
                    imageSet.addAll(images)
                    imagesAdapter.notifyDataSetChanged()
                }
                imagesAdapter.setData(images)
                adapter = imagesAdapter
            }
        }
    }

    private fun notifyColorAdapter(colors: ArrayList<String>) {
        if (colors.isNotEmpty()) {
            colorsAdapter.setData(colors)
            colorsAdapter.notifyDataSetChanged()
        }
    }

    private fun notifyImageAdapter(images: ArrayList<String>) {
        if (images.isNotEmpty()) {
            imagesAdapter.setData(images)
            imagesAdapter.notifyDataSetChanged()
        }
    }

    fun openImageChooser() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Matisse.from(requireActivity())
                .choose(MimeType.ofImage(), false)
                .theme(R.style.Matisse_Dracula)
                .countable(true)
                .capture(true)
                .captureStrategy(
                    CaptureStrategy(
                        true,
                        "com.sample.ecommerce.fileprovider",
                        "uploads"
                    )
                )
                .maxSelectable(10)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .setOnSelectedListener { uriList, pathList ->
                    Timber.e("uriList : $uriList")
                    Timber.e("pathList : $pathList")
                    pathList
                        .map { File(it) }
                        .forEach { uploadFiles[it.name] = it }
                    uriList.forEach {
                        imageSet.add(it.toString())
                    }
                    images.clear()
                    images.addAll(imageSet)
                    Timber.e("imageset : $imageSet")
                    Timber.e("images : $images")
                    notifyImageAdapter(images)
                }
                .showSingleMediaType(true)
                .originalEnable(true)
                .maxOriginalSize(10)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener { isChecked ->

                }
                .forResult(REQUEST_CODE_CHOOSE)
        } else {
            requireActivity().askPermissionDialog(
                "Storage",
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * Save
     * TODO :: Implement ktor multipart
     */
    @KtorExperimentalAPI
    @InternalAPI
    fun imageMultipart() {
        if (uploadFiles.size != 0) {
            val builder: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            uploadFiles.entries.forEach {
                if (it.value.exists()) {
                    builder.addFormDataPart("images", it.key,
                        it.value.asRequestBody("image/*".toMediaTypeOrNull()))
                } else {
                    Timber.e("File doesn't exists")
                }
            }
            val requestBody: RequestBody = builder.build()
            viewModel.uploadImages(requestBody)
        } else {
            viewModel.uploadProduct(productParams(images), update, productId)
        }
    }

    private fun getProductName(): String {
        return binding.etName.text.toString()
    }

    private fun getPrice(): String {
        return binding.etPrice.text.toString()
    }

    private fun getCategory(): String {
        return binding.tvCategory.text.toString()
    }

    private fun getAvailableQuantity(): String {
        return binding.etAvailableQuantity.text.toString()
    }

    private fun getDescription(): String {
        return binding.etDescription.text.toString()
    }

    private fun getDiscount(): String {
        return binding.etDiscount.text.toString()
    }

    private fun productParams(images: List<String>): ProductParams {
        return ProductParams(getProductName(),
            selectedSizes,
            images,
            colors,
            getPrice().toInt(),
            getCategory(),
            getDescription(),
            getDiscount().toInt(),
            getAvailableQuantity().toInt())
    }

    @KtorExperimentalAPI
    @InternalAPI
    fun uploadImage() {
        when {
            isValid() -> imageMultipart()
        }
    }

    private fun isValid(): Boolean = when {
        getProductName().isEmpty() -> {
            binding.etName.error = "Product name required"
            false
        }
        getPrice().isEmpty() -> {
            binding.etPrice.error = "Price required"
            false
        }
        getPrice().toInt() == 0 -> {
            binding.etPrice.error = "Price required"
            false
        }
        getCategory().isEmpty() -> {
            requireView().snackMessage(requireContext(), "Please select a category")
            false
        }
        binding.switchDiscount.isChecked && getDiscount().isEmpty() -> {
            binding.etDiscount.error = "Discount required"
            false
        }
        binding.switchColor.isChecked && colors.size == 0 -> {
            requireView().snackMessage(requireContext(), "Please add at least one color")
            false
        }
        binding.switchSize.isChecked && sizes.isEmpty() -> {
            requireView().snackMessage(requireContext(), "Please add at least one size")
            false
        }
        images.size == 0 -> {
            requireView().snackMessage(requireContext(), "Please add at least one image")
            false
        }
        else -> true
    }

    fun navigateUp() {
        findNavController().navigateUp()
    }

}