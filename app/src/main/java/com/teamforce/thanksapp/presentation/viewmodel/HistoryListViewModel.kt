package com.teamforce.thanksapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.teamforce.thanksapp.data.response.HistoryItem
import com.teamforce.thanksapp.domain.repositories.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HistoryListViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val userDataRepository: com.teamforce.thanksapp.utils.UserDataRepository
) : ViewModel() {

    fun getHistory(
        sentOnly: Int,
        receivedOnly: Int
    ): Flow<PagingData<HistoryItem>> {
        return historyRepository.getHistory(
            receivedOnly = if (receivedOnly == -1) null else receivedOnly,
            sentOnly = if (sentOnly == -1) null else sentOnly
        ).map {
            it.map {
                it
            }
        }
            .map {
                it.insertSeparators<HistoryItem.UserTransactionsResponse, HistoryItem> { before, after ->
                    if (after == null) {
                        return@insertSeparators null
                    }

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
    }

    fun getDateTimeLabel(dateTime: LocalDateTime): String {
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