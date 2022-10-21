package com.teamforce.thanksapp.presentation.viewmodel.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.teamforce.thanksapp.data.request.CancelTransactionRequest
import com.teamforce.thanksapp.data.response.HistoryItem
import com.teamforce.thanksapp.domain.repositories.HistoryRepository
import com.teamforce.thanksapp.presentation.fragment.historyScreen.HistoryFragment
import com.teamforce.thanksapp.utils.Result
import com.teamforce.thanksapp.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HistoryListViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val userDataRepository: com.teamforce.thanksapp.utils.UserDataRepository
) : ViewModel() {

    private val _cancellationResult: MutableLiveData<Result<Int>> =
        MutableLiveData<Result<Int>>()
    val cancellationResult: LiveData<Result<Int>> = _cancellationResult

    fun cancelUserTransaction(id: Int) {
        viewModelScope.launch {

            when (historyRepository.cancelTransaction(
                id.toString(),
                CancelTransactionRequest("D")
            )) {
                is ResultWrapper.GenericError -> {}
                ResultWrapper.NetworkError -> {}
                is ResultWrapper.Success -> _cancellationResult.postValue(Result.Success(0))
            }
        }
    }


    //todo исправить. вот так делать три поля с потоками не надо. Надо передать параметр. Исправить
    val allHistory = historyRepository.getHistory(
        receivedOnly = null,
        sentOnly = null
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PagingData.empty()
    ).cachedIn(viewModelScope).map {
        it.map {
            it
        }
    }
        .map {
            it.insertSeparators<HistoryItem.UserTransactionsResponse, HistoryItem> { before, after ->
                if (after == null) return@insertSeparators null


                if (before == null) {
                    val afterTime: LocalDateTime =
                        LocalDateTime.parse(
                            after.updatedAt.replace(
                                "+03:00",
                                ""
                            )
                        )

                    val label = getDateTimeLabel(afterTime)
                    return@insertSeparators HistoryItem.DateTimeSeparator(label)
                }

                val beforeTime: LocalDateTime =
                    LocalDateTime.parse(
                        before.updatedAt.replace(
                            "+03:00",
                            ""
                        )
                    )

                val afterTime: LocalDateTime =
                    LocalDateTime.parse(
                        after.updatedAt.replace(
                            "+03:00",
                            ""
                        )
                    )

                if (beforeTime.dayOfMonth != afterTime.dayOfMonth) {
                    val title = getDateTimeLabel(afterTime)
                    HistoryItem.DateTimeSeparator(title)
                } else null
            }
        }

    val received = historyRepository.getHistory(
        receivedOnly = 1,
        sentOnly = null
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PagingData.empty()
    ).cachedIn(viewModelScope).map {
        it.map {
            it
        }
    }
        .map {
            it.insertSeparators<HistoryItem.UserTransactionsResponse, HistoryItem> { before, after ->
                if (after == null) return@insertSeparators null


                if (before == null) {
                    val afterTime: LocalDateTime =
                        LocalDateTime.parse(
                            after.updatedAt.replace(
                                "+03:00",
                                ""
                            )
                        )

                    val label = getDateTimeLabel(afterTime)
                    return@insertSeparators HistoryItem.DateTimeSeparator(label)
                }

                val beforeTime: LocalDateTime =
                    LocalDateTime.parse(
                        before.updatedAt.replace(
                            "+03:00",
                            ""
                        )
                    )

                val afterTime: LocalDateTime =
                    LocalDateTime.parse(
                        after.updatedAt.replace(
                            "+03:00",
                            ""
                        )
                    )

                if (beforeTime.dayOfMonth != afterTime.dayOfMonth) {
                    val title = getDateTimeLabel(afterTime)
                    HistoryItem.DateTimeSeparator(title)
                } else null
            }
        }

    val sent = historyRepository.getHistory(
        receivedOnly = null,
        sentOnly = 1
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PagingData.empty()
    ).cachedIn(viewModelScope).map {
        it.map {
            it
        }
    }
        .map {
            it.insertSeparators<HistoryItem.UserTransactionsResponse, HistoryItem> { before, after ->
                if (after == null) return@insertSeparators null


                if (before == null) {
                    val afterTime: LocalDateTime =
                        LocalDateTime.parse(
                            after.updatedAt.replace(
                                "+03:00",
                                ""
                            )
                        )

                    val label = getDateTimeLabel(afterTime)
                    return@insertSeparators HistoryItem.DateTimeSeparator(label)
                }

                val beforeTime: LocalDateTime =
                    LocalDateTime.parse(
                        before.updatedAt.replace(
                            "+03:00",
                            ""
                        )
                    )

                val afterTime: LocalDateTime =
                    LocalDateTime.parse(
                        after.updatedAt.replace(
                            "+03:00",
                            ""
                        )
                    )

                if (beforeTime.dayOfMonth != afterTime.dayOfMonth) {
                    val title = getDateTimeLabel(afterTime)
                    HistoryItem.DateTimeSeparator(title)
                } else null
            }
        }

    private fun getDateTimeLabel(dateTime: LocalDateTime): String {
        val title = dateTime.dayOfMonth.toString() + " " + getMonth(dateTime)
        val result = dateTime.toString().subSequence(0, 10)
        val today: LocalDate = LocalDate.now()
        val yesterday: String =
            today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
        if (result == today.toString()) {
            return "Сегодня"
        } else if (result == yesterday) {
            return "Вчера"
        } else {
            return title
        }

    }

    //
    private fun getMonth(dateTime: LocalDateTime): String {
        when (dateTime.month.value) {
            1 -> {
                return "Января"
            }
            2 -> {
                return "Февраля"
            }
            3 -> {
                return "Марта"
            }
            4 -> {
                return "Апреля"
            }
            5 -> {
                return "Мая"
            }
            6 -> {
                return "Июня"
            }
            7 -> {
                return "Июля"
            }
            8 -> {
                return "Августа"
            }
            9 -> {
                return "Сентября"
            }
            10 -> {
                return "Октября"
            }
            11 -> {
                return "Ноября"
            }
            12 -> {
                return "Декабря"
            }
            else -> return "13й месяц"
        }
    }


    fun getUsername() = userDataRepository.getUserName()

}