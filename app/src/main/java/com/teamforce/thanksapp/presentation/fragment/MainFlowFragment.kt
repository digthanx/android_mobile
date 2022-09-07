package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentMainFlowBinding


class MainFlowFragment : BaseFlowFragment(
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
        // binding.bottomNavigation.setupWithNavController(navController)
//        binding.navView.setupWithNavController(navController)

        // Неизвестно, можно ли так делать вкупе с тем, что я вручную все внизу описал, будем тестить
        binding.bottomNavigation.setupWithNavController(navController)


        val optionForTransaction = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
            .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
            .setPopEnterAnim(com.google.android.material.R.anim.abc_fade_in)
            .setPopExitAnim(com.google.android.material.R.anim.abc_fade_out)
            .build()

//        binding.profile.setOnClickListener{
//            navController.navigate(R.id.profileFragment, null, optionForProfileFragment)
//        }

        binding.bottomNavigation.menu.getItem(1).isChecked = true
        binding.bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.balanceFragment -> {
                    navController.navigate(R.id.balanceFragment, null, optionForTransaction)
                    return@OnItemSelectedListener true
                }
                R.id.feedFragment -> {
                    navController.navigate(R.id.feedFragment, null, optionForTransaction)
                    return@OnItemSelectedListener true
                }
                R.id.transactionFragment -> {
                    navController.navigate(R.id.transactionFragment, null, optionForTransaction)
                    return@OnItemSelectedListener true
                }
                R.id.historyFragment -> {
                    navController.navigate(R.id.historyFragment, null, optionForTransaction)
                    return@OnItemSelectedListener true
                }
            }
            true
        })

        binding.bottomNavigation.setOnItemReselectedListener(NavigationBarView.OnItemReselectedListener { item ->
            return@OnItemReselectedListener
        })
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

