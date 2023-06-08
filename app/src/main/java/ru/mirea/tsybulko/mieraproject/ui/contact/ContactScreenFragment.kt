package ru.mirea.tsybulko.mieraproject.ui.contact

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.mirea.tsybulko.mieraproject.databinding.FragmentContactScreenBinding
import android.Manifest.permission
import android.app.Activity.RESULT_OK
import android.content.Intent
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

    private lateinit var imageUri: Uri

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

        requestPermissions(permissionsToRequest.toTypedArray(), 200)
    }


}