package ru.mirea.tsybulko.mieraproject.ui.data

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import ru.mirea.tsybulko.mieraproject.R
import ru.mirea.tsybulko.mieraproject.databinding.FragmentDataBinding

class DataFragment : Fragment() {
    private lateinit var binder: FragmentDataBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_data, container, false)
        binder = FragmentDataBinding.bind(fragmentView)

        binder.moreInfoButton.setOnClickListener(View.OnClickListener {
            val catsUrl = Uri.parse("https://lapkins.ru/cat/")
            val intent: Intent = Intent(Intent.ACTION_VIEW, catsUrl)

            startActivity(intent)
        })

        return fragmentView
    }

}