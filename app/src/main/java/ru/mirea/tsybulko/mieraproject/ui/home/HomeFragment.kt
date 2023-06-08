package ru.mirea.tsybulko.mieraproject.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.json.JSONException
import org.json.JSONObject
import ru.mirea.tsybulko.mieraproject.databinding.FragmentHomeBinding
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val connectivityManager: ConnectivityManager =
            this.context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo = connectivityManager.activeNetworkInfo!!
        if (networkInfo.isConnected) {
            DownloadPageTask(binding).execute("https://api.open-meteo.com/v1/forecast?latitude=57.650&longitude=37.22&current_weather=true")
        } else {
            Toast.makeText(this.context, "No connection", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class DownloadPageTask(var binding: FragmentHomeBinding) :
        AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String {
            return try {
                downloadInfo(params[0]!!)
            } catch (e: IOException) {
                e.printStackTrace()
                "error"
            }
        }

        override fun onPostExecute(result: String?) {
            try {
                val responseJson = JSONObject(result!!)
                binding.textHome.text = responseJson.toString()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            super.onPostExecute(result)
        }

        private fun downloadInfo(address: String): String {
            var inputStream: InputStream? = null
            var data = ""
            try {
                (URL(address).openConnection() as HttpURLConnection).apply {
                    readTimeout = 100000
                    connectTimeout = 100000
                    requestMethod = "GET"
                    instanceFollowRedirects = true
                    useCaches = false
                    doInput = true

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inputStream = getInputStream()

                        val bos = ByteArrayOutputStream()
                        var read = 0
                        while (inputStream!!.read().also { read = it } != -1) {
                            bos.write(read)
                        }
                        bos.close()
                        data = bos.toString()
                        disconnect()
                    } else {
                        data = "$responseMessage. Error Code: $responseCode"
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (inputStream != null) {
                    inputStream!!.close()
                }
            }
            return data
        }
    }
}