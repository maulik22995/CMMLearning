package network

import kotlinx.serialization.Serializable

@Serializable
data class CensoredText(
    val result: String
)