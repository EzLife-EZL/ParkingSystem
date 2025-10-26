package com.parkingSystem.parkingSystem.requestmodel

import android.net.Uri

data class UpdateUserInput(
    val name: String? = null,
    val phone: String? = null,
    val role: String? = null,
    val email: String? = null,
    val address: String? = null,
    val password: String? = null,
)