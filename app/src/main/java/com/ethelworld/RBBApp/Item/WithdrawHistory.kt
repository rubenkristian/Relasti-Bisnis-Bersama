package com.ethelworld.RBBApp.Item

data class WithdrawHistory(
    val id: Int,
    val date: String,
    val cash: String,
    val verified: Boolean,
    val success: Boolean)