package ua.polodarb.network.suggestedFlags.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuggestedFlagsNetModel(
    @SerialName("title") val title: String,
    @SerialName("packageName") val packageName: String,
    @SerialName("packageId") val packageId: String,
    @SerialName("isBeta") val isBeta: Boolean? = false,
    @SerialName("isEnabled") val isEnabled: Boolean? = true,
    @SerialName("isPrimary") val isPrimary: Boolean? = false,
    @SerialName("group") val group: String? = null,
    @SerialName("minAppVersionCode") val minAppVersionCode: Int? = null,
    @SerialName("minAndroidSdkCode") val minAndroidSdkCode: Int? = null,
    @SerialName("source") val source: String? = null,
    @SerialName("note") val note: String? = null,
    @SerialName("warning") val warning: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("tag") val tag: String? = null,
    @SerialName("flags") val flags: List<FlagInfoNetModel>
)