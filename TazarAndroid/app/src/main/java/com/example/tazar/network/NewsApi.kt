interface NewsApi {
    @GET("everything")
    suspend fun getEcoNews(
        @Query("q") query: String = "экология OR природа OR переработка",
        @Query("language") language: String = "ru",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_KEY
    ): Response<NewsResponse>
}

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

data class Article(
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val source: Source
)

data class Source(
    val name: String
) 