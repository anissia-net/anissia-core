package anissia.elasticsearch.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field

@Document(indexName = "anime")
data class AnimeDocument (
        @Id
        var id: Long = 0,
        @Field(store = true)
        var animeNo: Long = 0,
        @Field(index = true, analyzer = "standard", searchAnalyzer = "standard")
        var subject: String = "",
        @Field(index = true, analyzer = "whitespace", searchAnalyzer = "whitespace")
        var genres: String = "",
        @Field(index = true, analyzer = "standard", searchAnalyzer = "standard")
        var startDate: String = "",
        @Field(index = true, analyzer = "standard", searchAnalyzer = "standard")
        var endDate: String = ""
)
