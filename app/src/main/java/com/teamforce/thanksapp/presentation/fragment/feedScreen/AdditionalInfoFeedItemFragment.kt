package com.teamforce.thanksapp.presentation.fragment.feedScreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentAdditionalInfoFeedItemBinding
import com.teamforce.thanksapp.presentation.viewmodel.AdditionalInfoFeedItemViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository


class AdditionalInfoFeedItemFragment : Fragment() {

    private var _binding: FragmentAdditionalInfoFeedItemBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }
    val viewModel: AdditionalInfoFeedItemViewModel by viewModels()

    private var dateTransaction: String? = null
    private var avatarReceiver: String? = null
    private var descrFeed: String? = null
    private var senderTg: String? = null
    private var receiverTg: String? = null
    private var amount: String? = null
    private var photo: String? = null
    private var reason: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dateTransaction = it.getString(Consts.DATE_TRANSACTION)
            avatarReceiver = it.getString(Consts.AVATAR_USER)
            descrFeed = it.getString(Consts.DESCRIPTION_FEED)
            senderTg = it.getString(Consts.SENDER_TG)
            receiverTg = it.getString(Consts.RECEIVER_TG)
            amount = it.getString(Consts.AMOUNT_THANKS)
            photo = it.getString(Consts.PHOTO_TRANSACTION)
            reason = it.getString(Consts.REASON_TRANSACTION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdditionalInfoFeedItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.balanceFragment,
                R.id.feedFragment,
                R.id.transactionFragment,
                R.id.historyFragment
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        with(binding) {
            dateTransactionTv.text = dateTransaction
            if (senderTg?.equals(viewModel.userDataRepository.username) == true) {
                descriptionTransactionWhoSent.text = context?.getString(R.string.fromYou)
            } else {
                descriptionTransactionWhoSent.text = context?.getString(
                    R.string.tgName
                )?.let { String.format(it, senderTg) }
            }
            if (receiverTg?.equals(viewModel.userDataRepository.username) == true) {
                descriptionTransactionWhoReceived.text = context?.getString(R.string.you)
            } else {
                descriptionTransactionWhoReceived.text = context?.getString(
                    R.string.tgName
                )?.let { String.format(it, receiverTg) }
            }
            descriptionTransactionWhatDid.text = descrFeed
            amountThanks.text = context?.getString(R.string.amountThanks)?.let {
                String.format(it, amount)
            }
            reasonTransaction.text = reason
        }
        if (!avatarReceiver?.contains("null")!!) {
            Glide.with(this)
                .load(avatarReceiver?.toUri())
                .centerCrop()
                .into(binding.userAvatar)
        }
        if (!photo.isNullOrEmpty()) {
            Log.d("Token", "${photo}")
            binding.photoTv.visibility = View.VISIBLE
            binding.cardViewImg.visibility = View.VISIBLE
            Glide.with(binding.senderImage.context)
                .load("${Consts.BASE_URL}${photo}".toUri())
                .centerCrop()
                .into(binding.senderImage)
        } else {
            binding.photoTv.visibility = View.GONE
            binding.cardViewImg.visibility = View.GONE
        }
    }
}