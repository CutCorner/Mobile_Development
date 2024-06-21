package com.example.cepstun.cv

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.cepstun.R

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var visibleOn: Drawable
    private var visibleOff: Drawable

    private var visible: Boolean

    private var isEmailFormatValid: Boolean = true
    private var isNameFormatValid: Boolean = true
    private var isPostAddressFormatValid: Boolean = true
    private var isPhoneFormatValid: Boolean = true

    private var clearButtonImage: Drawable

    init {
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable

        visibleOff = ContextCompat.getDrawable(context, R.drawable.visible_off) as Drawable
        visibleOn = ContextCompat.getDrawable(context, R.drawable.visible_on) as Drawable

        when (inputType){
            InputType.TYPE_TEXT_VARIATION_PASSWORD, 129 -> {
                setButtonDrawables(endOfTheText = visibleOff)
            }
        }

        visible = false
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                // Do nothing.
            }
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                    when (inputType){
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, 33 -> {
                            if (p0.toString().isNotEmpty()){
                                showClearButton()
                            } else {
                                hideClearButton()
                                isEmailFormatValid =
                                    Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").matches(p0.toString())

                                error = if (isEmailFormatValid) null else context.getString(R.string.email_format_invalid)
                            }
                        }
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME, 97 ->{
                            if (p0.toString().isNotEmpty()){
                                showClearButton()
                            } else {
                                hideClearButton()
                                isNameFormatValid =
                                    Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").matches(p0.toString())

                                error = if (isNameFormatValid) null else context.getString(R.string.name_format_invalid)
                            }
                        }
                        InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS, 113 ->{
                            if (p0.toString().isNotEmpty()){
                                showClearButton()
                            } else {
                                hideClearButton()
                                isPostAddressFormatValid =
                                    Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").matches(p0.toString())

                                error = if (isPostAddressFormatValid) null else context.getString(R.string.address_format_invalid)
                            }
                        }
                        InputType.TYPE_CLASS_PHONE ->{
                            if (p0.toString().isEmpty()){
                                isPhoneFormatValid = p0.toString().length <= 14 && Regex("[0-9]+").matches(p0.toString())

                                error = if (isPhoneFormatValid) null else context.getString(R.string.number_format_invalid)
                            }
                        }
//                        else -> Log.d("inputType apa ya selain diatas","$inputType") // for check, delete if not used
                    }
            }
            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText, topOfTheText, endOfTheText, bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            when (inputType){
                InputType.TYPE_TEXT_VARIATION_PASSWORD, 129 -> {
                    val visibleButtonStart: Float
                    val visibleButtonEnd: Float
                    var isVisibleButtonClicked = false

                    if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                        visibleButtonEnd = (visibleOff.intrinsicWidth + paddingStart).toFloat()
                        when {
                            event.x < visibleButtonEnd -> isVisibleButtonClicked = true
                        }
                    } else {
                        visibleButtonStart = (width - paddingEnd - visibleOff.intrinsicWidth).toFloat()
                        when {
                            event.x > visibleButtonStart -> isVisibleButtonClicked = true
                        }
                    }

                    if (isVisibleButtonClicked) {
                        return when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                setButtonDrawables(endOfTheText = if (visible) visibleOn else visibleOff)
                                true
                            }

                            MotionEvent.ACTION_UP -> {
                                val cursorPosition = selectionStart
                                visible = !visible
                                transformationMethod =
                                    if (visible) null else PasswordTransformationMethod.getInstance()
                                if (visible){
                                    setButtonDrawables(endOfTheText = visibleOn)
                                } else {
                                    setButtonDrawables(endOfTheText = visibleOff)
                                }
                                setSelection(cursorPosition)
                                true
                            }
                            else -> false
                        }
                    } else return false
                }
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, 33 -> {
                    val clearButtonStart: Float
                    val clearButtonEnd: Float
                    var isClearButtonClicked = false

                    if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                        clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                        when {
                            event.x < clearButtonEnd -> isClearButtonClicked = true
                        }
                    } else {
                        clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                        when {
                            event.x > clearButtonStart -> isClearButtonClicked = true
                        }
                    }

                    if (isClearButtonClicked) {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                                showClearButton()
                                return true
                            }
                            MotionEvent.ACTION_UP -> {
                                clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                                when {
                                    text != null -> text?.clear()
                                }
                                hideClearButton()
                                return true
                            }
                            else -> return false
                        }
                    }
                }
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME, 97 -> {
                    val clearButtonStart: Float
                    val clearButtonEnd: Float
                    var isClearButtonClicked = false

                    if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                        clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                        when {
                            event.x < clearButtonEnd -> isClearButtonClicked = true
                        }
                    } else {
                        clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                        when {
                            event.x > clearButtonStart -> isClearButtonClicked = true
                        }
                    }

                    if (isClearButtonClicked) {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                                showClearButton()
                                return true
                            }
                            MotionEvent.ACTION_UP -> {
                                clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                                when {
                                    text != null -> text?.clear()
                                }
                                hideClearButton()
                                return true
                            }
                            else -> return false
                        }
                    }
                }
                InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS, 113 ->{
                    val clearButtonStart: Float
                    val clearButtonEnd: Float
                    var isClearButtonClicked = false

                    if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                        clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                        when {
                            event.x < clearButtonEnd -> isClearButtonClicked = true
                        }
                    } else {
                        clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                        when {
                            event.x > clearButtonStart -> isClearButtonClicked = true
                        }
                    }

                    if (isClearButtonClicked) {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                                showClearButton()
                                return true
                            }
                            MotionEvent.ACTION_UP -> {
                                clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                                when {
                                    text != null -> text?.clear()
                                }
                                hideClearButton()
                                return true
                            }
                            else -> return false
                        }
                    }
                }
            }

        }
        return false
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }
    private fun hideClearButton() {
        setButtonDrawables()
    }
}