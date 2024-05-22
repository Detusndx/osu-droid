package com.reco1l.osu.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.edlplan.framework.easing.Easing
import com.edlplan.ui.EasingHelper
import com.edlplan.ui.fragment.BaseFragment
import com.reco1l.toolkt.android.dp
import com.reco1l.toolkt.android.drawableLeft
import com.reco1l.toolkt.android.fontColor
import com.reco1l.toolkt.android.layoutWidth
import com.reco1l.toolkt.animation.TimeEasing
import com.reco1l.toolkt.animation.cancelAnimators
import com.reco1l.toolkt.animation.toAlpha
import com.reco1l.toolkt.animation.toScaleX
import com.reco1l.toolkt.animation.toScaleY
import com.reco1l.toolkt.animation.toTranslationX
import com.reco1l.toolkt.animation.toTranslationY
import ru.nsu.ccfit.zuev.osuplus.R


data class DialogButton(
    val text: String,
    val icon: Drawable? = null,
    val tint: Int = Color.WHITE,
    val clickListener: (MessageDialog) -> Unit
)

open class MessageDialog : BaseFragment() {


    override val layoutID = R.layout.dialog_message_fragment


    protected var title: CharSequence = "Alert"

    protected var message: CharSequence = ""

    protected var allowDismiss = true

    protected var onDismiss: (() -> Unit)? = null

    protected var buttons = mutableListOf<DialogButton>()


    override fun onLoadView() {

        findViewById<TextView>(R.id.title)!!.text = title
        findViewById<TextView>(R.id.message)?.text = message

        val buttonLayout = findViewById<LinearLayout>(R.id.button_layout)!!

        for (button in buttons) {
            buttonLayout.addView(Button(ContextThemeWrapper(context, R.style.button_borderless)).apply {

                minWidth = 300.dp
                gravity = Gravity.LEFT

                text = button.text
                fontColor = button.tint

                if (button.icon == null) {
                    compoundDrawablePadding = 0
                } else {
                    drawableLeft = button.icon
                    drawableLeft!!.setTint(button.tint)
                }

                setOnClickListener { button.clickListener(this@MessageDialog) }
            })
        }

        val background = findViewById<View>(R.id.frg_background)!!
        background.setOnClickListener { callDismissOnBackPress() }

        background.cancelAnimators()
            .toAlpha(0f)
            .toAlpha(1f, 200, ease = TimeEasing.DECELERATE)

        val body = findViewById<View>(R.id.frg_body)!!

        body.cancelAnimators()
            .toScaleX(0.9f)
            .toScaleY(0.9f)
            .toScaleX(1f, 300, ease = TimeEasing.BOUNCE_OUT)
            .toScaleY(1f, 300, ease = TimeEasing.BOUNCE_OUT)
    }


    /**
     * The text to be show displayed in the dialog title.
     */
    fun setTitle(text: String): MessageDialog {
        title = text
        return this
    }

    /**
     * The text to be show displayed in the dialog message.
     */
    fun setMessage(text: String): MessageDialog {
        message = text
        return this
    }

    /**
     * Whether the dialog is cancelable or not.
     */
    fun setAllowDismiss(value: Boolean): MessageDialog {
        allowDismiss = value
        return this
    }

    /**
     * The function to be called when the dialog is dismissed.
     */
    fun setOnDismiss(action: () -> Unit): MessageDialog {
        onDismiss = action
        return this
    }

    /**
     * Adds a new button.
     */
    @JvmOverloads
    open fun addButton(text: String, iconDrawable: Drawable? = null, tint: Int = Color.WHITE, clickListener: (MessageDialog) -> Unit): MessageDialog {
        buttons.add(DialogButton(text, iconDrawable, tint, clickListener))
        return this
    }


    override fun callDismissOnBackPress() {
        if (allowDismiss) {
            super.callDismissOnBackPress()
        }
    }

    override fun dismiss() {
        onDismiss?.invoke()
        super.dismiss()
    }
}


open class PromptDialog : MessageDialog() {

    override val layoutID = R.layout.dialog_input_fragment


    /**
     * The text input by user.
     */
    var input: String? = null
        protected set


    private var hint: String? = null

    private var onTextChanged: ((PromptDialog) -> Unit)? = null

    private var onTextInputBind: ((EditText) -> Unit)? = null


    override fun onLoadView() {

        super.onLoadView()

        findViewById<TextView>(R.id.message)!!.isVisible = message.isNotBlank()
        findViewById<EditText>(R.id.input)!!.apply {

            setText(input)

            doOnTextChanged { text, _, _, _ ->
                input = text.toString()
                onTextChanged?.invoke(this@PromptDialog)
            }

            onTextInputBind?.invoke(this)
        }

    }



    fun setInput(text: String?): PromptDialog {
        input = text
        return this
    }

    /**
     * The text to be show displayed in the input hint.
     */
    fun setHint(text: String): PromptDialog {
        hint = text
        return this
    }

    /**
     * The function to be called when the text input is changed.
     */
    fun setOnTextChanged(action: (PromptDialog) -> Unit): PromptDialog {
        onTextChanged = action
        return this
    }

    /**
     * The function to be called when the EditText is created.
     */
    fun setOnTextInputBind(action: (EditText) -> Unit): PromptDialog {
        onTextInputBind = action
        return this
    }


    override fun addButton(text: String, iconDrawable: Drawable?, tint: Int, clickListener: (MessageDialog) -> Unit): PromptDialog {
        return super.addButton(text, iconDrawable, tint, clickListener) as PromptDialog
    }

}



