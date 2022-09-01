package com.teamforce.thanksapp.utils

import com.teamforce.thanksapp.presentation.activity.MainActivity

object Consts {
    const val AMOUNT_THANKS = "amount_thanks"
    const val DESCRIPTION_OF_TRANSACTION = "descriptionOfTransaction"
    const val RECEIVER_TG = "receiverOfTransaction"
    const val RECEIVER_NAME = "receiverOfTransaction"
    const val RECEIVER_SURNAME = "receiverOfTransaction"
    const val BUNDLE_TG_OR_EMAIL = "telegramOrEmail"
    const val BUNDLE_EMAIL = "bundleEmail"
    const val LINK_TO_BOT = "https://t.me/DigitalRefBot"
    const val LINK_TO_BOT_Name = "LinkToBot"
    const val SELECT_PICTURE = 200
    // Аргументы в расширенную инфу о траназкции
    const val DATE_TRANSACTION = "date-transaction"
    const val DESCRIPTION_TRANSACTION_1 = "description-transaction"
    const val DESCRIPTION_TRANSACTION_2_WHO = "description-transaction_2_who"
    const val DESCRIPTION_TRANSACTION_3_AMOUNT = "description-transaction_2_who_3_amount"
    const val REASON_TRANSACTION = "reason-transaction"
    const val STATUS_TRANSACTION = "satus-transaction"
    const val LABEL_STATUS_TRANSACTION = "label-status-transaction"
    const val WE_REFUSED_YOUR_OPERATION = "we-refused_your_operation"
    const val AVATAR_USER = "user-avatar"
    const val SHOULD_ME_GOTO_HISTORY = "ShouldMeGoToHistory"

    val MAIN = MainActivity()
    const val BASE_URL: String = "http://176.99.6.251:8888"

    private const val SP_NAME = "com.teamforce.thanksapp"
    private const val SP_ARG_TELEGRAM = "Telegram"
    private const val SP_ARG_TOKEN = "Token"

}