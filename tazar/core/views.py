from django.shortcuts import render, redirect
from django.contrib.auth import login, authenticate, logout
from .models import User, CollectionPoint, TrashReport, Achievement, UserAchievement, UserRank, Team, Challenge, UserChallenge, EcoTip, Comment, DailyTask, UserDailyTask, TeamCompetition, TeamCompetitionResult, Notification, CleanupEvent, News
from .forms import UserRegisterForm, CollectionPointForm, LoginForm, TrashReportForm, UserProfileForm
from rest_framework import viewsets, permissions, generics
from .serializers import (
    UserSerializer, CollectionPointSerializer, TrashReportSerializer,
    UserRankSerializer, TeamSerializer, ChallengeSerializer,
    UserChallengeSerializer, EcoTipSerializer, CommentSerializer,
    DailyTaskSerializer, UserDailyTaskSerializer, TeamCompetitionSerializer,
    TeamCompetitionResultSerializer, NotificationSerializer, CleanupEventSerializer,
    AchievementSerializer, UserAchievementSerializer, UserRegisterSerializer,
    NewsSerializer
)
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from django.core import serializers
import json
from django.conf import settings
from django.utils import timezone
from rest_framework.decorators import action, api_view, permission_classes
from rest_framework.response import Response
from django.db.models import Count
from rest_framework_simplejwt.authentication import JWTAuthentication
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi
from .api_docs import (
    user_response, collection_point_response, trash_report_response,
    achievement_response, team_response, cleanup_event_response, news_response,
    collection_point_params, trash_report_params,
    user_register_request, collection_point_request, trash_report_request,
    achievement_request, team_request, cleanup_event_request, news_request
)

class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    permission_classes = [permissions.IsAuthenticated]
    authentication_classes = [JWTAuthentication]

    @swagger_auto_schema(
        responses={200: openapi.Response('Профиль текущего пользователя', user_response)}
    )
    @action(detail=False, methods=['get'])
    def me(self, request):
        """
        Получить профиль текущего пользователя
        """
        serializer = self.get_serializer(request.user)
        return Response(serializer.data)
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Таблица лидеров', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=user_response
        ))}
    )
    @action(detail=False, methods=['get'])
    def leaderboard(self, request):
        """
        Получить таблицу лидеров по баллам
        """
        top_users = User.objects.order_by('-points')[:20]
        serializer = self.get_serializer(top_users, many=True)
        return Response(serializer.data)

class CollectionPointViewSet(viewsets.ModelViewSet):
    queryset = CollectionPoint.objects.all()
    serializer_class = CollectionPointSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]
    authentication_classes = [JWTAuthentication]
    
    @swagger_auto_schema(
        manual_parameters=collection_point_params,
        responses={200: openapi.Response('Список пунктов приема', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=collection_point_response
        ))}
    )
    def list(self, request, *args, **kwargs):
        """
        Получить список всех пунктов приема отходов с возможностью фильтрации
        """
        return super().list(request, *args, **kwargs)
    
    @swagger_auto_schema(
        request_body=collection_point_request,
        responses={201: openapi.Response('Пункт приема создан', collection_point_response)}
    )
    def create(self, request, *args, **kwargs):
        """
        Создать новый пункт приема отходов
        """
        return super().create(request, *args, **kwargs)
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Доступные типы отходов', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=openapi.Schema(
                type=openapi.TYPE_OBJECT,
                properties={
                    'value': openapi.Schema(type=openapi.TYPE_STRING),
                    'label': openapi.Schema(type=openapi.TYPE_STRING),
                }
            )
        ))}
    )
    @action(detail=False, methods=['get'])
    def waste_types(self, request):
        """
        Получить список доступных типов отходов
        """
        waste_types = [
            {'value': type_code, 'label': type_name}
            for type_code, type_name in CollectionPoint.WASTE_TYPES
        ]
        return Response(waste_types)

