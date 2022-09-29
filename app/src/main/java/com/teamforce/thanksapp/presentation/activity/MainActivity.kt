package com.teamforce.thanksapp.presentation.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.SharedPreferences
import com.teamforce.thanksapp.databinding.ActivityMainBinding
import com.teamforce.thanksapp.presentation.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
        val prefs: android.content.SharedPreferences? =
            getSharedPreferences("com.teamforce.thanksapp", MODE_PRIVATE)
        val restoredUsername = prefs?.getString("Username", null)
        if (viewModel.isUserAuthorized()) {
            viewModel.userDataRepository.username =
                restoredUsername //todo исправить доступ к этому полу через shared preferences
            viewModel.loadUserProfile()
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
