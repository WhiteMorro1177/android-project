package ru.mirea.tsybulko.mieraproject.ui.web_view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import ru.mirea.tsybulko.mieraproject.R
import ru.mirea.tsybulko.mieraproject.databinding.FragmentWebViewBinding


class WebViewFragment : Fragment() {
    private lateinit var binder: FragmentWebViewBinding

    private val homePageUrl = "https://google.com/"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_web_view, container, false)
        binder = FragmentWebViewBinding.bind(fragmentView)

        val webView = binder.browserWindow
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(homePageUrl)

        return fragmentView
    }
}