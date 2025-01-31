from django.db.models.signals import post_save
from django.dispatch import receiver
from .models import TrashReport, Achievement, UserAchievement

@receiver(post_save, sender=TrashReport)
def check_achievements(sender, instance, created, **kwargs):
    if created:
        user = instance.user
        reports_count = TrashReport.objects.filter(user=user).count()
        
        # Проверяем достижения за отчеты
        report_achievements = Achievement.objects.filter(
            type='reports',
            points_required__lte=reports_count
        )
        
        for achievement in report_achievements:
            UserAchievement.objects.get_or_create(
                user=user,
                achievement=achievement
            )
        
        # Проверяем достижения за баллы
        point_achievements = Achievement.objects.filter(
            type='points',
            points_required__lte=user.points
        )
        
        for achievement in point_achievements:
            UserAchievement.objects.get_or_create(
                user=user,
                achievement=achievement
            ) 