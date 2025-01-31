"""
URL configuration for tazar project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static
from rest_framework import permissions
from drf_yasg.views import get_schema_view
from drf_yasg import openapi

schema_view = get_schema_view(
   openapi.Info(
      title="TAZAR API",
      default_version='v1',
      description="""
      # TAZAR - Платформа для решения экологических проблем
      
      ## Основные эндпоинты:
      
      ### 👤 Пользователи
      * `/api/users/` - Управление пользователями
      * `/api/auth/` - Аутентификация
      
      ### 🗑️ Отчеты о мусоре
      * `/api/trash-reports/` - Создание и просмотр отчетов
      * Поддерживает загрузку фото
      * Геолокация мест
      * Отслеживание статуса
      
      ### 📍 Пункты сбора
      * `/api/collection-points/` - Пункты приема отходов
      * Фильтрация по типам отходов
      * Поиск по адресу
      
      ### 🏆 Достижения
      * `/api/achievements/` - Список достижений
      * `/api/user-achievements/` - Достижения пользователя
      * Система баллов и наград
      
      ## Аутентификация
      * Используйте JWT токены для авторизации
      * Добавляйте токен в заголовок: `Authorization: Bearer <token>`
      
      ## Форматы данных
      * Все запросы и ответы в формате JSON
      * Загрузка файлов через multipart/form-data
      """,
      terms_of_service="https://tazar.com/terms/",
      contact=openapi.Contact(email="support@tazar.com"),
      license=openapi.License(name="MIT License"),
   ),
   public=True,
   permission_classes=(permissions.AllowAny,),
   patterns=[
       path('api/', include('core.urls')),
   ],
)

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', include('core.urls')),
    path('swagger/', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
    path('redoc/', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc'),
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
