package com.sample.ecommerce.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sample.ecommerce.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.ResponseBody
import org.json.JSONObject
import timber.log.Timber
import java.io.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


fun getErrorMessage(error: String): String {
    return try {
        JSONObject(error).optString("message")
    } catch (e: Exception) {
        e.localizedMessage!!
    }
}

fun currencyFormat(price: Int): String? {
    val formatter: NumberFormat = DecimalFormat("#,##,###")
    return "Rs. ${formatter.format(price)}"
}

fun View.snackMessage(context: Context, message: String) {
//    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
    val mSnackBar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    val view = mSnackBar.view
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    params.height = 100
    params.width = ViewGroup.LayoutParams.MATCH_PARENT
    view.layoutParams = params
    view.setBackgroundColor(Color.RED)
    val mainTextView = view.findViewById<View>(R.id.snackbar_text) as TextView
    mainTextView.setTextColor(Color.WHITE)
    mainTextView.gravity = Gravity.CENTER
    mSnackBar.show()
}

fun isEmailValid(email: String): Boolean {
    val pattern = Pattern.compile(".+@.+\\.[a-z]+")
    val matcher = pattern.matcher(email)
    return matcher.matches()
}

fun EditText.getQueryTextChangeStateFlow(): StateFlow<String> {

    val query = MutableStateFlow("")

    setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            query.value = v.text.toString()
            return@OnEditorActionListener true
        }
        false
    })
    return query
}

fun Activity.createListDialog(
    title: String,
    items: Array<String>,
    callback: DialogInterface.OnClickListener?,
): AlertDialog.Builder {
    // BUILDER
    return AlertDialog.Builder(this, R.style.MyAlertDialogTheme)
        .setTitle(title)
        .setItems(items, callback)
}

fun Activity.createMultiSelectListDialog(
    title: String,
    items: Array<String>,
    checkedItems: BooleanArray,
    callback: DialogInterface.OnClickListener?,
): AlertDialog.Builder {
    val mCheckedItems = BooleanArray(items.size)
    // BUILDER
    return AlertDialog.Builder(this)
        .setTitle(title)
        .setMultiChoiceItems(items, checkedItems) { dialogInterface, i, isChecked ->
            mCheckedItems[i] = isChecked
        }
        .setPositiveButton("Ok", callback)
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
}

fun Activity.showKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Activity.hideKeyboardOnStartUp() {
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}

fun Activity.askPermissionDialog(
    msg: String,
    permission: String,
) {
    val alertBuilder = AlertDialog.Builder(this)
    alertBuilder.setCancelable(true)
    alertBuilder.setTitle("Permission necessary")
    alertBuilder.setMessage("$msg permission is necessary")
    alertBuilder.setPositiveButton(
        android.R.string.yes
    ) { dialog, which ->
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            1
        )
    }
    val alert = alertBuilder.create()
    alert.show()
}

fun <T : Any> Fragment.setBackStackData(key: String, data: T, doBack: Boolean = true) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, data)
    if (doBack)
        findNavController().popBackStack()
}

fun <T : Any> Fragment.getBackStackData(key: String, result: (T) -> (Unit)) {
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
        ?.observe(viewLifecycleOwner) {
            result(it)
        }
}