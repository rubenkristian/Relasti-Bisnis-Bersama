package com.ethelworld.RBBApp.Item

data class InfoWithdraw(
    val username: String,
    val bonusTotal: Long,
    val bank: String,
    val minimum: Long,
    val bankNoAccount: String,
    val bankNameAccount: String,
    val helpTextWithdraw: String)