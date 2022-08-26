package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.UserBean
import com.teamforce.thanksapp.databinding.FragmentTransactionBinding
import com.teamforce.thanksapp.presentation.adapter.UsersAdapter
import com.teamforce.thanksapp.presentation.viewmodel.TransactionViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository


class TransactionFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private lateinit var viewModel: TransactionViewModel
    private lateinit var usersInput: TextInputEditText
    private lateinit var usersInputLayout: TextInputLayout
    private lateinit var countEditText: EditText
    private lateinit var reasonEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var sendCoinsGroup: LinearLayout
    private lateinit var availableCoins: TextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var checkBoxIsAnon: CheckBox
    private lateinit var progressBar: ProgressBar
    private var user: UserBean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shouldMeGoToHistoryFragment()
        viewModel = TransactionViewModel()
        viewModel.initViewModel()
        initViews(view)
        dropDownMenuUserInput(usersInput)
        appealToDB()
        checkedChip()
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
        val sendButton: Button = binding.sendCoinBtn
        sendButton.setOnClickListener(this)
        recyclerView = binding.usersListRv
        sendCoinsGroup = binding.sendCoinLinear
        countEditText = binding.countValueEt
        reasonEditText = binding.messageValueEt
        usersInputLayout = binding.textField
        usersInput = binding.usersEt
        availableCoins = binding.distributedValueTv
        chipGroup = binding.chipGroup
        checkBoxIsAnon = binding.isAnon
        progressBar = binding.progressBar

        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    recyclerView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        )
    }

    private fun shouldMeGoToHistoryFragment(){
        val bool = arguments?.getBoolean(Consts.SHOULD_ME_GOTO_HISTORY, false)
        bool?.let {
            if(it){
                findNavController().navigate(R.id.action_transactionFragment_to_historyFragment)
            }
        }
    }

    fun checkedChip(){
        Log.d("Token", "i'm inner chip function")
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.chipOne -> {
                    Log.d("Token", "i'm inner chip one")
                    countEditText.setText("1")
                }
                R.id.chipFive -> {
                    binding.chipFive.apply {
                        countEditText.setText("5")
                    }
                }
                R.id.chipTen -> {
                    binding.chipTen.apply {
                        countEditText.setText("10")
                    }
                }
                R.id.chipTwentyFive -> {
                    binding.chipTwentyFive.apply {
                        countEditText.setText("25")
                    }
                }
            }
        }



    }

    fun dropDownMenuUserInput(userInput: TextInputEditText){
        userInput.addTextChangedListener(object : TextWatcher {
            // TODO Возможно стоит будет оптимизировать вызов списка пользователей
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.trim().length > 0 && count > before && s.toString() != user?.tgName) {
                    UserDataRepository.getInstance()?.token?.let {
                        viewModel.loadUsersList(s.toString(), it)
                    }
                }else if(usersInput.text?.trim().toString().isEmpty()){
                    UserDataRepository.getInstance()?.token?.let {
                        viewModel.loadUsersListWithoutInput("true", it)
                    }
                } else {
                    recyclerView.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        if(usersInput.text?.trim().toString().isEmpty()){
            UserDataRepository.getInstance()?.token?.let {
                viewModel.loadUsersListWithoutInput("true", it)
            }
        }
    }

    fun appealToDB(){
        viewModel.users.observe(viewLifecycleOwner) {
            recyclerView.visibility = View.VISIBLE
            recyclerView.adapter = UsersAdapter(it, this)
        }
        viewModel.isSuccessOperation.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "Success!", Toast.LENGTH_LONG).show()
                showResultTransaction(
                    amountThanks = Integer.valueOf(countEditText.text.toString()),
                    receiverTg = user?.tgName.toString(),
                    receiverName = user?.firstname.toString(),
                    receiverSurname = user?.surname.toString(),
                    photo = "${Consts.BASE_URL}/media/${user?.photo}"
                )
            }
        }
        viewModel.sendCoinsError.observe(viewLifecycleOwner) {
            sendCoinsGroup.visibility = View.GONE
            usersInputLayout.visibility = View.VISIBLE
            //Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            val snack = Snackbar.make(requireView(), requireContext().resources.getString(R.string.unsuccessfulSendCoins), Snackbar.LENGTH_LONG)
                snack.setTextMaxLines(3)
                .setTextColor(context?.getColor(R.color.white)!!)
                .setAction(context?.getString(R.string.OK)!!){
                    snack.dismiss()
                }
                snack.show()
        }

        viewModel.usersLoadingError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(v: View?) {
        Log.d("Token", " View id -  ${v?.id}")
        if (v?.id == R.id.user_item) {
            recyclerView.visibility = View.GONE
            user = v.tag as UserBean
            usersInputLayout.visibility = View.GONE
            with(binding){
                if(!user?.photo.isNullOrEmpty()){
                    Glide.with(v)
                        .load("${Consts.BASE_URL}/media/${user?.photo}".toUri())
                        .centerCrop()
                        .into(receiverAvatar)
                }
                receiverTgName.text = user?.tgName
                receiverNameLabelTv.text = user?.firstname
                receiverSurnameLabelTv.text = user?.surname
            }
            sendCoinsGroup.visibility = View.VISIBLE
        } else if (v?.id == R.id.send_coin_btn) {
            val userId = user?.userId ?: -1
            val countText = countEditText.text.toString()
            val reason = reasonEditText.text.toString()
            val isAnon = when(checkBoxIsAnon.isChecked){
                true -> true
                else -> false
            }
                if (userId != -1 && countText.isNotEmpty() && reason.isNotEmpty()) {
                    try {
                        val count: Int = Integer.valueOf(countText)
                        UserDataRepository.getInstance()?.token?.let {
                            viewModel.sendCoins(it, userId, count, reason, isAnon)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                        //Toast.makeText(requireContext(), viewModel.sendCoinsError.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


    private fun showResultTransaction(amountThanks: Int, receiverTg: String, receiverName: String,
                                      receiverSurname: String, photo: String){
        usersInputLayout.editText?.setText("")
        countEditText.setText(R.string.empty)
        reasonEditText.setText(R.string.empty)
        sendCoinsGroup.visibility = View.GONE
        val bundle = Bundle()
        bundle.putInt(Consts.AMOUNT_THANKS, amountThanks)
        bundle.putString(Consts.RECEIVER_TG, receiverTg.trim())
        bundle.putString(Consts.RECEIVER_NAME, receiverName.trim())
        bundle.putString(Consts.RECEIVER_SURNAME, receiverSurname.trim())
        bundle.putString(Consts.AVATAR_USER, photo)
        findNavController().navigate(
            R.id.action_transactionFragment_to_transactionResultFragment,
            bundle
        )

    }
}
