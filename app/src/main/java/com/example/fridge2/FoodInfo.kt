package com.example.fridge2

data class FoodInfo(
    var name: String? = null,
    var date: String? = null,
    var date_long: Long? = null,
    var loc: Int? = null,
    var check: Boolean? = null,
)