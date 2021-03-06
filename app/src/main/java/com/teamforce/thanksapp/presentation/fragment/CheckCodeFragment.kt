package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import com.teamforce.thanksapp.utils.UserDataRepository

class CheckCodeFragment : Fragment(), View.OnClickListener {

    private var viewModel: LoginViewModel = LoginViewModel
    var editText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.enter_btn) {
            viewModel.verifyCode(editText?.text.toString())
        }
    }

    private fun initViews(view: View) {
        val enterButton: AppCompatButton = view.findViewById(R.id.enter_btn)
        editText = view.findViewById(R.id.code_et)
        enterButton.setOnClickListener(this)
        viewModel.verifyError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            enterButton.isClickable = !it
        }
        viewModel.verifyResult.observe(viewLifecycleOwner) {
            if (it != null) {
                finishLogin(it.authtoken, it.telegram)
            }
        }
    }

    private fun finishLogin(authtoken: String?, telegram: String?) {
        UserDataRepository.getInstance()?.saveCredentials(requireContext(), authtoken, telegram)
        Log.d("Token", "??????????????")
        findNavController().navigate(R.id.action_checkCodeFragment_to_mainFlowFragment)
    }


    companion object {

        const val TAG = "CheckCodeFragment"

        @JvmStatic
        fun newInstance() = CheckCodeFragment()
    }
}
