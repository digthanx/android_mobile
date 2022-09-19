package com.teamforce.thanksapp.presentation.fragment.newTransactionScreen

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.UserBean
import com.teamforce.thanksapp.databinding.FragmentTransactionBinding
import com.teamforce.thanksapp.model.domain.TagModel
import com.teamforce.thanksapp.presentation.adapter.UsersAdapter
import com.teamforce.thanksapp.presentation.adapter.ValuesAdapter
import com.teamforce.thanksapp.presentation.fragment.profileScreen.ProfileFragment
import com.teamforce.thanksapp.presentation.viewmodel.TransactionViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.UserDataRepository
import com.teamforce.thanksapp.utils.getPath
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class TransactionFragment : Fragment(R.layout.fragment_transaction), View.OnClickListener {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentTransactionBinding by viewBinding()

    private var viewModel: TransactionViewModel = TransactionViewModel()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.grant_permission),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }


    private var listValues: List<TagModel> = listOf()
    private var listCheckedValues: MutableList<TagModel> = mutableListOf()
    private var listCheckedIdTags: MutableList<Int> = mutableListOf()

    private var imageFilePart: MultipartBody.Part? = null
    private var user: UserBean? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.balanceFragment,
                R.id.feedFragment,
                R.id.historyFragment
            )
        )
        binding.toolbarTransaction.setupWithNavController(navController, appBarConfiguration)
        shouldMeGoToHistoryFragment()
        viewModel = TransactionViewModel()
        viewModel.initViewModel()
        initViews(view)
        dropDownMenuUserInput(binding.usersEt)
        loadValuesFromDB()
        setValuesFromDb()
        appealToDB()
        checkedChip()
        openValuesEt()
        val token = UserDataRepository.getInstance()?.token
        if (token != null) {
            viewModel.loadUserBalance(token)
        }
        viewModel.balance.observe(viewLifecycleOwner) {
            UserDataRepository.getInstance()?.leastCoins = it.distribute.amount
            binding.distributedValueTv.text = it.distribute.amount.toString()
        }

        binding.attachImageBtn.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            addPhotoFromIntent()
        }

        binding.detachImgBtn.setOnClickListener {
            binding.showAttachedImgCard.visibility = View.GONE
            imageFilePart = null
        }
        binding.etValue.inputType = InputType.TYPE_NULL
        binding.etValue.setOnClickListener {
            clearTags()
            createDialog(listValues, listCheckedValues)
        }
        binding.etValue.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                clearTags()
                createDialog(listValues, listCheckedValues)
            }
        }

    }

    private fun tagsToIdTags() {
        listCheckedValues.forEach {
            listCheckedIdTags.add(it.id)
        }
        Log.d("Token", "Список id tags ${listCheckedIdTags}")
    }

    private fun clearTags() {
        binding.tagsChipGroup.removeAllViews()
        binding.etValue.setText("")
    }

    private fun clearTagsWithListOfCheckedTags() {
        clearTags()
        listCheckedValues.clear()
    }

    private fun setTags(tagList: MutableList<TagModel>) {
        for (i in tagList.indices) {
            val tagModel = tagList[i]
            val tagName = tagList[i].name
            val chip: Chip = LayoutInflater.from(binding.tagsChipGroup.context)
                .inflate(
                    R.layout.chip_tag_example_transaction_tag,
                    binding.tagsChipGroup,
                    false
                ) as Chip
            with(chip) {
                setText(tagName)
                setEnsureMinTouchTargetSize(true)
                minimumWidth = 0
                setOnCloseIconClickListener {
                    val anim = AlphaAnimation(1f, 0f)
                    anim.duration = 250
                    anim.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {}

                        override fun onAnimationEnd(animation: Animation?) {
                            binding.tagsChipGroup.removeView(it)
                        }

                        override fun onAnimationStart(animation: Animation?) {}
                    })

                    it.startAnimation(anim)
                    tagList.remove(tagModel)
                    setTextInValuesEditText()
                }
            }

            binding.tagsChipGroup.addView(chip)
            setTextInValuesEditText()
        }
    }

    private fun setTextInValuesEditText() {
        if (listCheckedValues.size > 0) {
            binding.etValue.setText(
                String.format(
                    requireContext().getString(R.string.addedSomeValues), listCheckedValues.size
                )
            )
        } else if (listCheckedValues.size == 1) {
            binding.etValue.setText(
                String.format(
                    requireContext().getString(R.string.addedOneValue), listCheckedValues.size
                )
            )
        } else {
            binding.etValue.setText("")
        }
    }

    private fun loadValuesFromDB() {
        UserDataRepository.getInstance()?.token?.let { token ->
            viewModel.loadTags(token)
        }
    }

    private fun setValuesFromDb() {
        viewModel.tags.observe(viewLifecycleOwner) {
            Log.d("Token", " Вывод тегов ${it}")
            listValues = it
        }
    }

    private fun createDialog(list: List<TagModel>, listOfCheckedValues: MutableList<TagModel>) {
        val builderDialog = AlertDialog.Builder(context, R.style.FullscreenDialogTheme)
        val inflater = requireActivity().layoutInflater
        val newListValues = inflater.inflate(R.layout.fragment_list_of_values, null)
        val recyclerViewDialog = newListValues.findViewById<RecyclerView>(R.id.values_rv)
        val adapter = ValuesAdapter(list, listCheckedValues, requireContext())
        recyclerViewDialog.adapter = adapter
        builderDialog.setView(newListValues)
            .setPositiveButton(
                getString(R.string.applyValues),
                DialogInterface.OnClickListener { dialog, which ->
                    listCheckedValues = adapter.listCheckedValues
                    setTags(listCheckedValues)
                    Log.d("Token", " Список выбранных ценностей ${listCheckedValues}")
                    dialog.cancel()
                })
            .setNeutralButton(
                getString(R.string.applyValues),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })

        val dialog = builderDialog.create()

        dialog.show()
        val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        neutralButton.visibility = View.GONE
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val parent: LinearLayout = positiveButton.parent as LinearLayout
        parent.gravity = Gravity.CENTER_HORIZONTAL
        val leftSpacer = parent.getChildAt(1)
        leftSpacer.visibility = View.GONE
    }

    private fun openValuesEt() {
        binding.addValues.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.textInputLayoutValue.visibility = View.VISIBLE
            } else {
                binding.textInputLayoutValue.visibility = View.GONE
                clearTagsWithListOfCheckedTags()
            }
        }
    }


    private fun initViews(view: View) {
        binding.sendCoinBtn.setOnClickListener(this)
        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    binding.usersListRv.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.usersListRv.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }

            }
        )
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                Log.d(ProfileFragment.TAG, "${result.data?.data}:")
                val path = getPath(requireContext(), result.data?.data!!)
                val imageUri = result.data!!.data
                if (imageUri != null && path != null) {
                    binding.showAttachedImgCard.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(imageUri)
                        .centerCrop()
                        .into(binding.image)
                    uriToMultipart(imageUri, path)
                }

            }
        }

    private fun addPhotoFromIntent() {
        val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickIntent.type = "image/*"
        resultLauncher.launch(pickIntent)
    }


    private fun uriToMultipart(imageURI: Uri, filePath: String) {
        // Хардовая вставка картинки с самого начала
        // Убрать как только сделаю обновление по свайпам
        Glide.with(this)
            .load(imageURI)
            .centerCrop()
            .into(binding.image)
        val file = File(filePath)
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        imageFilePart = body
    }

    private fun shouldMeGoToHistoryFragment() {
        val bool = arguments?.getBoolean(Consts.SHOULD_ME_GOTO_HISTORY, false)
        bool?.let {
            if (it) {
                findNavController().navigate(R.id.action_transactionFragment_to_historyFragment)
            }
        }
    }

    fun checkedChip() {
        Log.d("Token", "i'm inner chip function")
        binding.chipGroupThanks.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chipOne -> {
                    Log.d("Token", "i'm inner chip one")
                    binding.countValueEt.setText("1")
                }
                R.id.chipFive -> {
                    binding.chipFive.apply {
                        binding.countValueEt.setText("5")
                    }
                }
                R.id.chipTen -> {
                    binding.chipTen.apply {
                        binding.countValueEt.setText("10")
                    }
                }
                R.id.chipTwentyFive -> {
                    binding.chipTwentyFive.apply {
                        binding.countValueEt.setText("25")
                    }
                }
            }
        }


    }

    fun dropDownMenuUserInput(userInput: TextInputEditText) {
        userInput.addTextChangedListener(object : TextWatcher {
            // TODO Возможно стоит будет оптимизировать вызов списка пользователей
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.trim().length > 0 && count > before && s.toString() != user?.tgName) {
                    UserDataRepository.getInstance()?.token?.let {
                        viewModel.loadUsersList(s.toString(), it)
                    }
                } else if (binding.usersEt.text?.trim().toString().isEmpty()) {
                    UserDataRepository.getInstance()?.token?.let {
                        viewModel.loadUsersListWithoutInput("true", it)
                    }
                } else {
                    binding.usersListRv.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        if (binding.usersEt.text?.trim().toString().isEmpty()) {
            UserDataRepository.getInstance()?.token?.let {
                viewModel.loadUsersListWithoutInput("true", it)
            }
        }
    }

    fun appealToDB() {
        viewModel.users.observe(viewLifecycleOwner) {
            binding.usersListRv.visibility = View.VISIBLE
            binding.usersListRv.adapter = UsersAdapter(it, this)
        }
        viewModel.isSuccessOperation.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "Success!", Toast.LENGTH_LONG).show()
                // Log.d("Token", " Юзер для передачи данных в результат ${user}")
                showResultTransaction(
                    amountThanks = Integer.valueOf(binding.countValueEt.text.toString()),
                    receiverTg = user?.tgName.toString(),
                    receiverName = user?.firstname.toString(),
                    receiverSurname = user?.surname.toString(),
                    photo = "${Consts.BASE_URL}/media/${user?.photo}"
                )
            }
        }
        viewModel.sendCoinsError.observe(viewLifecycleOwner) {
//            binding.sendCoinLinear.visibility = View.GONE
//            binding.textField.visibility = View.VISIBLE
            binding.messageValueEt.setText("")
            binding.countValueEt.setText("")
            //Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            val snack = Snackbar.make(
                requireView(),
                it,
                Snackbar.LENGTH_LONG
            )
            binding.sendCoinBtn.isClickable = true
            binding.sendCoinBtn.isEnabled = true
            snack.setTextMaxLines(3)
                .setTextColor(context?.getColor(R.color.white)!!)
                .setAction(context?.getString(R.string.OK)!!) {
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
            binding.usersListRv.visibility = View.GONE
            user = v.tag as UserBean
            binding.textField.visibility = View.GONE
            with(binding) {
                if (!user?.photo.isNullOrEmpty()) {
                    Glide.with(v)
                        .load("${Consts.BASE_URL}/media/${user?.photo}".toUri())
                        .centerCrop()
                        .into(receiverAvatar)
                }
                receiverTgName.text = user?.tgName
                receiverNameLabelTv.text = user?.firstname
                receiverSurnameLabelTv.text = user?.surname
            }
            binding.sendCoinLinear.visibility = View.VISIBLE
        } else if (v?.id == R.id.send_coin_btn) {
            val userId = user?.userId ?: -1
            val countText = binding.countValueEt.text.toString()
            val reason = binding.messageValueEt.text.toString()
            val isAnon = when (binding.isAnon.isChecked) {
                true -> true
                else -> false
            }
            tagsToIdTags()
            if (userId != -1 && countText.isNotEmpty() && reason.isNotEmpty()) {
                try {
                    val count: Int = Integer.valueOf(countText)
                    UserDataRepository.getInstance()?.token?.let {
                        viewModel.sendCoinsWithImage(
                            it,
                            userId,
                            count,
                            reason,
                            isAnon,
                            imageFilePart,
                            listCheckedIdTags
                        )
                        binding.sendCoinBtn.isClickable = false
                        binding.sendCoinBtn.isEnabled = false
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    //Toast.makeText(requireContext(), viewModel.sendCoinsError.toString(), Toast.LENGTH_LONG).show()
                }
            } else {
                val snack = Snackbar.make(
                    requireView(),
                    requireContext().resources.getString(R.string.unsuccessfulSendCoins),
                    Snackbar.LENGTH_LONG
                )
                snack.setTextMaxLines(3)
                    .setTextColor(context?.getColor(R.color.white)!!)
                    .setAction(context?.getString(R.string.OK)!!) {
                        snack.dismiss()
                    }
                snack.show()
            }
        }
    }


    private fun showResultTransaction(
        amountThanks: Int, receiverTg: String, receiverName: String,
        receiverSurname: String, photo: String
    ) {
        binding.textField.editText?.setText("")
        binding.countValueEt.setText(R.string.empty)
        binding.messageValueEt.setText(R.string.empty)
        binding.sendCoinLinear.visibility = View.GONE
        val bundle = Bundle()
        bundle.putInt(Consts.AMOUNT_THANKS, amountThanks)
        bundle.putString(Consts.RECEIVER_TG, receiverTg.trim())
        bundle.putString(Consts.RECEIVER_NAME, receiverName.trim())
        bundle.putString(Consts.RECEIVER_SURNAME, receiverSurname.trim())
        bundle.putString(Consts.AVATAR_USER, photo)
        findNavController().navigate(
            R.id.action_transactionFragment_to_transactionResultFragment,
            bundle,
            OptionsTransaction().optionForResult
        )

    }

    companion object {
        private const val CODE_IMG_GALLERY = 111
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }
}
