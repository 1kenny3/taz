from django.db import models
from django.contrib.auth.models import AbstractUser
from django.utils import timezone

class User(AbstractUser):
    is_collector = models.BooleanField(default=False)
    points = models.IntegerField(default=0)

    def __str__(self):
        return self.username

class CollectionPoint(models.Model):
    WASTE_TYPES = [
        ('plastic', 'Пластик'),
        ('paper', 'Бумага'),
        ('metal', 'Металл'),
        ('glass', 'Стекло'),
    ]
    
    waste_types = models.CharField(
        max_length=50,
        choices=WASTE_TYPES,
        verbose_name='Тип отходов'
    )
    city = models.CharField(max_length=100)
    address = models.CharField(max_length=255)
    latitude = models.FloatField()
    longitude = models.FloatField()

    def __str__(self):
        return f"{self.city}, {self.address}"
