package ua.polodarb.repository.suggestedFlags.models

import ua.polodarb.network.suggestedFlags.model.SuggestedFlagsNetModel
import ua.polodarb.repository.suggestedFlags.models.FlagInfoRepoModel.Companion.toRepoModel

data class SuggestedFlagsRepoModel(
    val title: String,
    val packageName: String,
    val packageId: String,
    val isBeta: Boolean? = false,
    val isEnabled: Boolean? = true,
    val isPrimary: Boolean? = false,
    val group: String? = null,
    val minAppVersionCode: Int? = null,
    val minAndroidSdkCode: Int? = null,
    val source: String? = null,
    val note: String? = null,
    val warning: String? = null,
    val url: String? = null,
    val tag: String? = null,
    val flags: List<FlagInfoRepoModel>
) {
    companion object {
        fun SuggestedFlagsNetModel.toRepoModel() =
            SuggestedFlagsRepoModel(
                title = title,
                packageName = packageName,
                isBeta = isBeta,
                isEnabled = isEnabled,
                isPrimary = isPrimary,
                group = group,
                packageId = packageId,
                minAppVersionCode = minAppVersionCode,
                minAndroidSdkCode = minAndroidSdkCode,
                source = source,
                note = note,
                warning = warning,
                url = url,
                tag = tag,
                flags = flags.map { it.toRepoModel() }
            )
    }
}