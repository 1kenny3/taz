from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (
    UserViewSet, 
    CollectionPointViewSet,
    home, 
    features, 
    register, 
    login_view, 
    logout_view, 
    map_view, 
    dashboard,
    add_collection_point, 
    contact_view
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
    path('', include(router.urls)),
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT) 