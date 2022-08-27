package kr.dshs.planthelper.data

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PlantProfile(
    val academic: PlantAcademic,
    val customName: String? = null,
    @Serializable(with = DateSerializer::class) val plantDate: Date
)
