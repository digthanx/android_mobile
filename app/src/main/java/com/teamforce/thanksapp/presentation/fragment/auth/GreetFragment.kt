package com.teamforce.thanksapp.presentation.fragment.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentGreetBinding
import com.teamforce.thanksapp.presentation.viewmodel.auth.GreetViewModel
import com.teamforce.thanksapp.utils.OptionsTransaction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GreetFragment : Fragment(R.layout.fragment_greet) {


    private val binding: FragmentGreetBinding by viewBinding()

    private val viewModel: GreetViewModel by viewModels()


    private var xCode: String? = null
    private var xId: String? = null
    private var orgId: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            xCode = getXcode()
            orgId = getOrgId()
            xId = getXId()
        }
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_greetFragment_to_loginFragment)
        }
        // Эта проверка на то что нужен переход не надежная, тк их нужно удалять при первом
        // входе чтобы была возможность зайти хотя бы
        // Добавить выбор организаций при авторизации
        // При авторизации при смене орг токен неверно сохраняется и все запросы
        // становятся forbidden 403
        if(xId != null && xCode != null && orgId != null){
            val bundle = Bundle()
            bundle.putString(XCODE, xCode)
            bundle.putString(XID, xId)
            bundle.putString(ORGID, orgId)
            xCode = null
            orgId = null
            xId = null
            findNavController().navigate(
                R.id.action_greetFragment_to_changeOrganizationFragment,
                bundle,
                OptionsTransaction().optionForEditProfile)
        }
    }

    companion object {
        private const val XCODE = "xCode"
        private const val XID = "xId"
        private const val ORGID = "organization_id"


        @JvmStatic
        fun newInstance(xCode: String, xId: String, orgId: String) =
            GreetFragment().apply {
                arguments = Bundle().apply {
                    putString(XCODE, xCode)
                    putString(XID, xId)
                    putString(ORGID, orgId)
                }
            }
    }

}