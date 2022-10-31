package com.teamforce.thanksapp.presentation.fragment.feedScreen

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.net.toUri
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputLayout
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentAdditionalInfoFeedItemBinding
import com.teamforce.thanksapp.model.domain.CommentModel
import com.teamforce.thanksapp.presentation.adapter.CommentsAdapter
import com.teamforce.thanksapp.presentation.adapter.challenge.FragmentDetailChallengeStateAdapter
import com.teamforce.thanksapp.presentation.adapter.feed.DetailFeedStateAdapter
import com.teamforce.thanksapp.presentation.viewmodel.AdditionalInfoFeedItemViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.Consts.AVATAR_USER
import com.teamforce.thanksapp.utils.Consts.RECEIVER_SURNAME
import com.teamforce.thanksapp.utils.Consts.TRANSACTION_ID
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.username
import com.teamforce.thanksapp.utils.viewSinglePhoto
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdditionalInfoFeedItemFragment : Fragment() {

    private var _binding: FragmentAdditionalInfoFeedItemBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private val viewModel: AdditionalInfoFeedItemViewModel by viewModels()


    private var userIdReceiver: Int? = null
    private var userIdSender: Int? = null
    private var likesCount: Int? = null
    private var likesCountReal: Int = 0
    private var isLikedInner: Boolean? = null
    private var transactionId: Int? = null
    private var recipientAvatar: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transactionId = it.getInt(TRANSACTION_ID)
            recipientAvatar = it.getString(AVATAR_USER)
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
        initTopBar()
        initTabLayoutMediator()
        loadDataFromDb()
        setBaseInfo()
        listeners()

    }

    private fun initTopBar() {
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
    }

    private fun initTabLayoutMediator() {
        val detailInnerAdapter = DetailFeedStateAdapter(requireActivity())

        detailInnerAdapter.setTransactionId(transactionId)

        binding.pager.adapter = detailInnerAdapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.text = context?.getString(R.string.details)
                1 -> tab.text = context?.getString(R.string.comments)
                2 -> tab.text = context?.getString(R.string.reactions)
            }
        }.attach()
    }

    private fun loadDataFromDb() {
        transactionId?.let { viewModel.loadTransactionDetail(it) }
    }

    private fun listeners() {
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
                val mapReaction: Map<String, Int> = mapOf(
                    "like_kind" to 1,
                    "transaction" to it
                )
                viewModel.pressLike(mapReaction)
                updateOutlookLike()
            }
        }

    }


    private fun updateOutlookLike() {
        if (isLikedInner != null) {
            isLikedInner = !isLikedInner!!
            if (isLikedInner == true) {
                likesCountReal += 1
                binding.likeBtn.text = likesCountReal.toString()
                binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_success_secondary))
            } else {
                likesCountReal -= 1
                binding.likeBtn.text = likesCountReal.toString()
                binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_info_secondary))

            }
        }
    }

    private fun setBaseInfo() {
        with(binding) {
            viewModel.dataOfTransaction.observe(viewLifecycleOwner) {
                dateTransactionTv.text = it?.updated_at
                if (it?.sender_tg_name?.equals(viewModel.getProfileUserName()) == true) {
                    descriptionTransactionWhoSent.text = context?.getString(R.string.fromYou)
                } else {
                    descriptionTransactionWhoSent.text = it?.sender_tg_name?.username()
                }
                if (it?.recipient_tg_name?.equals(viewModel.getProfileUserName()) == true) {
                    descriptionTransactionWhoReceived.text = context?.getString(R.string.you)
                } else {
                    descriptionTransactionWhoReceived.text =
                        it?.recipient_tg_name?.username()
                }
                descriptionTransactionWhatDid.text = requireContext().getString(R.string.gotFrom)
                amountThanks.text = requireContext().getString(
                    R.string.amountThanks,
                    it?.amount.toString()
                )
                setAvatar(it?.recipient_photo)
                userIdReceiver = it?.recipient_id
                userIdSender = it?.sender_id
                setLikes(it?.like_amount, it?.user_liked)

                // Переход к просмотру фото
                binding.userAvatar.setOnClickListener { view ->
                    it?.recipient_photo?.let { photo ->
                        (view as ShapeableImageView).viewSinglePhoto(photo, requireContext())
                    }
                    Glide.with(requireContext())
                        .load("${Consts.BASE_URL}${it?.recipient_photo}".toUri())
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.ic_anon_avatar)
                        .error(R.drawable.ic_anon_avatar)
                        .into(binding.userAvatar)
                }
            }
        }
    }

    private fun setAvatar(avatarRecipient: String?) {
        avatarRecipient?.let {
            if (!it.contains("null")) {
                Glide.with(requireContext())
                    .load("${Consts.BASE_URL}${avatarRecipient}".toUri())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.userAvatar)
            }
        }

    }

    private fun setLikes(likes: Int?, isLiked: Boolean?) {
        likes?.let { likes ->
            likesCountReal = likes
        }
        isLiked?.let { isLiked -> isLikedInner = isLiked }
        binding.likeBtn.text = likesCountReal.toString()
        if (isLikedInner == true) {
            binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_success_secondary))
        } else {
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
    }
}