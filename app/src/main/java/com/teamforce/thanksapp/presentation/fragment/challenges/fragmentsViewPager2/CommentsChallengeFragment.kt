package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.textfield.TextInputLayout
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentCommentsChallengeBinding
import com.teamforce.thanksapp.model.domain.CommentModel
import com.teamforce.thanksapp.presentation.adapter.CommentsAdapter
import com.teamforce.thanksapp.presentation.adapter.challenge.ChallengeCommentAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment
import com.teamforce.thanksapp.presentation.viewmodel.challenge.CommentsChallengeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommentsChallengeFragment : Fragment(R.layout.fragment_comments_challenge) {

    private val binding: FragmentCommentsChallengeBinding by viewBinding()
    private val viewModel: CommentsChallengeViewModel by viewModels()
    private var listAdapter: ChallengeCommentAdapter? = null

    private var idChallenge: Int? = null
    private var allComments: List<CommentModel> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idChallenge = it.getInt(ChallengesFragment.CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //listAdapter = ChallengeCommentAdapter()
        lifecycleScope.launch() {
            idChallenge?.let {
                viewModel.loadComments(it).collect() {

                }
            }
        }

        initRvAdapter()
        setCommentFromDb()
        listeners()
    }


    private fun initRvAdapter() {
        binding.commentsRv.adapter = CommentsAdapter(requireContext(), viewModel.getProfileId()!!)
    }

    private fun setCommentFromDb() {
        viewModel.comments.observe(viewLifecycleOwner) {
            allComments = it?.comments!!
            (binding.commentsRv.adapter as CommentsAdapter).submitList(it.comments)
        }
    }

    private fun createComment(challengeId: Int, text: String) {
        viewModel.createComment(challengeId, text)
    }

    private fun listeners() {
        inputMessage()

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
                    binding.textFieldMessage.setEndIconOnClickListener {
                        Log.d("Token", "Отправка сообщения")
                        idChallenge?.let { challengeId ->
                            createComment(challengeId, binding.messageValueEt.text.toString())
                            closeKeyboard()
                            binding.messageValueEt.text?.clear()

                        }

                    }
                } else {
                    binding.textFieldMessage.endIconMode = TextInputLayout.END_ICON_NONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        if (binding.messageValueEt.text?.trim().toString().isEmpty()) {
            // Запретить отправку
            binding.textFieldMessage.endIconMode = TextInputLayout.END_ICON_NONE
            binding.textFieldMessage.isEndIconCheckable = false
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
    }

    companion object {

        @JvmStatic
        fun newInstance(challengeId: Int) =
            CommentsChallengeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ChallengesFragment.CHALLENGER_ID, challengeId)
                }
            }
    }
}