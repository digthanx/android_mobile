package com.teamforce.thanksapp.presentation.fragment.newTransactionScreen

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.UserBean
import com.teamforce.thanksapp.databinding.FragmentTransactionBinding
import com.teamforce.thanksapp.model.domain.TagModel
import com.teamforce.thanksapp.presentation.adapter.UsersAdapter
import com.teamforce.thanksapp.presentation.adapter.ValuesAdapter
import com.teamforce.thanksapp.presentation.fragment.profileScreen.ProfileFragment
import com.teamforce.thanksapp.presentation.viewmodel.TransactionViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository
import com.teamforce.thanksapp.utils.createBitmapFromResult
import com.teamforce.thanksapp.utils.getPath
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File


class TransactionFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

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

    private val usersInput: TextInputEditText by lazy { binding.usersEt }
    private val usersInputLayout: TextInputLayout by lazy { binding.textField }
    private val countEditText: EditText by lazy { binding.countValueEt }
    private val reasonEditText: EditText by lazy { binding.messageValueEt }
    private val recyclerView: RecyclerView by lazy { binding.usersListRv }
    private val sendCoinsGroup: LinearLayout by lazy { binding.sendCoinLinear }
    private val availableCoins: TextView by lazy { binding.distributedValueTv }
    private val chipGroup: ChipGroup by lazy { binding.chipGroup }
    private val checkBoxIsAnon: SwitchMaterial by lazy { binding.isAnon }
    private val progressBar: ProgressBar by lazy { binding.progressBar }
    private val sendButton: Button by lazy { binding.sendCoinBtn }
    private val checkBoxAddValues: SwitchMaterial by lazy { binding.addValues }
    private val textInputLayoutAddValues: TextInputLayout by lazy { binding.textInputLayoutValue }
    private val etAddValues: TextInputEditText by lazy { binding.etValue }
    private var listValues: List<TagModel> = listOf()
    private var listCheckedValues: MutableList<TagModel> = mutableListOf()

    private val imgCard: MaterialCardView by lazy { binding.showAttachedImgCard }
    private val detachImgBtn: ImageButton by lazy { binding.detachImgBtn }
    private val image: ImageView by lazy { binding.image }
    private val attachImageBtn: MaterialButton by lazy { binding.attachImageBtn }
    private var imageFilePart: MultipartBody.Part? = null
    private var user: UserBean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.balanceFragment, R.id.feedFragment, R.id.transactionFragment, R.id.historyFragment))
        binding.toolbarTransaction.setupWithNavController(navController, appBarConfiguration)
        shouldMeGoToHistoryFragment()
        viewModel = TransactionViewModel()
        viewModel.initViewModel()
        initViews(view)
        dropDownMenuUserInput(usersInput)
        loadValuesFromDB()
        setValuesFromDb()
        appealToDB()
        checkedChip()
        openValuesEt()
        val token = UserDataRepository.getInstance()?.token
        if (token != null) {
            viewModel.loadUserBalance(token)
        }
        viewModel.balance.observe(viewLifecycleOwner){
            UserDataRepository.getInstance()?.leastCoins = it.distribute.amount
            availableCoins.text = it.distribute.amount.toString()
        }

        attachImageBtn.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            addPhotoFromIntent()
        }

        detachImgBtn.setOnClickListener {
            imgCard.visibility = View.GONE
            imageFilePart = null
        }

        etAddValues.setOnClickListener {
            createDialog(listValues)
           // findNavController().navigate(R.id.action_transactionFragment_to_listOfValuesFragment4)

        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

    }


    }

    private fun loadValuesFromDB(){
        UserDataRepository.getInstance()?.token?.let { token ->
            viewModel.loadTags(token)
        }
    }
    private fun setValuesFromDb(){
        viewModel.tags.observe(viewLifecycleOwner) {
            listValues = it
        }
    }
    private fun createDialog(list: List<TagModel>){
        val dialog = AlertDialog.Builder(context)
        val inflater = requireActivity().layoutInflater
        val newListValues = inflater.inflate(R.layout.fragment_list_of_values, null)
        val recyclerViewDialog = newListValues.findViewById<RecyclerView>(R.id.values_rv)
//        val list1: List<TagModel> = listOf(TagModel(0, "Name0"),
//            TagModel(1, "Name2"))
        val adapter = ValuesAdapter(list, requireContext())
        recyclerViewDialog.adapter = adapter
        dialog.setView(newListValues)
            .setPositiveButton(getString(R.string.applyValues), DialogInterface.OnClickListener { dialog, which ->
                listCheckedValues = adapter.listCheckedValues
                Log.d("Token", " Список выбранных ценностей ${listCheckedValues}")
                dialog.cancel()
            })
        dialog.show()
    }

    private fun openValuesEt(){
        checkBoxAddValues.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                textInputLayoutAddValues.visibility = View.VISIBLE
            }else{
                textInputLayoutAddValues.visibility = View.GONE
            }
        }
    }


    private fun initViews(view: View) {
        sendButton.setOnClickListener(this)
        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    recyclerView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
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
                if (imageUri != null && path != null){
                    imgCard.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(imageUri)
                        .centerCrop()
                        .into(image)
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

    private fun shouldMeGoToHistoryFragment(){
        val bool = arguments?.getBoolean(Consts.SHOULD_ME_GOTO_HISTORY, false)
        bool?.let {
            if(it){
                findNavController().navigate(R.id.action_transactionFragment_to_historyFragment)
            }
        }
    }

    fun checkedChip(){
        Log.d("Token", "i'm inner chip function")
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.chipOne -> {
                    Log.d("Token", "i'm inner chip one")
                    countEditText.setText("1")
                }
                R.id.chipFive -> {
                    binding.chipFive.apply {
                        countEditText.setText("5")
                    }
                }
                R.id.chipTen -> {
                    binding.chipTen.apply {
                        countEditText.setText("10")
                    }
                }
                R.id.chipTwentyFive -> {
                    binding.chipTwentyFive.apply {
                        countEditText.setText("25")
                    }
                }
            }
        }



    }

    fun dropDownMenuUserInput(userInput: TextInputEditText){
        userInput.addTextChangedListener(object : TextWatcher {
            // TODO Возможно стоит будет оптимизировать вызов списка пользователей
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.trim().length > 0 && count > before && s.toString() != user?.tgName) {
                    UserDataRepository.getInstance()?.token?.let {
                        viewModel.loadUsersList(s.toString(), it)
                    }
                }else if(usersInput.text?.trim().toString().isEmpty()){
                    UserDataRepository.getInstance()?.token?.let {
                        viewModel.loadUsersListWithoutInput("true", it)
                    }
                } else {
                    recyclerView.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        if(usersInput.text?.trim().toString().isEmpty()){
            UserDataRepository.getInstance()?.token?.let {
                viewModel.loadUsersListWithoutInput("true", it)
            }
        }
    }

    fun appealToDB(){
        viewModel.users.observe(viewLifecycleOwner) {
            recyclerView.visibility = View.VISIBLE
            recyclerView.adapter = UsersAdapter(it, this)
        }
        viewModel.isSuccessOperation.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "Success!", Toast.LENGTH_LONG).show()
                showResultTransaction(
                    amountThanks = Integer.valueOf(countEditText.text.toString()),
                    receiverTg = user?.tgName.toString(),
                    receiverName = user?.firstname.toString(),
                    receiverSurname = user?.surname.toString(),
                    photo = "${Consts.BASE_URL}/media/${user?.photo}"
                )
            }
        }
        viewModel.sendCoinsError.observe(viewLifecycleOwner) {
            sendCoinsGroup.visibility = View.GONE
            usersInputLayout.visibility = View.VISIBLE
            //Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            val snack = Snackbar.make(requireView(), requireContext().resources.getString(R.string.unsuccessfulSendCoins), Snackbar.LENGTH_LONG)
                snack.setTextMaxLines(3)
                .setTextColor(context?.getColor(R.color.white)!!)
                .setAction(context?.getString(R.string.OK)!!){
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
            recyclerView.visibility = View.GONE
            user = v.tag as UserBean
            usersInputLayout.visibility = View.GONE
            with(binding){
                if(!user?.photo.isNullOrEmpty()){
                    Glide.with(v)
                        .load("${Consts.BASE_URL}/media/${user?.photo}".toUri())
                        .centerCrop()
                        .into(receiverAvatar)
                }
                receiverTgName.text = user?.tgName
                receiverNameLabelTv.text = user?.firstname
                receiverSurnameLabelTv.text = user?.surname
            }
            sendCoinsGroup.visibility = View.VISIBLE
        } else if (v?.id == R.id.send_coin_btn) {
            val userId = user?.userId ?: -1
            val countText = countEditText.text.toString()
            val reason = reasonEditText.text.toString()
            val isAnon = when(checkBoxIsAnon.isChecked){
                true -> true
                else -> false
            }
                if (userId != -1 && countText.isNotEmpty() && reason.isNotEmpty()) {
                    try {
                        val count: Int = Integer.valueOf(countText)
                        UserDataRepository.getInstance()?.token?.let {
                            if (imageFilePart == null){
                                viewModel.sendCoins(it, userId, count, reason, isAnon)
                            }else{
                                viewModel.sendCoinsWithImage(it, userId, count, reason, isAnon, imageFilePart!!)
                            }
                            binding.sendCoinBtn.isClickable = false
                            binding.sendCoinBtn.isEnabled = false
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                        //Toast.makeText(requireContext(), viewModel.sendCoinsError.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


    private fun showResultTransaction(amountThanks: Int, receiverTg: String, receiverName: String,
                                      receiverSurname: String, photo: String){
        usersInputLayout.editText?.setText("")
        countEditText.setText(R.string.empty)
        reasonEditText.setText(R.string.empty)
        sendCoinsGroup.visibility = View.GONE
        val bundle = Bundle()
        bundle.putInt(Consts.AMOUNT_THANKS, amountThanks)
        bundle.putString(Consts.RECEIVER_TG, receiverTg.trim())
        bundle.putString(Consts.RECEIVER_NAME, receiverName.trim())
        bundle.putString(Consts.RECEIVER_SURNAME, receiverSurname.trim())
        bundle.putString(Consts.AVATAR_USER, photo)
        val optionForResult = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.bottom_out)
            .setPopEnterAnim(com.google.android.material.R.anim.abc_fade_in)
            .setPopExitAnim(com.google.android.material.R.anim.abc_fade_out)
            .build()
        findNavController().navigate(
            R.id.action_transactionFragment_to_transactionResultFragment,
            bundle,
            optionForResult
        )

    }

    companion object{
        private const val CODE_IMG_GALLERY = 111
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }
}
