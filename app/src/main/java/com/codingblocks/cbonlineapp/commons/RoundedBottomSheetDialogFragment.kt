package com.codingblocks.cbonlineapp.commons

import android.app.Dialog
import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * [BottomSheetDialogFragment] that uses a custom
 * theme which sets a rounded background to the dialog
 * and doesn't dim the navigation bar
 */
open class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.RoundedBottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)
}
