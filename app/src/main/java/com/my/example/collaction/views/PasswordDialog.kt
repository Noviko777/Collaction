package com.my.example.collaction.views

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.my.example.collaction.R

class PasswordDialog : DialogFragment() {

    private lateinit var mListener: Listener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_password, null)
        view.findViewById<Button>(R.id.ok_btn).setOnClickListener {
            val view2 = (context as Activity).currentFocus
            if (view2 != null) {
                val imm = (context as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            mListener.onConfirmPassword(view.findViewById<EditText>(R.id.password_text).text.toString())
            dismiss()
        }
        return AlertDialog.Builder(context!!).setView(view).create()
    }

    interface Listener {
        fun onConfirmPassword(password: String)
    }
}