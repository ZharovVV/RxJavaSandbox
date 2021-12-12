package com.github.zharovvv.rxjavasandbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.zharovvv.rxjavasandbox.fragments.MainFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mainFragment: MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            mainFragment = MainFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, mainFragment, "mainFragment")
                .commit()
        } else {
            mainFragment = supportFragmentManager.findFragmentByTag("mainFragment") as MainFragment
        }
    }
}