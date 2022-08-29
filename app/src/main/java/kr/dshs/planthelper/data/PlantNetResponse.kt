package kr.dshs.planthelper.data

import kotlinx.serialization.Serializable

@Serializable
data class PlantNetResponse(
    val results: Array<Result>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlantNetResponse

        if (!results.contentEquals(other.results)) return false

        return true
    }
    override fun hashCode(): Int {
        return results.contentHashCode()
    }

}

@Serializable
data class Result(
    val score: Double,
    val species: Array<Specie>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Result

        if (score != other.score) return false
        if (!species.contentEquals(other.species)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = score.hashCode()
        result = 31 * result + species.contentHashCode()
        return result
    }
}

@Serializable
data class Specie(
    val scientificNameWithoutAuthor: String,
    val commonNames: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Specie

        if (scientificNameWithoutAuthor != other.scientificNameWithoutAuthor) return false
        if (!commonNames.contentEquals(other.commonNames)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = scientificNameWithoutAuthor.hashCode()
        result = 31 * result + commonNames.contentHashCode()
        return result
    }
}