package com.rungo.speedball.features.settings

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.rungo.speedball.databinding.DialogSettingsBinding

class SettingsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(requireActivity()).create()
        val binding = DialogSettingsBinding.inflate(LayoutInflater.from(context))
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.setView(binding.root)
        return alertDialog
    }

    interface DialogConfirmationListener {
        fun onSaveClickListener()
    }
}