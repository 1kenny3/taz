from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema

# Схемы для запросов
trash_report_request = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    required=['address', 'description', 'photo', 'latitude', 'longitude'],
    properties={
        'address': openapi.Schema(type=openapi.TYPE_STRING, description='Адрес места с мусором'),
        'description': openapi.Schema(type=openapi.TYPE_STRING, description='Описание проблемы'),
        'photo': openapi.Schema(type=openapi.TYPE_FILE, description='Фотография мусора'),
        'latitude': openapi.Schema(type=openapi.TYPE_NUMBER, description='Широта'),
        'longitude': openapi.Schema(type=openapi.TYPE_NUMBER, description='Долгота'),
    }
)

collection_point_request = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    required=['waste_types', 'city', 'address', 'latitude', 'longitude'],
    properties={
        'waste_types': openapi.Schema(
            type=openapi.TYPE_STRING, 
            description='Тип принимаемых отходов',
            enum=['plastic', 'paper', 'metal', 'glass', 'medical', 'construction', 'agricultural'],
        ),
        'city': openapi.Schema(type=openapi.TYPE_STRING, description='Город'),
        'address': openapi.Schema(type=openapi.TYPE_STRING, description='Адрес пункта приема'),
        'latitude': openapi.Schema(type=openapi.TYPE_NUMBER, description='Широта'),
        'longitude': openapi.Schema(type=openapi.TYPE_NUMBER, description='Долгота'),
    }
)

user_register_request = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    required=['username', 'email', 'password', 'password2'],
    properties={
        'username': openapi.Schema(type=openapi.TYPE_STRING, description='Имя пользователя'),
        'email': openapi.Schema(type=openapi.TYPE_STRING, format='email', description='Email'),
        'password': openapi.Schema(type=openapi.TYPE_STRING, format='password', description='Пароль'),
        'password2': openapi.Schema(type=openapi.TYPE_STRING, format='password', description='Подтверждение пароля'),
    }
)

team_request = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    required=['name', 'description'],
    properties={
        'name': openapi.Schema(type=openapi.TYPE_STRING, description='Название команды'),
        'description': openapi.Schema(type=openapi.TYPE_STRING, description='Описание команды'),
    }
)

achievement_request = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    required=['name', 'description', 'icon', 'points_required', 'type'],
    properties={
        'name': openapi.Schema(type=openapi.TYPE_STRING, description='Название достижения'),
        'description': openapi.Schema(type=openapi.TYPE_STRING, description='Описание достижения'),
        'icon': openapi.Schema(type=openapi.TYPE_STRING, description='Иконка достижения'),
        'points_required': openapi.Schema(type=openapi.TYPE_INTEGER, description='Требуемые баллы'),
        'type': openapi.Schema(
            type=openapi.TYPE_STRING, 
            description='Тип достижения',
            enum=['reports', 'points', 'activity'],
        ),
    }
)

cleanup_event_request = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    required=['title', 'description', 'location', 'date', 'max_participants'],
    properties={
        'title': openapi.Schema(type=openapi.TYPE_STRING, description='Название мероприятия'),
        'description': openapi.Schema(type=openapi.TYPE_STRING, description='Описание мероприятия'),
        'location': openapi.Schema(type=openapi.TYPE_STRING, description='Место проведения'),
        'date': openapi.Schema(type=openapi.TYPE_STRING, format='date-time', description='Дата и время'),
        'max_participants': openapi.Schema(type=openapi.TYPE_INTEGER, description='Максимум участников'),
    }
)

news_request = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    required=['title', 'content'],
    properties={
        'title': openapi.Schema(type=openapi.TYPE_STRING, description='Заголовок новости'),
        'content': openapi.Schema(type=openapi.TYPE_STRING, description='Содержание новости'),
        'image': openapi.Schema(type=openapi.TYPE_FILE, description='Изображение для новости'),
        'is_published': openapi.Schema(type=openapi.TYPE_BOOLEAN, description='Статус публикации'),
    }
)

# Схемы для ответов
user_response = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    properties={
        'id': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID пользователя'),
        'username': openapi.Schema(type=openapi.TYPE_STRING, description='Имя пользователя'),
        'email': openapi.Schema(type=openapi.TYPE_STRING, description='Email'),
        'is_collector': openapi.Schema(type=openapi.TYPE_BOOLEAN, description='Является ли сборщиком'),
        'points': openapi.Schema(type=openapi.TYPE_INTEGER, description='Баллы пользователя'),
        'profile_photo': openapi.Schema(type=openapi.TYPE_STRING, description='URL фото профиля'),
        'bio': openapi.Schema(type=openapi.TYPE_STRING, description='О пользователе'),
    }
)

collection_point_response = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    properties={
        'id': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID пункта приема'),
        'waste_types': openapi.Schema(type=openapi.TYPE_STRING, description='Тип принимаемых отходов'),
        'waste_type_display': openapi.Schema(type=openapi.TYPE_STRING, description='Отображаемое название типа отходов'),
        'city': openapi.Schema(type=openapi.TYPE_STRING, description='Город'),
        'address': openapi.Schema(type=openapi.TYPE_STRING, description='Адрес пункта приема'),
        'latitude': openapi.Schema(type=openapi.TYPE_NUMBER, description='Широта'),
        'longitude': openapi.Schema(type=openapi.TYPE_NUMBER, description='Долгота'),
    }
)

