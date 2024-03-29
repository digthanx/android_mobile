package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.UserListWithoutInputRequest
import com.teamforce.thanksapp.data.request.UsersListRequest
import com.teamforce.thanksapp.data.response.BalanceResponse
import com.teamforce.thanksapp.data.response.GetTagsResponse
import com.teamforce.thanksapp.data.response.SendCoinsResponse
import com.teamforce.thanksapp.data.response.UserBean
import com.teamforce.thanksapp.model.domain.TagModel
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val thanksApi: ThanksApi,
    val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _users = MutableLiveData<List<UserBean>>()
    val users: LiveData<List<UserBean>> = _users
    private val _usersLoadingError = MutableLiveData<String>()
    val usersLoadingError: LiveData<String> = _usersLoadingError
    private val _isSuccessOperation = MutableLiveData<Boolean>()
    val isSuccessOperation: LiveData<Boolean> = _isSuccessOperation
    private val _sendCoinsError = MutableLiveData<String>()
    val sendCoinsError: LiveData<String> = _sendCoinsError
    private val _balance = MutableLiveData<BalanceResponse>()
    val balance: LiveData<BalanceResponse> = _balance
    private val _balanceError = MutableLiveData<String>()
    val balanceError: LiveData<String> = _balanceError

    private val _tags = MutableLiveData<List<TagModel>>()
    val tags: LiveData<List<TagModel>> = _tags
    private val _tagsError = MutableLiveData<String>()
    val tagsError: LiveData<String> = _tagsError


    fun loadTags() {
        _isLoading.postValue(true)
        viewModelScope.launch { callTagsEndpoint(Dispatchers.Default) }
    }

    private suspend fun callTagsEndpoint(
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi.getTags().enqueue(object : Callback<GetTagsResponse> {
                override fun onResponse(
                    call: Call<GetTagsResponse>,
                    response: Response<GetTagsResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _tags.postValue(response.body()?.tags)
                    } else {
                        _tagsError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<GetTagsResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _tagsError.postValue(t.message)
                }
            })
        }
    }


    fun loadUserBalance() {
        _isLoading.postValue(true)
        viewModelScope.launch { callBalanceEndpoint(Dispatchers.Default) }
    }

    private suspend fun callBalanceEndpoint(
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi.getBalance().enqueue(object : Callback<BalanceResponse> {
                override fun onResponse(
                    call: Call<BalanceResponse>,
                    response: Response<BalanceResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _balance.postValue(response.body())
                    } else {
                        _balanceError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _balanceError.postValue(t.message)
                }
            })
        }
    }


    fun loadUsersList(username: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callUsersListEndpoint(username, Dispatchers.Default) }
    }

    private suspend fun callUsersListEndpoint(
        username: String,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi.getUsersList(UsersListRequest(username))
                .enqueue(object : Callback<List<UserBean>> {
                    override fun onResponse(
                        call: Call<List<UserBean>>,
                        response: Response<List<UserBean>>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            Log.d("Token", "Кому спасибки ${response.body()}")
                            _users.postValue(response.body())
                        } else {
                            _usersLoadingError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<List<UserBean>>, t: Throwable) {
                        _isLoading.postValue(false)
                        _usersLoadingError.postValue(t.message)
                    }
                })
        }
    }

    fun sendCoinsWithImage(
        recipient: Int, amount: Int,
        reason: String,
        isAnon: Boolean,
        imageFilePart: MultipartBody.Part?,
        listOfTagsCheckedValues: MutableList<Int>?
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            callSendCoinsWithImageEndpoint(
                recipient, amount,
                reason, isAnon,
                imageFilePart,
                listOfTagsCheckedValues,
                Dispatchers.Default
            )
        }
    }


    private suspend fun callSendCoinsWithImageEndpoint(
        recipient: Int,
        amount: Int,
        reason: String,
        isAnon: Boolean,
        imageFilePart: MultipartBody.Part?,
        listOfTagsCheckedValues: MutableList<Int>?,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            val recipientB =
                RequestBody.create(MediaType.parse("multipart/form-data"), recipient.toString())
            val amountB =
                RequestBody.create(MediaType.parse("multipart/form-data"), amount.toString())
            val reasonB =
                RequestBody.create(MediaType.parse("multipart/form-data"), reason.toString())
            val isAnonB =
                RequestBody.create(MediaType.parse("multipart/form-data"), isAnon.toString())
            val string = listOfTagsCheckedValues.toString()
                .filter { it.isDigit() }
                .replace("", " ")
                .removeSurrounding(" ")
            val tags = RequestBody.create(MediaType.parse("multipart/form-data"), string)


            thanksApi?.sendCoinsWithImage(
                imageFilePart,
                recipientB,
                amountB,
                reasonB,
                isAnonB,
                tags
            )?.enqueue(object : Callback<SendCoinsResponse> {
                    override fun onResponse(
                        call: Call<SendCoinsResponse>,
                        response: Response<SendCoinsResponse>
                    ) {
                        _isSuccessOperation.postValue(false)
                        _isLoading.postValue(false)
                        if (response.code() == 201) {
                            Log.d("Token", "Успешный перевод средств")
                            _isSuccessOperation.postValue(true)
                        } else if (response.code() == 400) {
                            val jArrayError = JSONArray(response.errorBody()!!.string())
                            // _sendCoinsError.postValue(response.message() + " " + response.code())
                            _sendCoinsError.postValue(
                                jArrayError.toString()
                                    .subSequence(2, jArrayError.toString().length - 2).toString()
                            )
                        } else {
                            _sendCoinsError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<SendCoinsResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _sendCoinsError.postValue(t.message)
                    }
                })
            _isSuccessOperation.postValue(false)
        }
    }

    fun loadUsersListWithoutInput(get_users: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            callUsersListWithoutInputEndpoint(
                get_users,
                Dispatchers.Default
            )
        }
    }

    private suspend fun callUsersListWithoutInputEndpoint(
        get_users: String,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi.getUsersWithoutInput(
                get_users = UserListWithoutInputRequest(get_users)
            )
                .enqueue(object : Callback<List<UserBean>> {
                    override fun onResponse(
                        call: Call<List<UserBean>>,
                        response: Response<List<UserBean>>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            Log.d("Token", "Кому спасибки ${response.body()}")
                            _users.postValue(response.body())
                        } else {
                            _usersLoadingError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<List<UserBean>>, t: Throwable) {
                        _isLoading.postValue(false)
                        _usersLoadingError.postValue(t.message)
                    }
                })
        }
    }
}
