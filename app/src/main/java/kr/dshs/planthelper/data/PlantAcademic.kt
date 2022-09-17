package kr.dshs.planthelper.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.dshs.planthelper.data.serializers.DurationRangeSerializer
import kr.dshs.planthelper.data.serializers.IntRangeSerializer
import kotlin.time.Duration

@Serializable
data class PlantAcademic(
    val name: String,
    @SerialName("scientific_name") val scientificName: String? = null, // TODO null 금지하기
    @SerialName("accepted_temperature") @Serializable(with = IntRangeSerializer::class) val acceptableTemperature: IntRange,
    @Serializable(with = DurationRangeSerializer::class) @SerialName("watering_period") val wateringPeriod: ClosedRange<@Contextual Duration>? = null,
    @SerialName("max_height_meter") val maxHeight: Double? = null,
    @SerialName("needs_sunlight") val needsSunlight: SunlightDemands,
    @SerialName("grow_environment") val growEnvironment: GrowEnvironment,
    @SerialName("replace_soil_year") @Serializable(with = IntRangeSerializer::class) val replaceSoilYear: IntRange? = null,
    @SerialName("description") val etcDescription: String
)

@Serializable
enum class SunlightDemands {
    @SerialName("harmful") HARMFUL,
    @SerialName("optional") OPTIONAL,
    @SerialName("required") REQUIRED
}

@Serializable
enum class GrowEnvironment {
    @SerialName("in") IN,
    @SerialName("out") OUT,
    @SerialName("inout") INOUT
}
