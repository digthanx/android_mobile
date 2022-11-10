package com.teamforce.thanksapp.presentation.fragment.newTransactionScreen


import android.os.Bundle
import android.view.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentTransactionResultBinding
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.Consts.AMOUNT_THANKS
import com.teamforce.thanksapp.utils.Consts.AVATAR_USER
import com.teamforce.thanksapp.utils.Consts.RECEIVER_NAME
import com.teamforce.thanksapp.utils.Consts.RECEIVER_SURNAME
import com.teamforce.thanksapp.utils.Consts.RECEIVER_TG
import com.teamforce.thanksapp.utils.OptionsTransaction


class TransactionResultFragment : Fragment() {

    private var _binding: FragmentTransactionResultBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private var amountThanks: Int? = null
    private var receiverTg: String? = null
    private var receiverName: String? = null
    private var receiverSurname: String? = null
    private var receiverPhoto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            amountThanks = it.getInt(AMOUNT_THANKS)
            receiverTg = it.getString(RECEIVER_TG)
            receiverName = it.getString(RECEIVER_NAME)
            receiverPhoto = it.getString(AVATAR_USER)
            receiverSurname = it.getString(RECEIVER_SURNAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionResultBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.amount.text = " $amountThanks"
        binding.receiverTgName.text  = receiverTg
        binding.receiverNameLabelTv.text  = receiverName
        binding.receiverSurnameLabelTv.text  = receiverSurname
        if(!receiverPhoto?.contains("null")!!){
            Glide.with(view)
                .load(receiverPhoto?.toUri())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.receiverAvatar)
        }else{
            binding.receiverAvatar.setImageResource(R.drawable.ic_anon_avatar)
        }

        binding.btnToTheBeginning.setOnClickListener {
            findNavController().navigate(
                R.id.action_transactionResultFragment_to_transactionFragment,
                null, OptionsTransaction().optionForResult)
        }
        binding.btnToTheHistory.setOnClickListener {

            var bundle = Bundle()
            bundle.putBoolean(Consts.SHOULD_ME_GOTO_HISTORY, true)
            findNavController().navigate(
                R.id.action_transactionResultFragment_to_transactionFragment,
                bundle,
                OptionsTransaction().optionForResult
            )
        }
    }




}