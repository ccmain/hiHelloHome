package com.example.hiHelloHome.common.constants

import org.springframework.core.env.Environment

object Env {
    const val LOCAL: String = "local"
    const val AWS: String = "aws"

    fun getEnv(env: Environment): String =
            when {
                env.activeProfiles.contains(LOCAL) -> LOCAL
                else                               -> AWS
            }
}