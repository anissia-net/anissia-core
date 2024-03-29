package anissia.domain.anime.core

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field


@Document(indexName = "anissia_anime")
class AnimeDocument (
    @Id
    @Field(store = true)
    var animeNo: Long = 0,

    @Field(index = true)
    var week: String = "",

    @Field(index = true)
    var subject: String = "",

    @Field(index = true)
    var genres: List<String> = listOf(),

    @Field(index = true)
    var status: String = "",

    @Field(index = true)
    var translators: List<String> = listOf(),

    @Field(index = true)
    var endDate: Long = 0,
)
