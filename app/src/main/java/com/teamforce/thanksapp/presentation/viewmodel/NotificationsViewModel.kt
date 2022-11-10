package com.teamforce.thanksapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.teamforce.thanksapp.NotificationsRepository
import com.teamforce.thanksapp.domain.mappers.notifications.NotificationMapper
import com.teamforce.thanksapp.domain.models.notifications.NotificationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    private val mapper: NotificationMapper
) : ViewModel() {

    val notifications = notificationsRepository.getNotifications()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PagingData.empty()
        ).cachedIn(viewModelScope).map {
            it.map {
                mapper.map(it)
            }
        }
        .map {
            it.insertSeparators { before, after ->
                if (after == null) return@insertSeparators null


                if (before == null) {
                    val afterTime: LocalDateTime =
                        LocalDateTime.parse(
                            after.createdAt.replace(
                                "Z",
                                ""
                            )
                        )

                    val label = getDateTimeLabel(afterTime)
                    return@insertSeparators NotificationItem.DateTimeSeparator(label)
                }

                val beforeTime: LocalDateTime =
                    LocalDateTime.parse(
                        before.createdAt.replace(
                            "Z",
                            ""
                        )
                    )

                val afterTime: LocalDateTime =
                    LocalDateTime.parse(
                        after.createdAt.replace(
                            "Z",
                            ""
                        )
                    )

                if (beforeTime.dayOfMonth != afterTime.dayOfMonth) {
                    val title = getDateTimeLabel(afterTime)
                    NotificationItem.DateTimeSeparator(title)
                } else null
            }
        }


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

    private fun getDateTimeLabel(dateTime: LocalDateTime): String {
        val title = dateTime.dayOfMonth.toString() + " " + getMonth(dateTime)
        val result = dateTime.toString().subSequence(0, 10)
        val today: LocalDate = LocalDate.now()
        val yesterday: String =
            today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
        return when (result) {
            today.toString() -> {
                "Сегодня"
            }
            yesterday -> {
                "Вчера"
            }
            else -> {
                title
            }
        }

    }

}