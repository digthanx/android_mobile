package com.teamforce.thanksapp.presentation.fragment

import android.content.Intent
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
import com.teamforce.thanksapp.presentation.activity.MainActivity
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import com.teamforce.thanksapp.utils.UserDataRepository

class CheckCodeFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: LoginViewModel
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
        viewModel = (activity as ILoginAction).getViewModel()
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
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    companion object {

        const val TAG = "CheckCodeFragment"

        @JvmStatic
        fun newInstance() = CheckCodeFragment()
    }
}
