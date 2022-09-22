package com.teamforce.thanksapp.presentation.fragment.feedScreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentAdditionalInfoFeedItemBinding
import com.teamforce.thanksapp.presentation.viewmodel.AdditionalInfoFeedItemViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.UserDataRepository


class AdditionalInfoFeedItemFragment : Fragment() {

    private var _binding: FragmentAdditionalInfoFeedItemBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private val viewModel = AdditionalInfoFeedItemViewModel()

    private var dateTransaction: String? = null
    private var avatarReceiver: String? = null
    private var descrFeed: String? = null
    private var senderTg: String? = null
    private var receiverTg: String? = null
    private var amount: String? = null
    private var photo: String? = null
    private var reason: String? = null
    private val username = UserDataRepository.getInstance()?.username
    private var userIdReceiver: Int? = null
    private var userIdSender: Int? = null
    private var likesCount: Int? = null
    private var dislikesCount: Int? = null
    private var likesCountReal: Int = 0
    private var dislikesCountReal: Int = 0
    private var isLiked: Boolean? = null
    private var isDisliked: Boolean? = null
    private var transactionId: Int? = null

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
            userIdReceiver = it.getInt("userIdReceiver")
            userIdSender = it.getInt("userIdSender")
            likesCount = it.getInt(LIKES_COUNT)
            dislikesCount = it.getInt(DISLIKES_COUNT)
            isLiked = it.getBoolean(IS_LIKED)
            isDisliked = it.getBoolean(IS_DISLIKED)
            transactionId = it.getInt(TRANSACTION_ID)
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
        viewModel.initViewModel()
        setBaseInfo()
        setPhoto()
        setLikesAndDislikes()


        binding.descriptionTransactionWhoReceived.setOnClickListener {
            if (userIdReceiver != 0) {
                userIdReceiver?.let {
                    transactionToAnotherProfile(it)
                }
            }

        }

        binding.descriptionTransactionWhoSent.setOnClickListener {
            if (userIdSender != 0) {
                userIdSender?.let {
                    transactionToAnotherProfile(it)
                }
            }
        }
        binding.likeBtn.setOnClickListener {
            transactionId?.let {
                val mapReaction: Map<String, Int> = mapOf("like_kind" to 1,
                    "transaction" to it)
                viewModel.pressLike(mapReaction)
                updateOutlookLike()
            }
        }

        binding.dislikeBtn.setOnClickListener {
            transactionId?.let {
                val mapReaction: Map<String, Int> = mapOf("like_kind" to 2,
                    "transaction" to it)
                viewModel.pressLike(mapReaction)
                updateOutlookDislike()
            }
        }
    }

    private fun updateOutlookLike(){
        if(isLiked != null && isDisliked != null){
            isLiked = !isLiked!!
            if (isLiked == true){
                likesCountReal += 1
                binding.likeBtn.text = likesCountReal.toString()
                binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_success_secondary))
                if(isDisliked == true){
                    isDisliked = false
                    binding.dislikeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_info_secondary))
                    dislikesCountReal -= 1
                    binding.dislikeBtn.text = dislikesCountReal.toString()
                    return
                }
            }else{
                likesCountReal -= 1
                binding.likeBtn.text =likesCountReal.toString()
                binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_info_secondary))

            }
        }
    }
    private fun updateOutlookDislike(){
        if(isLiked != null && isDisliked != null){
            isDisliked = !isDisliked!!
            if (isDisliked == true){
                dislikesCountReal += 1
                binding.dislikeBtn.text = dislikesCountReal.toString()
                binding.dislikeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_error_secondary))
                if(isLiked == true){
                    isLiked = false
                    binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_info_secondary))
                    likesCountReal -= 1
                    binding.likeBtn.text = likesCountReal.toString()
                }
            }else{
                dislikesCountReal -= 1
                binding.dislikeBtn.text = dislikesCountReal.toString()
                binding.dislikeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_info_secondary))

            }
        }
    }

    private fun setBaseInfo() {
        with(binding) {
            dateTransactionTv.text = dateTransaction
            if (senderTg?.equals(username) == true) {
                descriptionTransactionWhoSent.text = context?.getString(R.string.fromYou)
            } else {
                descriptionTransactionWhoSent.text = context?.getString(
                    R.string.tgName
                )?.let { String.format(it, senderTg) }
            }
            if (receiverTg?.equals(username) == true) {
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
    }

    private fun setPhoto() {
        if (!avatarReceiver?.contains("null")!!) {
            Glide.with(this)
                .load(avatarReceiver?.toUri())
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
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

    private fun setLikesAndDislikes() {
        likesCount?.let { likes ->
            dislikesCount?.let { dislikes ->
                likesCountReal = likesCount!!
                dislikesCountReal = dislikesCount!!
            }
        }
        binding.likeBtn.text = likesCountReal.toString()
        binding.dislikeBtn.text = dislikesCountReal.toString()
        if (isLiked == true) {
            binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_success_secondary))
            binding.dislikeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_info_secondary))
        } else if (isDisliked == true) {
            binding.dislikeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_error_secondary))
            binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_info_secondary))
        } else {
            binding.dislikeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_info_secondary))
            binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_info_secondary))
        }
    }

    private fun transactionToAnotherProfile(userId: Int) {
        val bundle: Bundle = Bundle()
        bundle.putInt("userId", userId)
        findNavController().navigate(
            R.id.action_additionalInfoFeedItemFragment_to_someonesProfileFragment2,
            bundle, OptionsTransaction().optionForProfileFragment
        )
    }

    companion object {
        val LIKES_COUNT = "likesCount"
        val DISLIKES_COUNT = "dislikesCount"
        val IS_LIKED = "isLiked"
        val IS_DISLIKED = "isDisliked"
        val TRANSACTION_ID = "transactionId"
    }
}