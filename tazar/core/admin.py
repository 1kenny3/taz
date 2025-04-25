from django.contrib import admin
from .models import CollectionPoint, User, TrashReport, News
from django.utils.html import format_html
from django.contrib import messages

@admin.register(CollectionPoint)
class CollectionPointAdmin(admin.ModelAdmin):
    list_display = ('id', 'city', 'address', 'waste_types')
    search_fields = ('city', 'address', 'waste_types')

@admin.register(User)
class UserAdmin(admin.ModelAdmin):
    list_display = ('id', 'username', 'email', 'is_collector', 'points')
    search_fields = ('username', 'email')
    list_filter = ('is_collector',)

@admin.register(TrashReport)
class TrashReportAdmin(admin.ModelAdmin):
    list_display = ('address', 'user', 'status', 'created_at', 'preview_photo', 'points_status', 'actions_buttons')
    list_filter = ('status', 'created_at', 'points_awarded')
    search_fields = ('address', 'description', 'user__username')
    readonly_fields = ('created_at', 'updated_at', 'preview_photo', 'points_awarded')
    list_per_page = 20
    actions = ['mark_as_in_progress', 'mark_as_completed', 'mark_as_rejected']
    
    def preview_photo(self, obj):
        if obj.photo:
            return format_html('<img src="{}" width="100" height="100" style="object-fit: cover; border-radius: 5px;"/>', obj.photo.url)
        return "Нет фото"
    preview_photo.short_description = 'Фото'

    def points_status(self, obj):
        if obj.points_awarded:
            return format_html('<span style="color: green;">✓ Начислено {} баллов</span>', obj.POINTS_FOR_COMPLETION)
        return format_html('<span style="color: gray;">Не начислено</span>')
    points_status.short_description = 'Статус баллов'

    def actions_buttons(self, obj):
        if obj.status == 'new':
            return format_html(
                '<a class="button" href="{}?status=in_progress" style="background-color: #007bff; color: white; padding: 5px 10px; border-radius: 5px; margin-right: 5px;">Взять в работу</a>'
                '<a class="button" href="{}?status=rejected" style="background-color: #dc3545; color: white; padding: 5px 10px; border-radius: 5px;">Отклонить</a>',
                obj.id, obj.id
            )
        elif obj.status == 'in_progress':
            return format_html(
                '<a class="button" href="{}?status=completed" style="background-color: #28a745; color: white; padding: 5px 10px; border-radius: 5px;">Завершить</a>',
                obj.id
            )
        return ""
    actions_buttons.short_description = 'Действия'

    fieldsets = (
        ('Основная информация', {
            'fields': ('user', 'status', 'address', 'description')
        }),
        ('Местоположение', {
            'fields': ('latitude', 'longitude')
        }),
        ('Медиа', {
            'fields': ('photo', 'preview_photo')
        }),
        ('Временные метки', {
            'fields': ('created_at', 'updated_at'),
            'classes': ('collapse',)
        })
    )

    def get_readonly_fields(self, request, obj=None):
        if obj:  # если это существующий объект
            return self.readonly_fields + ('user',)
        return self.readonly_fields

    def mark_as_in_progress(self, request, queryset):
        queryset.update(status='in_progress')
    mark_as_in_progress.short_description = "Отметить как 'В обработке'"

    def mark_as_completed(self, request, queryset):
        for report in queryset:
            report.status = 'completed'
            report.save()  # это автоматически вызовет signal и начислит баллы
    mark_as_completed.short_description = "Отметить как 'Убрано'"

    def mark_as_rejected(self, request, queryset):
        queryset.update(status='rejected')
    mark_as_rejected.short_description = "Отметить как 'Отклонено'"

@admin.register(News)
class NewsAdmin(admin.ModelAdmin):
    list_display = ('title', 'created_at', 'updated_at', 'is_published', 'preview_image')
    list_filter = ('is_published', 'created_at')
    search_fields = ('title', 'content')
    readonly_fields = ('created_at', 'updated_at', 'preview_image')
    list_per_page = 20
    
    def preview_image(self, obj):
        if obj.image:
            return format_html('<img src="{}" width="100" height="100" style="object-fit: cover; border-radius: 5px;"/>', obj.image.url)
        return "Нет изображения"
    preview_image.short_description = 'Изображение' 