package com.teamforce.thanksapp.presentation.fragment.feedScreen.fragmentsViewPager2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.Consts.TRANSACTION_ID
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CommentsFeedFragment : Fragment() {

    private var transactionId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transactionId = it.getInt(TRANSACTION_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comments_feed, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(transactionId: Int) =
            CommentsFeedFragment().apply {
                arguments = Bundle().apply {
                    putInt(Consts.TRANSACTION_ID, transactionId)
                }
            }
    }
}