trash_report_response = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    properties={
        'id': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID отчета'),
        'user': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID пользователя'),
        'address': openapi.Schema(type=openapi.TYPE_STRING, description='Адрес места с мусором'),
        'description': openapi.Schema(type=openapi.TYPE_STRING, description='Описание проблемы'),
        'photo': openapi.Schema(type=openapi.TYPE_STRING, description='URL фотографии мусора'),
        'latitude': openapi.Schema(type=openapi.TYPE_NUMBER, description='Широта'),
        'longitude': openapi.Schema(type=openapi.TYPE_NUMBER, description='Долгота'),
        'status': openapi.Schema(
            type=openapi.TYPE_STRING, 
            description='Статус отчета',
            enum=['new', 'in_progress', 'completed', 'rejected'],
        ),
        'points_awarded': openapi.Schema(type=openapi.TYPE_BOOLEAN, description='Начислены ли баллы'),
        'created_at': openapi.Schema(type=openapi.TYPE_STRING, format='date-time', description='Дата создания'),
        'updated_at': openapi.Schema(type=openapi.TYPE_STRING, format='date-time', description='Дата обновления'),
    }
)

achievement_response = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    properties={
        'id': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID достижения'),
        'name': openapi.Schema(type=openapi.TYPE_STRING, description='Название достижения'),
        'description': openapi.Schema(type=openapi.TYPE_STRING, description='Описание достижения'),
        'icon': openapi.Schema(type=openapi.TYPE_STRING, description='Иконка достижения'),
        'points_required': openapi.Schema(type=openapi.TYPE_INTEGER, description='Требуемые баллы'),
        'type': openapi.Schema(type=openapi.TYPE_STRING, description='Тип достижения'),
        'created_at': openapi.Schema(type=openapi.TYPE_STRING, format='date-time', description='Дата создания'),
    }
)

team_response = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    properties={
        'id': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID команды'),
        'name': openapi.Schema(type=openapi.TYPE_STRING, description='Название команды'),
        'description': openapi.Schema(type=openapi.TYPE_STRING, description='Описание команды'),
        'leader': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID лидера команды'),
        'total_points': openapi.Schema(type=openapi.TYPE_INTEGER, description='Общие баллы команды'),
        'created_at': openapi.Schema(type=openapi.TYPE_STRING, format='date-time', description='Дата создания'),
    }
)

cleanup_event_response = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    properties={
        'id': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID мероприятия'),
        'organizer': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID организатора'),
        'title': openapi.Schema(type=openapi.TYPE_STRING, description='Название мероприятия'),
        'description': openapi.Schema(type=openapi.TYPE_STRING, description='Описание мероприятия'),
        'location': openapi.Schema(type=openapi.TYPE_STRING, description='Место проведения'),
        'date': openapi.Schema(type=openapi.TYPE_STRING, format='date-time', description='Дата и время'),
        'max_participants': openapi.Schema(type=openapi.TYPE_INTEGER, description='Максимум участников'),
        'participants': openapi.Schema(
            type=openapi.TYPE_ARRAY,
            items=openapi.Schema(type=openapi.TYPE_INTEGER),
            description='Список ID участников'
        ),
    }
)

news_response = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    properties={
        'id': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID новости'),
        'title': openapi.Schema(type=openapi.TYPE_STRING, description='Заголовок новости'),
        'content': openapi.Schema(type=openapi.TYPE_STRING, description='Содержание новости'),
        'image': openapi.Schema(type=openapi.TYPE_STRING, description='URL изображения'),
        'created_at': openapi.Schema(type=openapi.TYPE_STRING, format='date-time', description='Дата создания'),
        'updated_at': openapi.Schema(type=openapi.TYPE_STRING, format='date-time', description='Дата обновления'),
        'is_published': openapi.Schema(type=openapi.TYPE_BOOLEAN, description='Статус публикации'),
    }
)

# Параметры запросов
collection_point_params = [
    openapi.Parameter(
        'waste_types',
        openapi.IN_QUERY,
        description='Фильтр по типу отходов (например: plastic, paper, medical)',
        type=openapi.TYPE_STRING,
        required=False,
    ),
    openapi.Parameter(
        'city',
        openapi.IN_QUERY,
        description='Фильтр по городу',
        type=openapi.TYPE_STRING,
        required=False,
    ),
]

trash_report_params = [
    openapi.Parameter(
        'status',
        openapi.IN_QUERY,
        description='Фильтр по статусу отчета (например: new, in_progress, completed)',
        type=openapi.TYPE_STRING,
        required=False,
    ),
    openapi.Parameter(
        'date_from',
        openapi.IN_QUERY,
        description='Фильтр по дате начала (формат: YYYY-MM-DD)',
        type=openapi.TYPE_STRING,
        required=False,
    ),
    openapi.Parameter(
        'date_to',
        openapi.IN_QUERY,
        description='Фильтр по дате окончания (формат: YYYY-MM-DD)',
        type=openapi.TYPE_STRING,
        required=False,
    ),
]

# Примеры использования для документации
"""
@swagger_auto_schema(
    method='get',
    manual_parameters=collection_point_params,
    responses={200: openapi.Response('Список пунктов приема', collection_point_response)}
)
@swagger_auto_schema(
    method='post',
    request_body=collection_point_request,
    responses={201: openapi.Response('Пункт приема создан', collection_point_response)}
)
@api_view(['GET', 'POST'])
def collection_points_api(request):
    # Реализация
    pass

@swagger_auto_schema(
    method='get',
    manual_parameters=trash_report_params,
    responses={200: openapi.Response('Список отчетов о мусоре', trash_report_response)}
)
@swagger_auto_schema(
    method='post',
    request_body=trash_report_request,
    responses={201: openapi.Response('Отчет о мусоре создан', trash_report_response)}
)
@api_view(['GET', 'POST'])
def trash_reports_api(request):
    # Реализация
    pass
""" 