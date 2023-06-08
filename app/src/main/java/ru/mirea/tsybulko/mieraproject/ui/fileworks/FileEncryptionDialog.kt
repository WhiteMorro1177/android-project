package ru.mirea.tsybulko.mieraproject.ui.fileworks


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import ru.mirea.tsybulko.mieraproject.R
import java.io.FileOutputStream


class FileEncryptionDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        val fileName = EditText(context)
        fileName.hint = "set filename"
        val fileText = EditText(context)
        fileText.hint = "set text"
        layout.addView(fileName)
        layout.addView(fileText)
        val view = View(context)
        builder.setTitle("Create file")
            .setIcon(R.drawable.ic_launcher_foreground)
            .setView(layout)
            .setPositiveButton("Save") { dialog, which ->
                val string = fileText.text.toString()
                val outputStream: FileOutputStream
                try {
                    outputStream = activity!!.openFileOutput(
                        fileName.text.toString(),
                        Context.MODE_PRIVATE
                    )
                    outputStream.write(
                        FileWorksFragment.encryptMsg(
                            string,
                            FileWorksFragment.generateKey()
                        )
                    )
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        return builder.create()
    }
}