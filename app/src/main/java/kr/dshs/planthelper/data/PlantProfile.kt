package kr.dshs.planthelper.data

import android.util.Log
import kotlinx.serialization.Serializable
import kr.dshs.planthelper.data.serializers.DateSerializer
import kr.dshs.planthelper.data.serializers.FileSerializer
import java.io.File
import java.util.*
import java.util.logging.Logger

@Serializable
data class PlantProfile(
    val academic: PlantAcademic,
    val customName: String? = null,
    val wateringPeriod: Int,
    @Serializable(with = DateSerializer::class) val plantDate: Date? = null,
    @Serializable(with = FileSerializer::class) val photoFile: File? = null,
    val id: Int
) {
    init {
        Log.d("profile", "Plant profile created!")
        Log.d("stack", Exception().stackTraceToString())
    }
    fun getAnyName(): String {
        return customName?: academic.name
    }
}
