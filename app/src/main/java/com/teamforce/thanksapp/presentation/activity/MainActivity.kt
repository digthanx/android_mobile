package com.teamforce.thanksapp.presentation.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationBarView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.presentation.fragment.BalanceFragment
import com.teamforce.thanksapp.presentation.fragment.HistoryFragment
import com.teamforce.thanksapp.presentation.fragment.TransactionFragment
import com.teamforce.thanksapp.utils.UserDataRepository

class MainActivity : AppCompatActivity(), IMainAction {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager
        val prefs: SharedPreferences = getSharedPreferences("com.teamforce.thanksapp", MODE_PRIVATE)
        val restoredText = prefs.getString("Token", null)
        if (restoredText != null) {
            UserDataRepository.getInstance()?.token = restoredText
            val fragment: Fragment?
            val ft: FragmentTransaction = fragmentManager.beginTransaction()
            fragment = BalanceFragment.newInstance()
            ft.add(R.id.container, fragment, BalanceFragment.TAG)
            ft.commit()
        } else {
            val intent = Intent(baseContext, LoginActivity::class.java)
            startActivity(intent)
        }
        val navigationView: NavigationBarView = findViewById(R.id.bottom_navigation)
        navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    val fragment: Fragment?
                    val ft: FragmentTransaction = fragmentManager.beginTransaction()
                    fragment = BalanceFragment.newInstance()
                    ft.replace(R.id.container, fragment, BalanceFragment.TAG)
                    ft.commit()
                    true
                }
                R.id.page_2 -> {
                    val fragment: Fragment?
                    val ft: FragmentTransaction = fragmentManager.beginTransaction()
                    fragment = TransactionFragment.newInstance()
                    ft.replace(R.id.container, fragment, TransactionFragment.TAG)
                    ft.commit()
                    true
                }
                R.id.page_3 -> {
                    val fragment: Fragment?
                    val ft: FragmentTransaction = fragmentManager.beginTransaction()
                    fragment = HistoryFragment()
                    ft.replace(R.id.container, fragment, HistoryFragment.TAG)
                    ft.commit()
                    true
                }
                else -> false
            }
        }
    }

    override fun showSuccessSendingCoins(count: Int, message: String, name: String) {
        TODO("Not yet implemented")
    }
}
