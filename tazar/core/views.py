from django.shortcuts import render, redirect
from django.contrib.auth import login, authenticate, logout
from .models import User, CollectionPoint, TrashReport, Achievement, UserAchievement, UserRank, Team, Challenge, UserChallenge, EcoTip, Comment, DailyTask, UserDailyTask, TeamCompetition, TeamCompetitionResult, Notification, CleanupEvent
from .forms import UserRegisterForm, CollectionPointForm, LoginForm, TrashReportForm, UserProfileForm
from rest_framework import viewsets, permissions
from .serializers import (
    UserSerializer, CollectionPointSerializer, TrashReportSerializer,
    UserRankSerializer, TeamSerializer, ChallengeSerializer,
    UserChallengeSerializer, EcoTipSerializer, CommentSerializer,
    DailyTaskSerializer, UserDailyTaskSerializer, TeamCompetitionSerializer,
    TeamCompetitionResultSerializer, NotificationSerializer, CleanupEventSerializer,
    AchievementSerializer, UserAchievementSerializer
)
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from django.core import serializers
import json
from django.conf import settings
from django.utils import timezone
from rest_framework.decorators import action
from rest_framework.response import Response
from django.db.models import Count

class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer

class CollectionPointViewSet(viewsets.ModelViewSet):
    queryset = CollectionPoint.objects.all()
    serializer_class = CollectionPointSerializer

class TrashReportViewSet(viewsets.ModelViewSet):
    queryset = TrashReport.objects.all()
    serializer_class = TrashReportSerializer

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

class TeamViewSet(viewsets.ModelViewSet):
    queryset = Team.objects.all()
    serializer_class = TeamSerializer

class ChallengeViewSet(viewsets.ModelViewSet):
    queryset = Challenge.objects.all()
    serializer_class = ChallengeSerializer

class EcoTipViewSet(viewsets.ModelViewSet):
    queryset = EcoTip.objects.all()
    serializer_class = EcoTipSerializer
    permission_classes = [permissions.AllowAny]

class DailyTaskViewSet(viewsets.ModelViewSet):
    queryset = DailyTask.objects.all()
    serializer_class = DailyTaskSerializer

class TeamCompetitionViewSet(viewsets.ModelViewSet):
    queryset = TeamCompetition.objects.all()
    serializer_class = TeamCompetitionSerializer

class CleanupEventViewSet(viewsets.ModelViewSet):
    queryset = CleanupEvent.objects.all()
    serializer_class = CleanupEventSerializer

    def perform_create(self, serializer):
        serializer.save(organizer=self.request.user)

class NotificationViewSet(viewsets.ModelViewSet):
    serializer_class = NotificationSerializer
    queryset = Notification.objects.all()

    def get_queryset(self):
        return Notification.objects.filter(user=self.request.user)

class CommentViewSet(viewsets.ModelViewSet):
    queryset = Comment.objects.all()
    serializer_class = CommentSerializer

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

class AchievementViewSet(viewsets.ModelViewSet):
    """
    API для работы с достижениями.
    """
    queryset = Achievement.objects.all()
    serializer_class = AchievementSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]

    @action(detail=False, methods=['get'])
    def stats(self, request):
        """
        Получить статистику по достижениям.
        """
        total_achievements = Achievement.objects.count()
        achievements_by_type = Achievement.objects.values('type').annotate(count=Count('id'))
        
        return Response({
            'total': total_achievements,
            'by_type': achievements_by_type
        })

    @action(detail=True, methods=['get'])
    def users(self, request, pk=None):
        """
        Получить список пользователей с этим достижением.
        """
        achievement = self.get_object()
        users = achievement.userachievement_set.select_related('user')
        data = [
            {
                'user_id': ua.user.id,
                'username': ua.user.username,
                'date_earned': ua.date_earned
            }
            for ua in users
        ]
        return Response(data)

class UserAchievementViewSet(viewsets.ModelViewSet):
    queryset = UserAchievement.objects.all()
    serializer_class = UserAchievementSerializer

    def get_queryset(self):
        if getattr(self, 'swagger_fake_view', False):
            return UserAchievement.objects.none()
        return UserAchievement.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

def home(request):
    return render(request, 'core/home.html')

def features(request):
    return render(request, 'core/features.html')

def register(request):
    if request.method == 'POST':
        form = UserRegisterForm(request.POST)
        if form.is_valid():
            user = form.save()
            login(request, user)
            return redirect('home')
    else:
        form = UserRegisterForm()
    return render(request, 'core/register.html', {'form': form})

def login_view(request):
    if request.method == 'POST':
        form = LoginForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data.get('username')
            password = form.cleaned_data.get('password')
            user = authenticate(username=username, password=password)
            if user is not None:
                login(request, user)
                return redirect('home')
            else:
                messages.error(request, 'Неверное имя пользователя или пароль')
    else:
        form = LoginForm()
    
    return render(request, 'core/login.html', {'form': form})

def logout_view(request):
    logout(request)
    return redirect('home')

def map_view(request):
    collection_points = CollectionPoint.objects.all()
    points_list = []
    
    for point in collection_points:
        point_data = {
            'latitude': float(point.latitude),
            'longitude': float(point.longitude),
            'address': point.address,
            'waste_types': point.waste_types,
            'waste_types_display': point.get_waste_types_display(),
            'city': point.city
        }
        print(f"Point {point.address}: waste_types = {point.waste_types}")
        points_list.append(point_data)
    
    return render(request, 'core/map.html', {
        'collection_points': json.dumps(points_list),
        'points_list': collection_points
    })

@login_required
def dashboard(request):
    if request.method == 'POST':
        form = UserProfileForm(request.POST, request.FILES, instance=request.user)
        if form.is_valid():
            if request.POST.get('clear_photo'):
                request.user.profile_photo.delete(save=True)
                form.cleaned_data['profile_photo'] = None
            form.save()
            messages.success(request, 'Профиль успешно обновлен')
            return redirect('dashboard')
    else:
        form = UserProfileForm(instance=request.user)
    
    user_reports = TrashReport.objects.filter(user=request.user).order_by('-created_at')
    
    # Статистика
    stats = {
        'total_reports': user_reports.count(),
        'completed_reports': user_reports.filter(status='completed').count(),
        'total_points': request.user.points,
        'reports_this_month': user_reports.filter(
            created_at__month=timezone.now().month
        ).count(),
    }
    
    # Достижения
    achievements = Achievement.objects.all()
    user_achievements = UserAchievement.objects.filter(user=request.user)
    
    return render(request, 'core/dashboard.html', {
        'user_reports': user_reports,
        'form': form,
        'stats': stats,
        'achievements': achievements,
        'user_achievements': user_achievements,
    })

@login_required
def add_collection_point(request):
    if request.method == 'POST':
        form = CollectionPointForm(request.POST)
        if form.is_valid():
            form.save()
            return redirect('map')
    else:
        form = CollectionPointForm()
    return render(request, 'core/add_collection_point.html', {'form': form})

def contact_view(request):
    return render(request, 'core/contact.html')

@login_required
def report_trash(request):
    if request.method == 'POST':
        form = TrashReportForm(request.POST, request.FILES)
        if form.is_valid():
            report = form.save(commit=False)
            report.user = request.user
            report.save()
            messages.success(request, 'Спасибо! Ваше сообщение отправлено.')
            return redirect('dashboard')
    else:
        form = TrashReportForm()
    
    return render(request, 'core/report_trash.html', {
        'form': form,
        'google_maps_api_key': settings.GOOGLE_MAPS_API_KEY
    })
