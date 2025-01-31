import os
from django.conf import settings

def create_media_directories():
    """Создает необходимые директории для медиафайлов"""
    directories = [
        settings.MEDIA_ROOT,
        os.path.join(settings.MEDIA_ROOT, 'profile_photos'),
        os.path.join(settings.MEDIA_ROOT, 'trash_reports')
    ]
    
    for directory in directories:
        os.makedirs(directory, exist_ok=True) 