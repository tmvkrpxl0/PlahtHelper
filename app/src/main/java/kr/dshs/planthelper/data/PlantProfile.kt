package kr.dshs.planthelper.data

import android.net.Uri
import kotlinx.serialization.Serializable
import kr.dshs.planthelper.data.serializers.DateSerializer
import kr.dshs.planthelper.data.serializers.UriSerializer
import java.util.*

@Serializable
data class PlantProfile(
    val academic: PlantAcademic,
    val customName: String? = null,
    @Serializable(with = DateSerializer::class) val plantDate: Date,
    @Serializable(with = UriSerializer::class) val photoUri: Uri
)
