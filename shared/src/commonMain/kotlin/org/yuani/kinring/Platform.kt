package org.yuani.kinring

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform