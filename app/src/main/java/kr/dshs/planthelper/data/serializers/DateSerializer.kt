package kr.dshs.planthelper.data.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

class DateSerializer: KSerializer<Date> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Duration", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Date {
        val string = decoder.decodeString()
        val split = string.split("-")
        require(split.size == 3) { "데이터 형식이 YYYY-MM-DD 형식이 아닙니다!" }
        val year = split[0].toInt()
        val month = split[1].toInt()
        val date = split[2].toInt()
        return Date(year, month, date)
    }

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString("${value.year}-${value.month}-${value.date}")
    }
}