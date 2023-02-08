package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText = ""
    private var buttonWidth = 0
    private var circleAngel = 0f
    private var buttonBackgroundColor = 0
    private var buttonTextColor = 0
    private var buttonAnimationColor = 0
    private var circleAnimator = ValueAnimator()
    private var buttonAnimator = ValueAnimator()


    private val paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
        typeface = Typeface.create("", Typeface.BOLD)
    }

    //Paint Object for the circle
    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = context.getColor(R.color.colorAccent)
    }

    //Paint Object for Button Text
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = resources.getDimension(R.dimen.text_size_large)
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, old, new ->
        when (new) {
            ButtonState.Loading -> {
                // change button text when the Animation start
                buttonText = resources.getString(R.string.button_loading)
                circleAnimator = ValueAnimator.ofFloat(0f, 360f)
                    .apply {
                        duration = 3000
                        repeatCount = ValueAnimator.INFINITE
                        repeatMode = ValueAnimator.REVERSE
                        interpolator = DecelerateInterpolator(1f)
                        addUpdateListener {
                            circleAngel = animatedValue as Float

                            invalidate()
                        }
                    }
                buttonAnimator = ValueAnimator.ofInt(0, widthSize)
                    .apply {
                        duration = 3000
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = 1
                        addUpdateListener {
                            buttonWidth = animatedValue as Int
                            invalidate()
                        }
                    }
                circleAnimator.start()
                buttonAnimator.start()
            }

            ButtonState.Completed -> {
                stopAnimation()
            }
        }
    }

    init {
        isClickable = true
        buttonText = resources.getText(R.string.download_text).toString()
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,
            0
        )
        buttonBackgroundColor = a.getColor(R.styleable.LoadingButton_buttonBackground, Color.GRAY)
        buttonTextColor = a.getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
        buttonAnimationColor = a.getColor(R.styleable.LoadingButton_animationColor, Color.GRAY)
        buttonState = ButtonState.Clicked

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawMainRectangle(canvas)
        if (buttonState == ButtonState.Loading) {
            drawAnimatedRectangle(canvas)
            drawAnimatedCircle(canvas)
        }

        drawButtonText(canvas)

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun stopAnimation() {
        buttonText = resources.getString(R.string.download_text)
        buttonWidth = 0
        circleAnimator.end()
        buttonAnimator.end()
    }

    private fun drawMainRectangle(canvas: Canvas?) {
        paintButton.color = buttonBackgroundColor
        canvas?.drawRect(
            0f,
            0f, widthSize.toFloat(),
            heightSize.toFloat(),
            paintButton
        )
    }

    private fun drawAnimatedRectangle(canvas: Canvas?) {
        paintButton.color = buttonAnimationColor
        canvas!!.drawRect(
            0f,
            0f,
            widthSize.toFloat() * buttonWidth / 100, heightSize.toFloat(),
            paintButton
        )
    }

    private fun drawButtonText(canvas: Canvas?) {
        paintText.textAlign = Paint.Align.CENTER
        paintText.color = buttonTextColor
        canvas?.drawText(
            buttonText,
            widthSize.toFloat() / 2,
            heightSize / 1.7f,
            paintText
        )
    }

    private fun drawAnimatedCircle(canvas: Canvas?) {
        canvas?.drawArc(
            widthSize - 140f,
            heightSize / 2 - 40f,
            widthSize - 75f,
            heightSize / 2 + 40f,
            0f,
            circleAngel,
            true, paintCircle
        )
    }

}