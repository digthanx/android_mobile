package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.presentation.activity.ILoginAction
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel

class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: LoginViewModel
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
    }

    private fun initViews(view: View) {
        val getCodeButton: AppCompatButton = view.findViewById(R.id.get_code_btn)
        editText = view.findViewById(R.id.telegram_et)
        getCodeButton.setOnClickListener(this)
        viewModel = (activity as ILoginAction).getViewModel()
        viewModel.authError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            getCodeButton.isClickable = !it
        }
        viewModel.isSuccessAuth.observe(viewLifecycleOwner) {
            if (it) {
                (activity as ILoginAction).showCheckCode()
            }
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.get_code_btn) {
            viewModel.authorizeUser(editText?.text.toString())
        }
    }

    companion object {

        const val TAG = "LoginFragment"

        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}
