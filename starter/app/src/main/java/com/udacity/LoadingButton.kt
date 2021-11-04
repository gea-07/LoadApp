package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var pointPosition: PointF = PointF(0.0f, 0.0f)
    private val loadingRectangle = Rect()
    private var loadingProgress = 0
    private var textColor = 0
    private var arcColor = 0

    private var valueAnimator = ValueAnimator()

    public var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(2000)
                    .apply {
                        addUpdateListener {
                            loadingProgress = it.animatedValue as Int
                            invalidate()
                        }
                        repeatCount = ValueAnimator.INFINITE
                        repeatMode = ValueAnimator.RESTART

                        start()
                    }
            }
            ButtonState.Completed -> {

            }
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            arcColor = getColor(R.styleable.LoadingButton_arcColor, 0)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = resources.getColor(R.color.colorPrimary)

        // get the canvas size
        val canvasRect = Rect()
        canvas?.getClipBounds(canvasRect)
        val canvasHeight = canvasRect.height()
        val canvasWidth = canvasRect.width()

        // Draw the rectnagle and label that says "Download"
        canvas?.drawRect(canvasRect, paint)
        paint.color = textColor
        val label = resources.getString(R.string.download_string)
        pointPosition.computeXYForLabel()
        canvas?.drawText(label, pointPosition.x, pointPosition.y + 15, paint)

        if (buttonState == ButtonState.Loading) {
            // Draw the underlying original rectangle
            paint.color = resources.getColor(R.color.colorPrimary)
            canvas?.drawRect(canvasRect, paint)

            // Draw the darker animated rectangle using value animator over the original rect
            paint.color = resources.getColor(R.color.colorPrimaryDark)
            loadingRectangle.set(0,0, width * loadingProgress / 360, height)
            canvas?.drawRect(loadingRectangle, paint)

            // Draw the label "we are loading"
            paint.color = textColor
            val label = resources.getString(R.string.we_are_loading)
            pointPosition.computeXYForLabel()
            canvas?.drawText(label, pointPosition.x, pointPosition.y + 15, paint)

            // Draw the arc
            paint.color = arcColor
            // Calculate the "we are loading" rect so we can place the arc right after it
            val textRect = Rect()
            paint.getTextBounds(label, 0, label.length, textRect)
            val rectF = RectF()
            val arcWidth = 30F
            rectF.set(
                (canvasWidth / 2f + textRect.width()) - arcWidth,
                (canvasHeight/2) - arcWidth,
                (canvasWidth / 2f + textRect.width()) + arcWidth,
                (canvasHeight/2) + arcWidth
            )

            canvas?.drawArc(
                rectF,
                90F, (loadingProgress).toFloat(), true, paint
            )
        }
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

    private fun PointF.computeXYForLabel() {
        x = (widthSize / 2 ).toFloat()
        y = (heightSize / 2).toFloat()
    }
}