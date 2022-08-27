package kr.dshs.planthelper.data.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration

object DurationRangeSerializer: KSerializer<ClosedRange<Duration>> {
    override val descriptor = PrimitiveSerialDescriptor("DurationRange", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): ClosedRange<Duration> {
        val split = decoder.decodeString().split("..")
        val start = Duration.parse(split[0])
        val end = Duration.parse(split[1])
        return start..end
    }

    override fun serialize(encoder: Encoder, value: ClosedRange<Duration>) {
        val start = value.start
        val end = value.endInclusive
        encoder.encodeString("$start..$end")
    }
}