package com.teamforce.thanksapp.presentation.fragment.feedScreen.fragmentsViewPager2

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.textfield.TextInputLayout
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentCommentsChallengeBinding
import com.teamforce.thanksapp.databinding.FragmentCommentsFeedBinding
import com.teamforce.thanksapp.presentation.adapter.challenge.ChallengeCommentAdapter
import com.teamforce.thanksapp.presentation.viewmodel.challenge.CommentsChallengeViewModel
import com.teamforce.thanksapp.presentation.viewmodel.feed.CommentsFeedViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.Consts.TRANSACTION_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CommentsFeedFragment : Fragment(R.layout.fragment_comments_feed) {

    private val binding: FragmentCommentsFeedBinding by viewBinding()
    private val viewModel: CommentsFeedViewModel by viewModels()
    private var listAdapter: ChallengeCommentAdapter? = null

    private var transactionId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transactionId = it.getInt(TRANSACTION_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter = ChallengeCommentAdapter(viewModel.getProfileId()!!, ::deleteComment)
        binding.commentsRv.adapter = listAdapter
        transactionId?.let { loadComment(it) }
        listeners()
    }

    private fun loadComment(transactionId: Int) {
        lifecycleScope.launch() {
            viewModel.loadComments(transactionId).collectLatest() {
                listAdapter?.submitData(it)
            }

        }
    }

    private fun createComment(challengeId: Int, text: String) {
        viewModel.createComment(challengeId, text)
    }

    private fun listeners() {
        inputMessage()
        viewModel.isLoading.observe(viewLifecycleOwner){ isLoading ->
            if(!isLoading) transactionId?.let { transactionId -> loadComment(transactionId) }
        }
    }

    private fun deleteComment(commentId: Int) {
        viewModel.deleteComment(commentId)
    }

    private fun inputMessage() {
        binding.messageValueEt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.trim().length > 0) {
                    binding.textFieldMessage.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    binding.textFieldMessage.endIconDrawable =
                        context?.getDrawable(R.drawable.ic_send_vector)
                } else {
                    binding.textFieldMessage.endIconDrawable =
                        context?.getDrawable(R.drawable.ic_emotion)
                }
                binding.textFieldMessage.setEndIconOnClickListener {
                    sendMessage()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        if (binding.messageValueEt.text?.trim().toString().isEmpty()) {
            // Запретить отправку
            binding.textFieldMessage.endIconMode = TextInputLayout.END_ICON_NONE
            binding.textFieldMessage.isEndIconCheckable = false
        }
        transactionId?.let { transactionId ->
            viewModel.createCommentsLoading.observe(viewLifecycleOwner) { loading ->
                if (!loading) loadComment(transactionId)
            }
        }
    }

    private fun sendMessage(){
        transactionId?.let { transactionId ->
            if(binding.messageValueEt.text?.trim()?.length!! > 0){
                createComment(transactionId, binding.messageValueEt.text.toString())
            }
            binding.messageValueEt.text?.clear()
            closeKeyboard()
        }
    }

    private fun closeKeyboard() {
        val view: View? = activity?.currentFocus
        if (view != null) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.commentsRv.adapter = null
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