class TrashReportViewSet(viewsets.ModelViewSet):
    queryset = TrashReport.objects.all()
    serializer_class = TrashReportSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]
    authentication_classes = [JWTAuthentication]

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)
    
    @swagger_auto_schema(
        manual_parameters=trash_report_params,
        responses={200: openapi.Response('Список отчетов о мусоре', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=trash_report_response
        ))}
    )
    def list(self, request, *args, **kwargs):
        """
        Получить список всех отчетов о мусоре с возможностью фильтрации
        """
        return super().list(request, *args, **kwargs)
    
    @swagger_auto_schema(
        request_body=trash_report_request,
        responses={201: openapi.Response('Отчет о мусоре создан', trash_report_response)}
    )
    def create(self, request, *args, **kwargs):
        """
        Создать новый отчет о мусоре
        """
        return super().create(request, *args, **kwargs)
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Отчеты текущего пользователя', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=trash_report_response
        ))}
    )
    @action(detail=False, methods=['get'])
    def my(self, request):
        """
        Получить список отчетов текущего пользователя
        """
        reports = TrashReport.objects.filter(user=request.user)
        serializer = self.get_serializer(reports, many=True)
        return Response(serializer.data)

class TeamViewSet(viewsets.ModelViewSet):
    queryset = Team.objects.all()
    serializer_class = TeamSerializer
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Список всех команд', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=team_response
        ))}
    )
    def list(self, request, *args, **kwargs):
        """
        Получить список всех команд
        """
        return super().list(request, *args, **kwargs)
    
    @swagger_auto_schema(
        request_body=team_request,
        responses={201: openapi.Response('Команда создана', team_response)}
    )
    def create(self, request, *args, **kwargs):
        """
        Создать новую команду
        """
        return super().create(request, *args, **kwargs)
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Участники команды', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=user_response
        ))}
    )
    @action(detail=True, methods=['get'])
    def members(self, request, pk=None):
        """
        Получить список участников команды
        """
        team = self.get_object()
        members = team.members.all()
        serializer = UserSerializer(members, many=True)
        return Response(serializer.data)
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Присоединение к команде', openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'success': openapi.Schema(type=openapi.TYPE_BOOLEAN),
                'message': openapi.Schema(type=openapi.TYPE_STRING),
            }
        ))}
    )
    @action(detail=True, methods=['post'])
    def join(self, request, pk=None):
        """
        Присоединиться к команде
        """
        team = self.get_object()
        team.members.add(request.user)
        return Response({'success': True, 'message': 'Вы присоединились к команде'})
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Выход из команды', openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'success': openapi.Schema(type=openapi.TYPE_BOOLEAN),
                'message': openapi.Schema(type=openapi.TYPE_STRING),
            }
        ))}
    )
    @action(detail=True, methods=['post'])
    def leave(self, request, pk=None):
        """
        Выйти из команды
        """
        team = self.get_object()
        team.members.remove(request.user)
        return Response({'success': True, 'message': 'Вы вышли из команды'})

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
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Список мероприятий по уборке', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=cleanup_event_response
        ))}
    )
    def list(self, request, *args, **kwargs):
        """
        Получить список всех мероприятий по уборке
        """
        return super().list(request, *args, **kwargs)
    
    @swagger_auto_schema(
        request_body=cleanup_event_request,
        responses={201: openapi.Response('Мероприятие создано', cleanup_event_response)}
    )
    def create(self, request, *args, **kwargs):
        """
        Создать новое мероприятие по уборке
        """
        return super().create(request, *args, **kwargs)
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Присоединение к мероприятию', openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'success': openapi.Schema(type=openapi.TYPE_BOOLEAN),
                'message': openapi.Schema(type=openapi.TYPE_STRING),
            }
        ))}
    )
    @action(detail=True, methods=['post'])
    def join(self, request, pk=None):
        """
        Присоединиться к мероприятию по уборке
        """
        event = self.get_object()
        event.participants.add(request.user)
        return Response({'success': True, 'message': 'Вы присоединились к мероприятию'})
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Предстоящие мероприятия', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=cleanup_event_response
        ))}
    )
    @action(detail=False, methods=['get'])
    def upcoming(self, request):
        """
        Получить список предстоящих мероприятий
        """
        events = CleanupEvent.objects.filter(date__gt=timezone.now())
        serializer = self.get_serializer(events, many=True)
        return Response(serializer.data)

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
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Список всех достижений', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=achievement_response
        ))}
    )
    def list(self, request, *args, **kwargs):
        """
        Получить список всех достижений
        """
        return super().list(request, *args, **kwargs)
    
    @swagger_auto_schema(
        request_body=achievement_request,
        responses={201: openapi.Response('Достижение создано', achievement_response)}
    )
    def create(self, request, *args, **kwargs):
        """
        Создать новое достижение
        """
        return super().create(request, *args, **kwargs)

    @swagger_auto_schema(
        responses={200: openapi.Response('Статистика по достижениям', openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'total': openapi.Schema(type=openapi.TYPE_INTEGER),
                'by_type': openapi.Schema(
                    type=openapi.TYPE_ARRAY,
                    items=openapi.Schema(
                        type=openapi.TYPE_OBJECT,
                        properties={
                            'type': openapi.Schema(type=openapi.TYPE_STRING),
                            'count': openapi.Schema(type=openapi.TYPE_INTEGER),
                        }
                    )
                ),
            }
        ))}
    )
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

    @swagger_auto_schema(
        responses={200: openapi.Response('Пользователи с достижением', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=openapi.Schema(
                type=openapi.TYPE_OBJECT,
                properties={
                    'user_id': openapi.Schema(type=openapi.TYPE_INTEGER),
                    'username': openapi.Schema(type=openapi.TYPE_STRING),
                    'date_earned': openapi.Schema(type=openapi.TYPE_STRING, format='date-time'),
                }
            )
        ))}
    )
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

