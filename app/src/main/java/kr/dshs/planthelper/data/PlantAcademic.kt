package kr.dshs.planthelper.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class PlantAcademic(
    val name: String,
    @Serializable(with = IntRangeSerializer::class) val temperatureRange: IntRange,
    val waterRange: ClosedRange<@Serializable(with = DurationSerializer::class) Duration>,
    val maxHeight: Int = -1, // 만약 -1 이 입력되면 최대 키가 지정되어 있지 않음
    val needsSunlight: SunlightDemands,
    @Serializable(with = IntRangeSerializer::class) val replaceSoilInterval: IntRange,
    val etcDescription: String
)

@Serializable
enum class SunlightDemands {
    @SerialName("harmful") HARMFUL,
    @SerialName("optional") OPTIONAL,
    @SerialName("required") REQUIRED
}
