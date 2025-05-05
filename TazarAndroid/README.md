# Tazar Mobile

Мобильное приложение для экологического проекта Tazar.

## Описание

Tazar Mobile - это Android-приложение, которое позволяет пользователям:
- Сообщать о местах скопления мусора с фото и геолокацией 📍
- Находить точки сбора различных видов отходов на карте 🗑️
- Участвовать в экологических мероприятиях и командах 👥
- Получать достижения и повышать свой рейтинг 🏆
- Просматривать эко-советы и новости 📰

## Технологии

- Android Studio
- Java/Kotlin
- Retrofit для REST API
- Google Maps SDK
- Glide для работы с изображениями
- Room для локальной базы данных
- Firebase для push-уведомлений
- Material Design компоненты

## Структура проекта

```
app/
├── src/main/
│   ├── java/com/tazar/android/
│   │   ├── api/          # API интерфейсы и модели
│   │   ├── ui/           # Activity и Fragment классы
│   │   │   ├── auth/     # Авторизация и регистрация
│   │   │   ├── map/      # Карта с пунктами приема
│   │   │   ├── profile/  # Профиль пользователя
│   │   │   ├── reports/  # Отчеты о мусоре
│   │   │   └── main/     # Главный экран
│   │   ├── utils/        # Утилиты и вспомогательные классы
│   │   └── models/       # Модели данных
│   └── res/              # Ресурсы приложения
└── build.gradle          # Зависимости и конфигурация сборки
```

## API-интеграция

Приложение взаимодействует с Django REST API:

```java
public interface TazarApi {
    // Аутентификация
    @POST("api/token/")
    Call<TokenResponse> getToken(@Body LoginRequest loginRequest);
    
    // Отчеты о мусоре
    @GET("api/trash-reports/")
    Call<List<TrashReport>> getTrashReports();
    
    @POST("api/trash-reports/")
    Call<TrashReport> createTrashReport(@Body TrashReportRequest request);
    
    // Пункты приема
    @GET("api/collection-points/")
    Call<List<CollectionPoint>> getCollectionPoints(@Query("waste_types") String wasteType);
    
    // Достижения пользователя
    @GET("api/user-achievements/")
    Call<List<Achievement>> getUserAchievements();
}
```

## Настройка проекта

1. Клонируйте репозиторий
```bash
git clone https://github.com/your-username/TazarAndroid.git
```

2. Создайте файл `local.properties` в корне проекта:
```properties
sdk.dir=/path/to/your/android/sdk
BASE_API_URL="https://api.tazar.kg/"
MAPS_API_KEY="your_google_maps_key"
```

3. Откройте проект в Android Studio
4. Запустите приложение

## Требования

- Android 8.0 (API level 26) и выше
- Подключение к интернету
- Разрешения на доступ к камере и геолокации

## Функции приложения

### Авторизация и регистрация
- Вход по email и паролю
- Регистрация нового пользователя
- Подтверждение email
- Восстановление пароля

### Карта пунктов приема
- Отображение точек приема на карте
- Фильтрация по типам отходов
- Детальная информация о пункте
- Прокладывание маршрута до пункта

### Отчеты о мусоре
- Создание отчета с фото
- Определение местоположения автоматически
- Отслеживание статуса отчета
- Комментирование отчетов

### Профиль пользователя
- Отображение заработанных баллов
- Полученные достижения
- История отчетов
- Редактирование личной информации

### Эко-советы и новости
- Актуальные новости экологии
- Практические советы по сортировке
- Информация о мероприятиях

## Контакты

По всем вопросам обращайтесь:
- Email: support@tazar.kg
- Telegram: @tazar_support 