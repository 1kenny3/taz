import uuid
import os
from django.db import models
from django.contrib.auth.models import AbstractUser
from django.utils import timezone
from django.db.models.signals import pre_save, post_save
from django.dispatch import receiver

def get_profile_photo_path(instance, filename):
    ext = filename.split('.')[-1]
    filename = f'{uuid.uuid4()}.{ext}'
    return os.path.join('profile_photos', filename)

def get_trash_report_photo_path(instance, filename):
    ext = filename.split('.')[-1]
    filename = f'{uuid.uuid4()}.{ext}'
    return os.path.join('trash_reports', filename)

class User(AbstractUser):
    is_collector = models.BooleanField(default=False)
    points = models.IntegerField(default=0)
    profile_photo = models.ImageField(
        upload_to=get_profile_photo_path,
        null=True, 
        blank=True, 
        verbose_name='Фото профиля'
    )
    bio = models.TextField(max_length=500, blank=True, verbose_name='О себе')

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

class TrashReport(models.Model):
    STATUS_CHOICES = [
        ('new', 'Новый'),
        ('in_progress', 'В обработке'),
        ('completed', 'Убрано'),
        ('rejected', 'Отклонено'),
    ]

    POINTS_FOR_COMPLETION = 10

    user = models.ForeignKey(User, on_delete=models.CASCADE, verbose_name='Пользователь')
    address = models.CharField(max_length=255, verbose_name='Адрес')
    description = models.TextField(verbose_name='Описание')
    photo = models.ImageField(
        upload_to=get_trash_report_photo_path,
        verbose_name='Фото'
    )
    latitude = models.FloatField(verbose_name='Широта')
    longitude = models.FloatField(verbose_name='Долгота')
    status = models.CharField(
        max_length=20, 
        choices=STATUS_CHOICES, 
        default='new',
        verbose_name='Статус'
    )
    points_awarded = models.BooleanField(default=False, verbose_name='Баллы начислены')
    created_at = models.DateTimeField(auto_now_add=True, verbose_name='Дата создания')
    updated_at = models.DateTimeField(auto_now=True, verbose_name='Дата обновления')

    class Meta:
        verbose_name = 'Сообщение о мусоре'
        verbose_name_plural = 'Сообщения о мусоре'
        ordering = ['-created_at']

    def __str__(self):
        return f"Сообщение о мусоре по адресу {self.address}"

    def award_points(self):
        if not self.points_awarded and self.status == 'completed':
            self.user.points += self.POINTS_FOR_COMPLETION
            self.user.save()
            self.points_awarded = True
            self.save()

class Achievement(models.Model):
    TYPES = [
        ('reports', 'За отчеты'),
        ('points', 'За баллы'),
        ('activity', 'За активность'),
    ]

    name = models.CharField(max_length=100, verbose_name='Название')
    description = models.TextField(verbose_name='Описание')
    icon = models.CharField(max_length=50, verbose_name='Иконка')
    points_required = models.IntegerField(verbose_name='Требуемые баллы')
    type = models.CharField(max_length=20, choices=TYPES, default='points')
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = 'Достижение'
        verbose_name_plural = 'Достижения'
        ordering = ['points_required']

    def __str__(self):
        return self.name

