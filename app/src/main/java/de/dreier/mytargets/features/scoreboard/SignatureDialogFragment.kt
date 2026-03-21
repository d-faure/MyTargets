/*
 * Copyright (C) 2018 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.scoreboard

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.databinding.FragmentSignatureBinding
import de.dreier.mytargets.shared.models.db.Signature
import java.io.File
import java.io.FileOutputStream

class SignatureDialogFragment : DialogFragment() {

    private val signatureDAO by lazy { ApplicationInstance.db.signatureDAO() }
    private lateinit var binding: FragmentSignatureBinding
    private var signatureId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignatureBinding.inflate(inflater, container, false)
        val args = arguments
        signatureId = args!!.getLong(ARG_SIGNATURE_ID)
        val signature = signatureDAO.loadSignatureOrNull(signatureId) ?: return binding.root
        val defaultName = args.getString(ARG_DEFAULT_NAME)

        val draftBitmap = loadDraftBitmap()
        if (draftBitmap != null) {
            binding.signatureView.signatureBitmap = draftBitmap
        } else if (signature.isSigned) {
            binding.signatureView.signatureBitmap = signature.bitmap
        }
        binding.editName.setOnClickListener {
            MaterialDialog.Builder(requireContext())
                .title(R.string.name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(defaultName, signature.name) { _, input ->
                    signature.name = input.toString()
                    signatureDAO.updateSignature(signature)
                    binding.signer.text = signature.name
                }
                .negativeText(android.R.string.cancel)
                .show()
        }
        binding.signer.text = signature.getName(defaultName!!)
        binding.save.setOnClickListener {
            var bitmap: Bitmap? = null
            if (!binding.signatureView.isEmpty) {
                bitmap = binding.signatureView.transparentSignatureBitmap
            }
            signature.bitmap = bitmap
            signatureDAO.updateSignature(signature)
            deleteDraftBitmap()
            dismiss()
        }
        binding.clear.setOnClickListener {
            binding.signatureView.clear()
            deleteDraftBitmap()
        }
        isCancelable = false
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        if (::binding.isInitialized) {
            saveDraftBitmap()
        }
    }

    override fun onStart() {
        super.onStart()
        adjustDialogWidth()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustDialogWidth()
    }

    private fun adjustDialogWidth() {
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun saveDraftBitmap() {
        val file = getDraftFile()
        if (binding.signatureView.isEmpty) {
            deleteDraftBitmap()
            return
        }
        try {
            val bitmap = binding.signatureView.transparentSignatureBitmap
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        } catch (_: Exception) {
            // Ignore draft persistence errors to avoid interrupting signature flow.
        }
    }

    private fun loadDraftBitmap(): Bitmap? {
        val file = getDraftFile()
        if (!file.exists()) {
            return null
        }
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (_: Exception) {
            null
        }
    }

    private fun deleteDraftBitmap() {
        val file = getDraftFile()
        if (file.exists()) {
            file.delete()
        }
    }

    private fun getDraftFile(): File {
        return File(requireContext().cacheDir, "signature-draft-$signatureId.png")
    }

    companion object {
        private const val ARG_SIGNATURE_ID = "signature_id"
        private const val ARG_DEFAULT_NAME = "default_name"

        fun newInstance(signature: Signature, defaultName: String): SignatureDialogFragment {
            val fragment = SignatureDialogFragment()
            val args = Bundle()
            args.putLong(ARG_SIGNATURE_ID, signature.id)
            args.putString(ARG_DEFAULT_NAME, defaultName)
            fragment.arguments = args
            return fragment
        }
    }
}
