package ua.polodarb.domain.suggestedFlags

import android.os.Build
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import ua.polodarb.domain.BuildConfig
import ua.polodarb.domain.suggestedFlags.models.SuggestedFlagsModel
import ua.polodarb.repository.appsList.AppsListRepository
import ua.polodarb.repository.suggestedFlags.SuggestedFlagsRepository
import ua.polodarb.repository.suggestedFlags.models.MergedAllTypesOverriddenFlags
import ua.polodarb.repository.suggestedFlags.models.SuggestedFlagsRepoModel

class SuggestedFlagsUseCase(
    val repository: SuggestedFlagsRepository,
    private val appsRepository: AppsListRepository
) {

    suspend operator fun invoke(): List<SuggestedFlagsModel>? {
        return try {
            val rawSuggestedFlag = repository.loadSuggestedFlags() ?: emptyList()
            val overriddenFlags = fetchOverriddenFlags(rawSuggestedFlag.map { it.packageName })
            val data = mutableListOf<SuggestedFlagsModel>()
            val isCurrentAppStable = !BuildConfig.VERSION_NAME.contains("beta", ignoreCase = true)

            rawSuggestedFlag.forEach { flag ->
                val minAndroidSdkCode = flag.minAndroidSdkCode
                val minVersionCode = flag.minAppVersionCode
                val appVersionCode = appsRepository.getAppVersionCode(flag.packageId)
                val isEnabled = flag.isEnabled ?: true
                val versionAndroidCheck =
                    minAndroidSdkCode == null || Build.VERSION.SDK_INT >= minAndroidSdkCode
                val versionAppCheck = minVersionCode == null || minVersionCode <= appVersionCode
                val flagIsBeta = flag.isBeta ?: false
                val isBetaCheck = !(isCurrentAppStable && flagIsBeta)

                if (isBetaCheck && isEnabled && appVersionCode != -1L && versionAndroidCheck && versionAppCheck) {
                    val isFlagEnabled = isFlagOverridden(flag, overriddenFlags)
                    data.add(SuggestedFlagsModel(flag = flag, enabled = isFlagEnabled))
                }
            }

            data.distinct().toImmutableList()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchOverriddenFlags(packages: List<String>): Map<String, MergedAllTypesOverriddenFlags> {
        val overriddenFlagsMap = mutableMapOf<String, MergedAllTypesOverriddenFlags>()

        packages.forEach { pkg ->
            if (overriddenFlagsMap[pkg] == null) {
                overriddenFlagsMap[pkg] = repository.getMergedOverriddenFlagsByPackage(pkg).first()
            }
        }

        return overriddenFlagsMap
    }

    private fun isFlagOverridden(flag: SuggestedFlagsRepoModel, overriddenFlags: Map<String, MergedAllTypesOverriddenFlags>): Boolean {
        return flag.flags.firstOrNull {
            overriddenFlags[flag.packageName]?.boolFlag?.get(it.name) != it.value &&
                    overriddenFlags[flag.packageName]?.intFlag?.get(
                        it.name
                    ) != it.value &&
                    overriddenFlags[flag.packageName]?.floatFlag?.get(
                        it.name
                    ) != it.value &&
                    overriddenFlags[flag.packageName]?.stringFlag?.get(
                        it.name
                    ) != it.value
        } == null
    }

}
