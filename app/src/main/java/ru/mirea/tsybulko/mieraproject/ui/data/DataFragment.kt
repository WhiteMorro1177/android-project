package ru.mirea.tsybulko.mieraproject.ui.data

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.mirea.tsybulko.mieraproject.R
import ru.mirea.tsybulko.mieraproject.databinding.FragmentDataBinding

/*

    main theme: ???
        background: <picture>
        text: a little
        some buttons: for more info (or some actions)

*/


class DataFragment : Fragment() {
    private lateinit var binder: FragmentDataBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = FragmentDataBinding.bind(inflater.inflate(R.layout.fragment_data, container, false))



        // Inflate the layout for this fragment
        return binder.root.rootView
    }
}