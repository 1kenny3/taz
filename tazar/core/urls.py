from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (
    UserViewSet, CollectionPointViewSet, TrashReportViewSet,
    AchievementViewSet, UserAchievementViewSet,
    home, features, register, login_view, logout_view, map_view,
    dashboard, add_collection_point, contact_view, report_trash
)
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from rest_framework import permissions
from django.conf import settings
from django.conf.urls.static import static
from rest_framework_simplejwt.views import (
    TokenObtainPairView,
    TokenRefreshView,
)

router = DefaultRouter()
router.register(r'users', UserViewSet)
router.register(r'collection-points', CollectionPointViewSet)
router.register(r'trash-reports', TrashReportViewSet)
router.register(r'achievements', AchievementViewSet)
router.register(r'user-achievements', UserAchievementViewSet)

schema_view = get_schema_view(
   openapi.Info(
      title="TAZAR API",
      default_version='v1',
      description="Документация API для TAZAR",
      terms_of_service="https://www.google.com/policies/terms/",
      contact=openapi.Contact(email="support@tazar.com"),
      license=openapi.License(name="BSD License"),
   ),
   public=True,
   permission_classes=(permissions.AllowAny,),
)

urlpatterns = [
    path('', home, name='home'),
    path('features/', features, name='features'),
    path('register/', register, name='register'),
    path('login/', login_view, name='login'),
    path('logout/', logout_view, name='logout'),
    path('map/', map_view, name='map'),
    path('dashboard/', dashboard, name='dashboard'),
    path('swagger/', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
    path('redoc/', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc'),
    path('add-collection-point/', add_collection_point, name='add_collection_point'),
    path('api/token/', TokenObtainPairView.as_view(), name='token_obtain_pair'),
    path('api/token/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
    path('contact/', contact_view, name='contact'),
    path('report-trash/', report_trash, name='report_trash'),
    path('api/', include(router.urls)),
    path('api/auth/', include('rest_framework.urls')),
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT) 