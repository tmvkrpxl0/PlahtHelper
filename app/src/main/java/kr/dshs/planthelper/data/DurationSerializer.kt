package kr.dshs.planthelper.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration

class DurationSerializer: KSerializer<Duration> {
    override val descriptor = PrimitiveSerialDescriptor("Duration", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Duration {
        val string = decoder.decodeString()
        return Duration.parse(string)
    }

    override fun serialize(encoder: Encoder, value: Duration) {
        val string = value.toString()
        encoder.encodeString(string)
    }
}