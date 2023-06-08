package ru.mirea.tsybulko.mieraproject.ui.contact

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.mirea.tsybulko.mieraproject.databinding.FragmentContactScreenBinding
import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import ru.mirea.tsybulko.mieraproject.R
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ContactScreenFragment : Fragment() {
    private lateinit var binding: FragmentContactScreenBinding
    private var imageUri: Uri? = null

    companion object {
        const val PREFERENCES_TAG = "shared_prefs"

        const val NAME_TAG = "saved_name"
        const val ORG_TAG = "saved_org_name"
        const val NUMBER_TAG = "saved_number"
        const val IMAGE_URI = ""
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootFragmentView = inflater.inflate(R.layout.fragment_contact_screen, container, false)
        binding = FragmentContactScreenBinding.bind(rootFragmentView)

        val requiredPermissions = arrayOf(
            permission.CAMERA,
            permission.WRITE_EXTERNAL_STORAGE
        )

        checkAndRequestPermissions(requiredPermissions)

        val callback: ActivityResultCallback<ActivityResult> =
            ActivityResultCallback<ActivityResult> { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    binding.imageView.setImageURI(imageUri)
                }
            }
        val cameraActivityResultLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                callback
            )

        // set profile photo
        binding.imageView.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                val photoFile: File = saveImage()
                val authorities = "${this.context!!.packageName}.fileprovider"
                imageUri = FileProvider.getUriForFile(this.context!!, authorities, photoFile)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                cameraActivityResultLauncher.launch(cameraIntent)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val fragment = this.context!!

        val prefs: SharedPreferences =
            this.context!!.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE).apply {
                binding.editTextName.setText(this.getString(NAME_TAG, ""))
                binding.editTextOrg.setText(this.getString(ORG_TAG, ""))
                binding.editTextTelephone.setText(this.getString(NUMBER_TAG, ""))

                binding.imageView.setImageURI(
                    Uri.parse(
                        this.getString(
                            IMAGE_URI,
                            fragment.getDrawable(R.drawable.baseline_camera_alt_24).toString()
                        )
                    )
                )
            }

        // save fields data
        binding.buttonSave.setOnClickListener {
            prefs.edit().run {
                putString(NAME_TAG, binding.editTextName.text.toString())
                putString(ORG_TAG, binding.editTextOrg.text.toString())
                putString(NUMBER_TAG, binding.editTextTelephone.text.toString())
                putString(IMAGE_URI, if (imageUri == null) "" else imageUri.toString())
                apply()
            }
        }

        return rootFragmentView
    }

    private fun saveImage(): File {
        val imageFileName =
            "IMAGE_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}_"
        val storageDirectory = this.context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDirectory)
    }

    private fun checkAndRequestPermissions(permissionsToCheck: Array<String>) {
        val permissionsToRequest: ArrayList<String> = ArrayList()
        for (permission in permissionsToCheck) {
            if (ContextCompat.checkSelfPermission(
                    this.context!!,
                    permission
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty())
            requestPermissions(permissionsToRequest.toTypedArray(), 200)
    }


}