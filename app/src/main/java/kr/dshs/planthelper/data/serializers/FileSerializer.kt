package kr.dshs.planthelper.data.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File

class FileSerializer: KSerializer<File> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("File", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): File = File(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: File) = encoder.encodeString(value.toString())
}