package com.ethelworld.RBBApp.Item

data class Contact(
    val isSave: Boolean = false,
    var id: Long,
    val idUser: Long,
    val image: String = "",
    val name: String?,
    val address: String,
    val occupation: String?,
    val company: String?,
    val province: String?,
    val city: String?,
    val phoneNumber: String?,
    val facebook: String = "",
    val instagram: String = "",
    val olshop: String = "",
    val tiktok: String = "",
    val youtube: String = "")