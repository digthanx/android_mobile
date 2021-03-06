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
import com.teamforce.thanksapp.presentation.activity.ILoginAction
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import com.teamforce.thanksapp.utils.UserDataRepository

class LoginFragment : Fragment(), View.OnClickListener, ILoginAction {

    private var viewModel: LoginViewModel = LoginViewModel
    var editText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        viewModel.initViewModel()
    }

    private fun initViews(view: View) {
        val getCodeButton: AppCompatButton = view.findViewById(R.id.get_code_btn)
        editText = view.findViewById(R.id.telegram_et)
        getCodeButton.setOnClickListener(this)
        viewModel.authError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            getCodeButton.isClickable = !it
        }
        viewModel.isSuccessAuth.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_loginFragment_to_checkCodeFragment)
            }
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.get_code_btn) {
            UserDataRepository.getInstance()?.username = editText?.text.toString()
            viewModel.authorizeUser(editText?.text.toString())
        }
    }

    companion object {

        const val TAG = "LoginFragment"

        @JvmStatic
        fun newInstance() = LoginFragment()

    }

    override fun showCheckCode() {
        TODO("Not yet implemented")
    }

    override fun getViewModel(): LoginViewModel {
        return viewModel
    }
}
