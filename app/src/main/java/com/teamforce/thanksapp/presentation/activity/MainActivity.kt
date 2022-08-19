package com.teamforce.thanksapp.presentation.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.ActivityMainBinding
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import com.teamforce.thanksapp.presentation.viewmodel.ProfileViewModel
import com.teamforce.thanksapp.utils.UserDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), IMainAction {


    private var _binding: ActivityMainBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    val viewModel: ProfileViewModel by viewModels()

    private val navController by lazy {
        val navFragment =
            supportFragmentManager.findFragmentById(R.id.navContainer) as NavHostFragment
        navFragment.findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        val prefs: SharedPreferences = getSharedPreferences("com.teamforce.thanksapp", MODE_PRIVATE)
        val restoredText = prefs.getString("Token", null)
        Log.d("Token", "Token ${restoredText}")
        if (restoredText != null) {
            UserDataRepository.getInstance()?.token = restoredText
            viewModel.initViewModel()
            viewModel.loadUserProfile(restoredText)
            viewModel.profile.observe(
                this,
                Observer {
                    if(it.profile.tgName != "null"){
                        UserDataRepository.getInstance()?.username = it.profile.tgName
                        Log.d("Token", "Имя пользователя ------- ${it.profile.tgName}")
                    }
                }
            )
            navGraph.setStartDestination(R.id.mainFlowFragment)
        } else {
            navGraph.setStartDestination(R.id.signFlowFragment)
        }
        navController.graph = navGraph
    }


    override fun showSuccessSendingCoins(count: Int, message: String, name: String) {
        TODO("Not yet implemented")
    }


}
