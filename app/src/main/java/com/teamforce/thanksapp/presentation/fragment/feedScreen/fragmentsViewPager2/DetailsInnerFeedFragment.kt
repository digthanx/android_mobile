package com.teamforce.thanksapp.presentation.fragment.feedScreen.fragmentsViewPager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.chip.Chip
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.entities.feed.Tag
import com.teamforce.thanksapp.databinding.FragmentDetailsInnerFeedBinding
import com.teamforce.thanksapp.presentation.viewmodel.feed.DetailInnerFeedViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.Consts.TRANSACTION_ID
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [DetailsInnerFeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DetailsInnerFeedFragment : Fragment(R.layout.fragment_details_inner_feed) {

    private val binding: FragmentDetailsInnerFeedBinding by viewBinding()

    private val viewModel: DetailInnerFeedViewModel by viewModels()

    private var transactionId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transactionId = it.getInt(TRANSACTION_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionId?.let { viewModel.loadTransactionDetail(it) }
        setDetail()
    }

    private fun setDetail(){
        viewModel.dataOfTransaction.observe(viewLifecycleOwner){
            with(binding){
                if(it?.reason != "") reasonTransaction.text = it?.reason
                else messageCard.visibility = View.GONE
                if (it != null) {
                    setTags(it.tags)
                }
                if(it?.photo != null){
                    photoTv.visibility = View.VISIBLE
                    cardViewImg.visibility = View.VISIBLE
                    Glide.with(requireContext())
                        .load("${Consts.BASE_URL}${it?.photo}".toUri())
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.senderImage)
                }else{
                    photoTv.visibility = View.GONE
                    cardViewImg.visibility = View.GONE
                }

            }
        }
    }


    private fun setTags(tagList: List<Tag>) {
        for (i in tagList.indices) {
            val tagName = tagList[i].name
            val chip: Chip = LayoutInflater.from(binding.tagsChipGroup.context)
                .inflate(
                    R.layout.chip_tag_example_in_history_transaction,
                    binding.tagsChipGroup,
                    false
                ) as Chip
            with(chip) {
                setText(tagName)
                setEnsureMinTouchTargetSize(true)
                minimumWidth = 0
            }
            binding.tagsChipGroup.addView(chip)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(transactionId: Int) =
            DetailsInnerFeedFragment().apply {
                arguments = Bundle().apply {
                    putInt(TRANSACTION_ID, transactionId)
                }
            }
    }
}