class HomeViewModel : ViewModel() {
    private val _news = MutableStateFlow<List<News>>(emptyList())
    val news = _news.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val response = NewsApi.instance.getEcoNews()
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    _news.value = newsResponse?.articles?.map { article ->
                        News(
                            title = article.title,
                            description = article.description,
                            url = article.url,
                            imageUrl = article.urlToImage,
                            publishedAt = article.publishedAt,
                            source = article.source.name
                        )
                    } ?: emptyList()
                } else {
                    _error.value = "Ошибка загрузки новостей: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 