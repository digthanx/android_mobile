package com.teamforce.thanksapp.presentation.fragment.challenges

enum class ChallengesStatus(val value: String) {
    YOU_ARE_CREATER("Вы создатель челленджа"),
    YOU_CAN_SENT_REPORT("Можно отправить отчёт"),
    REPORT_SENDED("Отчёт отправлен"),
    REPORT_APPLIED("Отчёт подтверждён"),
    REPORT_REFUSED("Отчёт отклонён"),
    YOU_GOT_REWARD("Получено вознаграждение")
}