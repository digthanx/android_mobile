package com.teamforce.thanksapp.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.presentation.fragment.CheckCodeFragment
import com.teamforce.thanksapp.presentation.fragment.LoginFragment
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity(), ILoginAction {

    private val isLogined: Boolean = false
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = LoginViewModel()
        viewModel.initViewModel()
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragment: Fragment?
        val ft: FragmentTransaction = fragmentManager.beginTransaction()
        if (isLogined) {
            finish()
        } else {
            fragment = LoginFragment.newInstance()
            ft.add(R.id.container, fragment, LoginFragment.TAG)
        }
        ft.commit()
    }

    override fun onBackPressed() {
        // do nothing
    }

    override fun showCheckCode() {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment: Fragment = CheckCodeFragment.newInstance()
        ft.replace(R.id.container, fragment, CheckCodeFragment.TAG)
        ft.commit()
    }

    override fun getViewModel(): LoginViewModel {
        return viewModel
    }
}
