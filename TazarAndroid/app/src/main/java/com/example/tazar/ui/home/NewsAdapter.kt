class NewsAdapter : ListAdapter<News, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NewsViewHolder(
        private val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            binding.apply {
                titleText.text = news.title
                descriptionText.text = news.description
                sourceText.text = news.source
                
                // Форматирование даты
                val prettyTime = PrettyTime(Locale("ru"))
                val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    .parse(news.publishedAt)
                dateText.text = prettyTime.format(date)

                // Загрузка изображения
                news.imageUrl?.let { url ->
                    Glide.with(newsImage)
                        .load(url)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_error_placeholder)
                        .into(newsImage)
                }

                // Обработка клика по новости
                root.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
                    root.context.startActivity(intent)
                }
            }
        }
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }
} 