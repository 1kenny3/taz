from rest_framework import serializers
from drf_yasg.utils import swagger_serializer_method
from .models import (
    User, CollectionPoint, TrashReport, UserRank, Team, Challenge, 
    UserChallenge, EcoTip, Comment, DailyTask, UserDailyTask,
    TeamCompetition, TeamCompetitionResult, Notification, CleanupEvent,
    Achievement, UserAchievement, News
)
from django.contrib.auth import get_user_model

User = get_user_model()

class UserRankSerializer(serializers.ModelSerializer):
    """
    Сериализатор для рангов пользователей.
    """
    class Meta:
        model = UserRank
        fields = '__all__'
        swagger_schema_fields = {
            "title": "Ранг пользователя",
            "description": "Уровень достижений пользователя"
        }

class TeamSerializer(serializers.ModelSerializer):
    """
    Сериализатор для команд.
    """
    members_count = serializers.SerializerMethodField()
    leader_name = serializers.CharField(source='leader.username', read_only=True)

    class Meta:
        model = Team
        fields = '__all__'
        swagger_schema_fields = {
            "title": "Команда",
            "description": "Группа пользователей для совместной работы"
        }

    @swagger_serializer_method(serializer_or_field=serializers.IntegerField)
    def get_members_count(self, obj):
        return obj.members.count()

class UserSerializer(serializers.ModelSerializer):
    """
    Сериализатор для пользователей системы.
    """
    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'points', 'profile_photo', 'bio']
        swagger_schema_fields = {
            "title": "Пользователь",
            "description": "Данные пользователя системы"
        }

class ChallengeSerializer(serializers.ModelSerializer):
    class Meta:
        model = Challenge
        fields = '__all__'

class UserChallengeSerializer(serializers.ModelSerializer):
    challenge_details = ChallengeSerializer(source='challenge', read_only=True)

    class Meta:
        model = UserChallenge
        fields = '__all__'

class EcoTipSerializer(serializers.ModelSerializer):
    class Meta:
        model = EcoTip
        fields = '__all__'

class CommentSerializer(serializers.ModelSerializer):
    user_name = serializers.CharField(source='user.username', read_only=True)
    user_photo = serializers.ImageField(source='user.profile_photo', read_only=True)

    class Meta:
        model = Comment
        fields = '__all__'

class DailyTaskSerializer(serializers.ModelSerializer):
    class Meta:
        model = DailyTask
        fields = '__all__'

class UserDailyTaskSerializer(serializers.ModelSerializer):
    task_details = DailyTaskSerializer(source='task', read_only=True)

    class Meta:
        model = UserDailyTask
        fields = '__all__'

class TeamCompetitionSerializer(serializers.ModelSerializer):
    class Meta:
        model = TeamCompetition
        fields = '__all__'

class TeamCompetitionResultSerializer(serializers.ModelSerializer):
    team_name = serializers.CharField(source='team.name', read_only=True)

    class Meta:
        model = TeamCompetitionResult
        fields = '__all__'

class NotificationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Notification
        fields = '__all__'

class CleanupEventSerializer(serializers.ModelSerializer):
    organizer_name = serializers.CharField(source='organizer.username', read_only=True)
    participants_count = serializers.SerializerMethodField()

    class Meta:
        model = CleanupEvent
        fields = '__all__'

    def get_participants_count(self, obj):
        return obj.participants.count()

class TrashReportSerializer(serializers.ModelSerializer):
    """
    Сериализатор для отчетов о мусоре.
    """
    user_name = serializers.CharField(source='user.username', read_only=True)
    comments = CommentSerializer(many=True, read_only=True)

    class Meta:
        model = TrashReport
        fields = '__all__'
        swagger_schema_fields = {
            "title": "Отчет о мусоре",
            "description": "Информация о местах скопления мусора"
        }

class CollectionPointSerializer(serializers.ModelSerializer):
    """
    Сериализатор для пунктов сбора отходов.
    """
    class Meta:
        model = CollectionPoint
        fields = '__all__'
        swagger_schema_fields = {
            "title": "Пункт сбора",
            "description": "Место приема различных типов отходов"
        }

class AchievementSerializer(serializers.ModelSerializer):
    """
    Сериализатор для достижений.
    """
    class Meta:
        model = Achievement
        fields = ['id', 'name', 'description', 'icon', 'points_required', 'type', 'created_at']
        swagger_schema_fields = {
            "title": "Достижение",
            "description": "Награда за определенные действия",
            "example": {
                "name": "Начинающий эколог",
                "description": "Создайте первый отчет о мусоре",
                "icon": "🌱",
                "points_required": 10,
                "type": "reports"
            }
        }

class UserAchievementSerializer(serializers.ModelSerializer):
    """
    Сериализатор для достижений пользователя.
    """
    achievement_details = AchievementSerializer(source='achievement', read_only=True)

    class Meta:
        model = UserAchievement
        fields = '__all__'
        swagger_schema_fields = {
            "title": "Достижение пользователя",
            "description": "Связь между пользователем и полученным достижением"
        }

class UserRegisterSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True)

    class Meta:
        model = User
        fields = ['username', 'email', 'password']

    def create(self, validated_data):
        user = User(**validated_data)
        user.set_password(validated_data['password'])  # Хранить пароль в зашифрованном виде
        user.save()
        return user

class NewsSerializer(serializers.ModelSerializer):
    class Meta:
        model = News
        fields = '__all__' 