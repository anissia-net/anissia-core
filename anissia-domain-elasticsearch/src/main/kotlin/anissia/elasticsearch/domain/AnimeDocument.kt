package anissia.elasticsearch.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field


@Document(indexName = "anissia_anime")
data class AnimeDocument (
        @Id
        @Field(store = true)
        var animeNo: Long = 0,

        @Field(index = true, analyzer = "standard", searchAnalyzer = "standard")
        var subject: String = "",

        @Field(index = true)
        var genres: List<String> = listOf(),

        @Field(index = true)
        var end: Boolean = true,

        @Field(index = true)
        var translators: List<String> = listOf()
)
