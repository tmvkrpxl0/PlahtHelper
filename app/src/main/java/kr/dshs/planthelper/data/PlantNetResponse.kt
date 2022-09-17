package kr.dshs.planthelper.data

import kotlinx.serialization.Serializable

@Serializable
data class PlantNetResponse(
    val bestMatch: String,
    val language: String,
    val preferedReferential: String,
    val query: Query,
    val remainingIdentificationRequests: Int,
    val results: List<Result>,
    val version: String
)

@Serializable
data class Query(
    val images: List<String>,
    val includeRelatedImages: Boolean,
    val organs: List<String>,
    val project: String
)

@Serializable
data class Result(
    val gbif: Gbif,
    val score: Double,
    val species: Species
)

@Serializable
data class Gbif(
    val id: String
)

@Serializable
data class Species(
    val commonNames: List<String>,
    val family: Family,
    val genus: Genus,
    val scientificName: String,
    val scientificNameAuthorship: String,
    val scientificNameWithoutAuthor: String
)

@Serializable
data class Family(
    val scientificName: String,
    val scientificNameAuthorship: String,
    val scientificNameWithoutAuthor: String
)

@Serializable
data class Genus(
    val scientificName: String,
    val scientificNameAuthorship: String,
    val scientificNameWithoutAuthor: String
)