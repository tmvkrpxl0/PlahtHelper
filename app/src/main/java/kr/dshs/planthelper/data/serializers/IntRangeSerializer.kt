package kr.dshs.planthelper.data.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlin.time.Duration

class IntRangeSerializer() : KSerializer<IntRange> {
    override val descriptor = PrimitiveSerialDescriptor("IntRange", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): IntRange {
        val split = decoder.decodeString().split("..")
        val start = split[0].toInt()
        val end = split[1].toInt()
        return start..end
    }

    override fun serialize(encoder: Encoder, value: IntRange) {
        val start = value.first
        val end = value.last
        encoder.encodeString("$start..$end")
    }
}