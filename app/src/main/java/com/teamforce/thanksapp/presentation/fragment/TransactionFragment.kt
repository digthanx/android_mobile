package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.UserBean
import com.teamforce.thanksapp.presentation.adapter.UsersAdapter
import com.teamforce.thanksapp.presentation.viewmodel.TransactionViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository
import java.lang.Exception


class TransactionFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var usersInput: TextInputEditText
    private lateinit var usersInputLayout: TextInputLayout
    private lateinit var countEditText: EditText
    private lateinit var reasonEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardViewForRecyclerView: MaterialCardView
    private lateinit var sendCoinsGroup: Group
    private lateinit var availableCoins: TextView
    private var user: UserBean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = TransactionViewModel()
        viewModel.initViewModel()
        initViews(view)
        val token = UserDataRepository.getInstance()?.token
        if (token != null) {
            viewModel.loadUserBalance(token)
        }
        viewModel.balance.observe(viewLifecycleOwner){
            UserDataRepository.getInstance()?.leastCoins = it.distribute.amount
            availableCoins.text = it.distribute.amount.toString()
        }
    }

    private fun initViews(view: View) {
        val sendButton: Button = view.findViewById(R.id.send_coin_btn)
        sendButton.setOnClickListener(this)
        recyclerView = view.findViewById(R.id.users_list_rv)
        cardViewForRecyclerView = view.findViewById(R.id.card_view_for_rv)
        sendCoinsGroup = view.findViewById(R.id.send_coins_group)
        countEditText = view.findViewById(R.id.count_value_et)
        reasonEditText = view.findViewById(R.id.message_value_et)
        usersInputLayout = view.findViewById(R.id.textField)
        usersInput = view.findViewById(R.id.users_et)
        availableCoins = view.findViewById(R.id.distributed_value_tv)
        usersInput.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 2 && count > before && s.toString() != user?.tgName) {
                    UserDataRepository.getInstance()?.token?.let {
                        viewModel.loadUsersList(s.toString(), it)
                    }
                } else {
                    recyclerView.visibility = View.GONE
                    cardViewForRecyclerView.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })
        viewModel.users.observe(viewLifecycleOwner) {
            cardViewForRecyclerView.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            recyclerView.adapter = UsersAdapter(it, this)
        }
        viewModel.isSuccessOperation.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "Success!", Toast.LENGTH_LONG).show()
                usersInputLayout.editText?.setText("")
                countEditText.setText(R.string.empty)
                reasonEditText.setText(R.string.empty)
                sendCoinsGroup.visibility = View.GONE
            }
        }
        viewModel.sendCoinsError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
        viewModel.usersLoadingError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.user_item) {
            cardViewForRecyclerView.visibility = View.GONE
            recyclerView.visibility = View.GONE
            user = v.tag as UserBean
            usersInputLayout.editText?.setText(user?.tgName)
            sendCoinsGroup.visibility = View.VISIBLE
        } else if (v?.id == R.id.send_coin_btn) {
            val userId = user?.userId ?: -1
            val countText = countEditText.text.toString()
            val reason = reasonEditText.text.toString()
            if (userId != -1 && countText.isNotEmpty() && reason.isNotEmpty()) {
                try {
                    val count: Int = Integer.valueOf(countText)
                    UserDataRepository.getInstance()?.token?.let {
                        viewModel.sendCoins(it, userId, count, reason)
                        showResultTransaction(
                            amountThanks = count,
                            description = reasonEditText.text.toString(),
                            receiver = usersInput.text.toString()
                        )
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun showResultTransaction(amountThanks: Int, description: String, receiver: String ){
        val bundle = Bundle()
        bundle.putInt(Consts.AMOUNT_THANKS, amountThanks)
        bundle.putString(Consts.DESCRIPTION_OF_TRANSACTION, description.trim())
        bundle.putString(Consts.RECEIVER, receiver.trim())
        findNavController().navigate(
            R.id.action_transactionFragment_to_transactionResultFragment,
            bundle
        )
    }

    private fun logout() {
        UserDataRepository.getInstance()?.logout(requireContext())
        activity?.recreate()
    }

    companion object {

        const val TAG = "TransactionFragment"

        @JvmStatic
        fun newInstance() = TransactionFragment()
    }
}
