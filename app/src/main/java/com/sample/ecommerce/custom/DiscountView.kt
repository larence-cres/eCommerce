package com.sample.ecommerce.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.sample.ecommerce.R
import kotlin.math.pow
import kotlin.math.sqrt


class DiscountView : LinearLayout {

    private lateinit var paintDiscount: Paint
    private lateinit var paintDisText: Paint
    private var discountNumber = ""
    private var isDiscount = true
    private lateinit var mRoot: View

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mRoot = LayoutInflater.from(context).inflate(R.layout.discount_view, this)
        setWillNotDraw(false)
        initView()
    }

    private fun initView() {
        paintDiscount = Paint()
        paintDisText = Paint()
        paintDisText.isAntiAlias = true
        paintDisText.color = Color.WHITE
        paintDisText.style = Paint.Style.FILL_AND_STROKE
        paintDiscount.color = Color.RED
        paintDiscount.style = Paint.Style.FILL
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val left: Int = mRoot.left
        val top: Int = mRoot.top
        val right: Int = mRoot.right
        val bottom: Int = mRoot.bottom
        val viewHeight = bottom - top
        val viewWidth = right - left

        if (isDiscount) {
            val path = Path()
            path.moveTo(0F, viewHeight / 4F)
            path.lineTo(viewWidth / 4F, 0F)
            path.lineTo(viewWidth / 2F, 0F)
            path.lineTo(0F, viewHeight / 2F)
            path.close()
            canvas.drawPath(path, paintDiscount)

            paintDisText.textSize = 35f
            paintDisText.textAlign = Paint.Align.LEFT
            val hOffset = (
                    sqrt(
                        (viewHeight / 4.0f).toDouble().pow(2.0) +
                                (viewWidth / 4.0f).toDouble().pow(2.0)
                    ) / 2.0f).toFloat() - 20f
            val vOffset = (
                    viewHeight * viewWidth * sqrt(
                        viewHeight.toDouble().pow(2.0) + viewWidth.toDouble().pow(2.0)
                    ) /
                            (viewHeight.toDouble().pow(2.0) + viewWidth.toDouble().pow(2.0)) / 6.0f
                    ).toFloat() + 3f
            canvas.drawTextOnPath(discountNumber, path, hOffset, vOffset, paintDisText)
        }
    }

    fun setDiscountNumber(discountNumber: String) {
        this.discountNumber = discountNumber
        invalidate()
    }

    fun setDisCountNumber(size: Float) {
        paintDisText.textSize = size
    }

    fun setDiscount(discount: Boolean) {
        isDiscount = discount
        invalidate()
    }
/*
    */
    /**
     * 设置较大文字
     *//*
    fun setTextBig(big: String?) {
        dataType!!.text = big
    }

    */
    /**
     * 设置较小文字
     *
     * @param small
     *//*
    fun setTextSamll(small: String?) {
        price!!.text = small
    }

    */
    /**
     * 设置较大文字大小
     *
     * @param size
     *//*
    fun setTextzieBig(size: Float) {
        dataType!!.textSize = size
    }

    */
    /**
     * 设置较小文字大小
     *
     * @param size
     *//*
    fun setTextzieSmall(size: Float) {
        price!!.textSize = size
    }*/
}