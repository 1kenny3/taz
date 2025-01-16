from django.contrib import admin
from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static

urlpatterns = [
    # path('jet/', include('jet.urls', 'jet')),
    path('admin/', admin.site.urls),
    # другие маршруты...
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT) 