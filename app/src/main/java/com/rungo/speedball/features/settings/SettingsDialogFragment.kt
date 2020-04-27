package com.rungo.speedball.features.settings

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class SettingsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(requireActivity())

        return alertDialog.create()
    }

    interface DialogConfirmationListener {
        fun onSaveClickListener()
    }
}