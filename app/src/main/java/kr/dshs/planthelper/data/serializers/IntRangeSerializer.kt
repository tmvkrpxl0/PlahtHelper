package kr.dshs.planthelper.data.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

class IntRangeSerializer() : KSerializer<IntRange> {
    override val descriptor = buildClassSerialDescriptor("IntRange") {
        element<Int>("start")
        element<Int>("endInclusive")
    }

    override fun deserialize(decoder: Decoder): IntRange = decoder.decodeStructure(descriptor) {
        var start: Int? = null
        var endInclusive: Int? = null


        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> start = decodeIntElement(descriptor, index)
                1 -> endInclusive = decodeIntElement(descriptor, index)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unknown range index: $index")
            }
        }

        require(start != null) { "Invalid start of a range!" }
        require(endInclusive != null) { "Invalid end of a range!" }
        return@decodeStructure IntRange(start, endInclusive)
    }

    override fun serialize(encoder: Encoder, value: IntRange) = encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.first)
            encodeIntElement(descriptor, 1, value.last)
        }
}