class UserRegisterView(generics.CreateAPIView):
    queryset = User.objects.all()
    serializer_class = UserRegisterSerializer
    permission_classes = [permissions.AllowAny]
    
    @swagger_auto_schema(
        request_body=user_register_request,
        responses={201: openapi.Response('Пользователь создан', user_response)}
    )
    def post(self, request, *args, **kwargs):
        """
        Регистрация нового пользователя
        """
        return super().post(request, *args, **kwargs)

class NewsViewSet(viewsets.ModelViewSet):
    queryset = News.objects.filter(is_published=True)
    serializer_class = NewsSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]
    authentication_classes = [JWTAuthentication]

    def get_permissions(self):
        if self.action in ['create', 'update', 'partial_update', 'destroy']:
            return [permissions.IsAdminUser()]
        return [permissions.AllowAny()]
    
    @swagger_auto_schema(
        responses={200: openapi.Response('Список новостей', openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=news_response
        ))}
    )
    def list(self, request, *args, **kwargs):
        """
        Получить список всех опубликованных новостей
        """
        return super().list(request, *args, **kwargs)
    
    @swagger_auto_schema(
        request_body=news_request,
        responses={201: openapi.Response('Новость создана', news_response)}
    )
    def create(self, request, *args, **kwargs):
        """
        Создать новую новость (только для админов)
        """
        return super().create(request, *args, **kwargs)

def home(request):
    latest_news = News.objects.filter(is_published=True).order_by('-created_at')[:3]
    return render(request, 'core/home.html', {'latest_news': latest_news})

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
        points_list.append(point_data)
    
    # Получить все отчеты о мусоре
    trash_reports = TrashReport.objects.all()
    trash_points = []
    
    print(f"Найдено {trash_reports.count()} отчетов о мусоре") # Отладочный вывод
    
    for report in trash_reports:
        report_data = {
            'latitude': float(report.latitude),
            'longitude': float(report.longitude),
            'address': report.address,
            'description': report.description,
            'status': report.status,
            'id': report.id
        }
        trash_points.append(report_data)
        print(f"Добавлен отчет: {report.address} ({report.latitude}, {report.longitude})") # Отладочный вывод
    
    return render(request, 'core/map.html', {
        'collection_points': json.dumps(points_list),
        'points_list': collection_points,
        'trash_reports': json.dumps(trash_points),
        'google_maps_api_key': settings.GOOGLE_MAPS_API_KEY
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
            # Изменить на 'map' вместо 'map_view'
            return redirect('map')
    else:
        form = TrashReportForm()
    
    return render(request, 'core/report_trash.html', {
        'form': form,
        'google_maps_api_key': settings.GOOGLE_MAPS_API_KEY
    })

@api_view(['POST'])
@permission_classes([permissions.IsAuthenticated])
def report_trash_api(request):
    serializer = TrashReportSerializer(data=request.data)
    if serializer.is_valid():
        serializer.save(user=request.user)
        return Response(serializer.data, status=201)
    return Response(serializer.errors, status=400)

def news_list(request):
    news = News.objects.filter(is_published=True).order_by('-created_at')
    return render(request, 'core/news_list.html', {'news': news})

def news_detail(request, pk):
    news = News.objects.get(pk=pk, is_published=True)
    return render(request, 'core/news_detail.html', {'news': news})
