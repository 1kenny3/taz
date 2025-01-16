from django.contrib import admin
from .models import CollectionPoint, User
from django.utils.html import format_html

@admin.register(CollectionPoint)
class CollectionPointAdmin(admin.ModelAdmin):
    list_display = ('id', 'city', 'address', 'waste_types')
    search_fields = ('city', 'address', 'waste_types')

@admin.register(User)
class UserAdmin(admin.ModelAdmin):
    list_display = ('id', 'username', 'email', 'is_collector', 'points')
    search_fields = ('username', 'email')
    list_filter = ('is_collector',)
