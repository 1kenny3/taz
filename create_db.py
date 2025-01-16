import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'tazar.settings')
django.setup()

from core.models import CollectionPoint

# Пример данных для Бишкека
collection_points = [
    {
        "city": "Бишкек",
        "address": "Улица Ленина, 1",
        "waste_types": "Пластик, Бумага",
        "latitude": 42.8746,
        "longitude": 74.6122
    },
    {
        "city": "Бишкек",
        "address": "Проспект Чуй, 100",
        "waste_types": "Металл, Стекло",
        "latitude": 42.8765,
        "longitude": 74.6059
    }
]

for point in collection_points:
    CollectionPoint.objects.create(**point) 