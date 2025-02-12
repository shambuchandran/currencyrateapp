package org.example.currencyrateapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform