package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentMainFlowBinding


class MainFlowFragment : BaseFlowFragment (
    R.layout.fragment_main_flow, R.id.nav_host_fragment_main
) {

    // private val binding by viewBinding(FragmentMainFlowFragmentBinding::bind)

    private var _binding: FragmentMainFlowBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainFlowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupNavigation(navController: NavController) {
        binding.bottomNavigation.setupWithNavController(navController)
//        binding.navView.setupWithNavController(navController)
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.feedFragment, R.id.balanceFragment, R.id.transactionFragment, R.id.historyFragment))
        val toolbar = binding.toolbar
        val collapsingToolbar = binding.collapsingToolbar
        collapsingToolbar.setupWithNavController(toolbar, navController, appBarConfiguration)


        binding.profile.setOnClickListener{
            navController.navigate(R.id.profileFragment)
        }
    }



//    override fun profilePage() {
//        val headerView = binding.navView.getHeaderView(0)
//        val headerLinearLayout = headerView.findViewById<LinearLayout>(R.id.profileFragment)
//        //val headerLinearLayout = headerView.findViewById<Button>(R.id.btn_out_account)
//        headerLinearLayout.setOnClickListener{
//            Toast.makeText(requireContext(), "clicked", Toast.LENGTH_SHORT).show();
//            activityNavController().navigateSafely(R.id.profileFragment)
//        }
//    }



}

