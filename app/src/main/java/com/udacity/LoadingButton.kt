package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private lateinit var buttonText: String
    private var CProgress = 0f
    private var BProgress = 0f
    private var valueAnimator = ValueAnimator()

    /// Custom Attribute
    private var borderColor = Color.BLACK
    private var borderWidth = 4.0f





    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                valueAnimator.duration = 2500
                valueAnimator.addUpdateListener {
                    CProgress = it.animatedValue as Float
                    BProgress = (widthSize.toFloat() / 500) * CProgress
                    invalidate()
                }
                valueAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        CProgress = 0f
                        BProgress = 0f
                    }
                })

                valueAnimator.start()
                invalidate()
            }
            ButtonState.Loading -> {
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                buttonText = resources.getString(R.string.downloading)
                invalidate()
            }
        }

    }

    init {
        buttonText = resources.getString(R.string.downloading)
    }

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private val loadingBackgroundColor =
        ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    private val textColor = ResourcesCompat.getColor(resources, R.color.white, null)


      private val paint = Paint().apply {
        isAntiAlias = true
        color = backgroundColor
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = textColor
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawButton(canvas)
        drawProgress(canvas)
        drawText(canvas)

    }

    private fun drawButton(canvas: Canvas?) {
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth

        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
    }

    private fun drawText(canvas: Canvas?) {
        val textWidth = textPaint.measureText(buttonText)
        canvas?.drawText(
            buttonText,
            widthSize / 2 - textWidth / 2,
            heightSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2,
            textPaint
        )

    }

    private fun drawProgress(canvas: Canvas?) {
        val bPaint = Paint().apply {
            isAntiAlias = true
            color = loadingBackgroundColor
        }
        canvas?.drawRect(0f, 0f, BProgress, heightSize.toFloat(), bPaint)

        val textWidth = textPaint.measureText(resources.getString(R.string.button_loading))
        val x = widthSize / 2 + textWidth / 1.5f
        val y = heightSize / 2 - textWidth / 4

        val oval = RectF(
            x, y,
            width.toFloat() - x / 7, height.toFloat() - y
        )

        val cPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.YELLOW
            strokeWidth = 3f
        }

        canvas?.drawArc(oval, 0f, CProgress, true, cPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}

