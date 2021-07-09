package com.sample.ecommerce.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import com.sample.ecommerce.R
import com.sample.ecommerce.databinding.DialogProgressBinding

class ProgressDialog(private val activity: Activity) : Dialog(activity) {

    private var binding: DialogProgressBinding

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun startLoading(message: String) {
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        window?.setLayout(width, height)
        window?.setBackgroundDrawable(ColorDrawable(activity.resources.getColor(R.color.transparent)))
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding.message = message
        show()
    }

    fun dismissDialog() {
        dismiss()
    }

}