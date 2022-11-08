package com.teamforce.thanksapp.presentation.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.teamforce.thanksapp.NotificationSharedViewModel
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.ActivityMainBinding
import com.teamforce.thanksapp.presentation.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), IMainAction {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    val viewModel: ProfileViewModel by viewModels()
    val notificationsSharedViewModel: NotificationSharedViewModel by viewModels()

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
        if (viewModel.isUserAuthorized()) {
            viewModel.loadUserProfile()
            navGraph.setStartDestination(R.id.mainFlowFragment)
        } else {
            navGraph.setStartDestination(R.id.signFlowFragment)
        }
        navController.graph = navGraph
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.isUserAuthorized()) {
            notificationsSharedViewModel.checkNotifications()
        }
    }


    override fun showSuccessSendingCoins(count: Int, message: String, name: String) {
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "MainActivity"
    }


}