class UserAchievement(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    achievement = models.ForeignKey(Achievement, on_delete=models.CASCADE)
    date_earned = models.DateTimeField(auto_now_add=True)

    class Meta:
        unique_together = ['user', 'achievement']

class UserRank(models.Model):
    """
    Модель для рангов пользователей.
    Определяет уровень достижений пользователя.
    """
    name = models.CharField(max_length=100, verbose_name='Название ранга',
                          help_text='Например: "Новичок", "Эко-воин"')
    min_points = models.IntegerField(verbose_name='Минимум баллов',
                                   help_text='Количество баллов для получения ранга')
    icon = models.CharField(max_length=50, verbose_name='Иконка',
                          help_text='Эмодзи или класс иконки')
    color = models.CharField(max_length=20, verbose_name='Цвет',
                           help_text='HEX или название цвета')

    class Meta:
        verbose_name = 'Ранг пользователя'
        verbose_name_plural = 'Ранги пользователей'
        ordering = ['min_points']

    def __str__(self):
        return f"{self.name} ({self.min_points} баллов)"

class Team(models.Model):
    name = models.CharField(max_length=100, verbose_name='Название команды')
    description = models.TextField(verbose_name='Описание')
    leader = models.ForeignKey('User', on_delete=models.SET_NULL, null=True, related_name='led_teams')
    total_points = models.IntegerField(default=0, verbose_name='Общие баллы')
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.name

class Challenge(models.Model):
    name = models.CharField(max_length=100, verbose_name='Название')
    description = models.TextField(verbose_name='Описание')
    required_reports = models.IntegerField(verbose_name='Требуемое количество отчетов')
    time_frame = models.IntegerField(verbose_name='Срок выполнения (дней)')
    bonus_points = models.IntegerField(verbose_name='Бонусные баллы')

    def __str__(self):
        return self.name

class UserChallenge(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    challenge = models.ForeignKey(Challenge, on_delete=models.CASCADE)
    start_date = models.DateTimeField(auto_now_add=True)
    completed = models.BooleanField(default=False)

    class Meta:
        unique_together = ['user', 'challenge']

class EcoTip(models.Model):
    CATEGORIES = [
        ('waste', 'Отходы'),
        ('energy', 'Энергия'),
        ('water', 'Вода'),
        ('transport', 'Транспорт'),
    ]
    
    DIFFICULTY = [
        ('easy', 'Легко'),
        ('medium', 'Средне'),
        ('hard', 'Сложно'),
    ]

    title = models.CharField(max_length=200, verbose_name='Заголовок')
    content = models.TextField(verbose_name='Содержание')
    image = models.ImageField(upload_to='eco_tips/', verbose_name='Изображение')
    category = models.CharField(max_length=50, choices=CATEGORIES, verbose_name='Категория')
    difficulty = models.CharField(max_length=20, choices=DIFFICULTY, verbose_name='Сложность')

    def __str__(self):
        return self.title

class Comment(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    trash_report = models.ForeignKey('TrashReport', on_delete=models.CASCADE)
    content = models.TextField(verbose_name='Комментарий')
    created_at = models.DateTimeField(auto_now_add=True)
    parent = models.ForeignKey('self', null=True, blank=True, on_delete=models.CASCADE)

    def __str__(self):
        return f'Комментарий от {self.user.username}'

class DailyTask(models.Model):
    title = models.CharField(max_length=200, verbose_name='Заголовок')
    description = models.TextField(verbose_name='Описание')
    points = models.IntegerField(verbose_name='Баллы')
    expires_in = models.IntegerField(verbose_name='Срок действия (часов)')

    def __str__(self):
        return self.title

class UserDailyTask(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    task = models.ForeignKey(DailyTask, on_delete=models.CASCADE)
    completed = models.BooleanField(default=False)
    assigned_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        unique_together = ['user', 'task', 'assigned_at']

class TeamCompetition(models.Model):
    title = models.CharField(max_length=200, verbose_name='Название')
    description = models.TextField(verbose_name='Описание')
    start_date = models.DateTimeField(verbose_name='Дата начала')
    end_date = models.DateTimeField(verbose_name='Дата окончания')
    teams = models.ManyToManyField(Team, through='TeamCompetitionResult')

    def __str__(self):
        return self.title

class TeamCompetitionResult(models.Model):
    team = models.ForeignKey(Team, on_delete=models.CASCADE)
    competition = models.ForeignKey(TeamCompetition, on_delete=models.CASCADE)
    points = models.IntegerField(default=0)
    rank = models.IntegerField(null=True)

    class Meta:
        unique_together = ['team', 'competition']

class Notification(models.Model):
    TYPES = [
        ('achievement', 'Новое достижение'),
        ('level_up', 'Новый уровень'),
        ('report_status', 'Статус отчета изменен'),
        ('challenge', 'Новый вызов'),
    ]
    
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    type = models.CharField(max_length=20, choices=TYPES)
    title = models.CharField(max_length=200)
    message = models.TextField()
    read = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f'{self.title} для {self.user.username}'

class CleanupEvent(models.Model):
    organizer = models.ForeignKey(User, on_delete=models.CASCADE, related_name='organized_events')
    title = models.CharField(max_length=200, verbose_name='Название')
    description = models.TextField(verbose_name='Описание')
    location = models.CharField(max_length=255, verbose_name='Место проведения')
    date = models.DateTimeField(verbose_name='Дата и время')
    max_participants = models.IntegerField(verbose_name='Максимум участников')
    participants = models.ManyToManyField(User, related_name='cleanup_events')

    def __str__(self):
        return self.title

@receiver(pre_save, sender=TrashReport)
def handle_status_change(sender, instance, **kwargs):
    if instance.id:  # если это существующий объект
        old_instance = TrashReport.objects.get(id=instance.id)
        if old_instance.status != instance.status and instance.status == 'completed':
            instance.award_points()
