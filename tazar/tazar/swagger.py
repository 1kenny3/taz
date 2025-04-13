from drf_yasg import openapi
from drf_yasg.views import get_schema_view
from rest_framework import permissions

schema_view = get_schema_view(
    openapi.Info(
        title="Tazar API",
        default_version='v1',
        description="""
        # API для экологической платформы Tazar
        
        ## Типы отходов
        
        ### Основные типы:
        - 🔵 Пластик (plastic)
        - 📄 Бумага (paper)
        - ⚙️ Металл (metal)
        - 🔍 Стекло (glass)
        
        ### Новые типы:
        - 🏥 Медицинские отходы (medical)
          * Медицинские инструменты
          * Использованные материалы
          * Фармацевтические отходы
        
        - 🏗️ Строительные отходы (construction)
          * Строительный мусор
          * Демонтажные материалы
          * Остатки стройматериалов
        
        - 🌾 Сельские отходы (agricultural)
          * Органические отходы
          * Сельскохозяйственные остатки
          * Отходы животноводства
        
        ## Фильтрация
        Используйте параметр `waste_types` для фильтрации пунктов приема по типу отходов.
        
        Пример: `/api/collection-points/?waste_types=medical`
        """,
        terms_of_service="https://www.tazar.kg/terms/",
        contact=openapi.Contact(email="contact@tazar.kg"),
        license=openapi.License(name="BSD License"),
    ),
    public=True,
    permission_classes=[permissions.AllowAny],
) 