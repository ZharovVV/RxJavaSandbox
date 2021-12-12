package com.github.zharovvv.rxjavasandbox.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class RetainFragment : Fragment() {

    init {
        Log.i("RetainFragmentLifeCycle", "#constructor")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("RetainFragmentLifeCycle", "#onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        Log.i("RetainFragmentLifeCycle", "#onCreate")
    }

    abstract override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    abstract override fun onViewCreated(view: View, savedInstanceState: Bundle?)

    override fun onDestroy() {
        super.onDestroy()
        Log.i("RetainFragmentLifeCycle", "#onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("RetainFragmentLifeCycle", "#onDetach")
    }
}