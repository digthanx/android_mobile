package com.teamforce.thanksapp.presentation.fragment


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentTransactionResultBinding
import com.teamforce.thanksapp.utils.Consts.AMOUNT_THANKS
import com.teamforce.thanksapp.utils.Consts.DESCRIPTION_OF_TRANSACTION
import com.teamforce.thanksapp.utils.Consts.RECEIVER


class TransactionResultFragment : DialogFragment() {

    private var _binding: FragmentTransactionResultBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    // TODO: Rename and change types of parameters
    private var amountThanks: Int? = null
    private var description: String? = null
    private var receiver: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            amountThanks = it.getInt(AMOUNT_THANKS)
            description = it.getString(DESCRIPTION_OF_TRANSACTION)
            receiver = it.getString(RECEIVER)
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
//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.GONE
//        requireActivity().findViewById<Toolbar>(R.id.toolbar).visibility = View.GONE
        binding.tvAmountThanks.text = "-" + amountThanks.toString()
        binding.tvDescription.text = "\"" + description + "\""
        binding.tvReceiver.text = receiver
        binding.btnToTheBeginning.setOnClickListener {
            findNavController().navigate(R.id.action_transactionResultFragment_to_transactionFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onPause() {
        super.onPause()
//        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.VISIBLE
//        requireActivity().findViewById<Toolbar>(R.id.toolbar).visibility = View.VISIBLE
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionResultFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String) =
            TransactionResultFragment().apply {
                arguments = Bundle().apply {
                    putString(AMOUNT_THANKS, param1)
                    putString(DESCRIPTION_OF_TRANSACTION, param2)
                    putString(RECEIVER, param3)
                }
            }
    }
}