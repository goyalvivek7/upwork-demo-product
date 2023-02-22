package com.example.products.view
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.products.R

class CustomProgressBar {
    lateinit var dialog:ProgressDialog
    fun show(context: Context): Dialog {
        return show(context, "Loading..")
    }
    fun show(context: Context, title: CharSequence?): Dialog {
        dialog = ProgressDialog.show(context, null, null, true)
        dialog.setContentView(R.layout.custom_progress_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        if (dialog != null && !dialog.isShowing) {
            dialog.dismiss()
            dialog.show()
        }
        return dialog
    }
}