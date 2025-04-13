from drf_yasg import openapi
from drf_yasg.views import get_schema_view
from rest_framework import permissions
from django.urls import path, include

schema_view = get_schema_view(
    openapi.Info(
        title="TAZAR API",
        default_version='v1',
        description="""
        # TAZAR - Платформа для решения экологических проблем
        
        ## Типы отходов
        
        ### 🔄 Основные типы отходов:
        * 🔵 `plastic` - Пластик
          - ПЭТ бутылки
          - Пластиковые контейнеры
          - Пакеты
        * 📄 `paper` - Бумага
          - Картон
          - Газеты
          - Журналы
        * ⚙️ `metal` - Металл
          - Алюминиевые банки
          - Железный лом
          - Металлическая тара
        * 🔍 `glass` - Стекло
          - Стеклянные бутылки
          - Оконное стекло
          - Стеклотара
        
        ### 🆕 Новые типы отходов:
        * 🏥 `medical` - Медицинские отходы
          - Медицинские инструменты
          - Использованные материалы
          - Фармацевтические отходы
          - Биологические отходы
        
        * 🏗️ `construction` - Строительные отходы
          - Строительный мусор
          - Демонтажные материалы
          - Остатки стройматериалов
          - Бетон и кирпич
        
        * 🌾 `agricultural` - Сельские отходы
          - Органические отходы
          - Сельскохозяйственные остатки
          - Отходы животноводства
          - Растительные остатки
        
        ## API Endpoints
        
        ### Пункты приема отходов
        * GET `/api/collection-points/` - Получить список всех пунктов
        * GET `/api/collection-points/?waste_types=medical` - Фильтрация по типу
        * GET `/api/collection-points/?city=Бишкек` - Фильтрация по городу
        * GET `/api/collection-points/waste_types/` - Список всех типов отходов
        
        ### Фильтрация
        Используйте следующие параметры для фильтрации:
        * `waste_types` - тип отходов (например: medical, construction, agricultural)
        * `city` - название города
        * `search` - поиск по адресу или описанию
        
        ## Аутентификация
        * Используйте JWT токены для авторизации
        * Добавляйте токен в заголовок: `Authorization: Bearer <token>`
        
        ## Форматы данных
        * Все запросы и ответы в формате JSON
        * Даты в формате ISO 8601
        * Координаты в формате decimal degrees (DD)
        """,
        terms_of_service="https://www.tazar.kg/terms/",
        contact=openapi.Contact(email="contact@tazar.kg"),
        license=openapi.License(name="BSD License"),
    ),
    public=True,
    permission_classes=[permissions.AllowAny],
    patterns=[path('api/', include('core.urls'))],
) 