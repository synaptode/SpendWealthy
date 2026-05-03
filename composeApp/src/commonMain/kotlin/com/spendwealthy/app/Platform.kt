package com.spendwealthy.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform