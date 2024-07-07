package com.example.efslha

data class Offer(
    var sellerName: String,
    var sellerPhone: String,
    var sellerId: String,
    var canCount: Int = 0,
    var plasticCount: Int = 0,
    var glassCount: Int = 0,
    var paperCount: Int = 0
)
