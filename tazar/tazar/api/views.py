from rest_framework import viewsets, filters
from rest_framework.decorators import action
from rest_framework.response import Response
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi
from django_filters import rest_framework as django_filters
from core.models import CollectionPoint
from .serializers import CollectionPointSerializer, CollectionPointCreateSerializer

class CollectionPointFilter(django_filters.FilterSet):
    waste_types = django_filters.CharFilter(
        field_name='waste_types',
        lookup_expr='exact',
        help_text='Фильтр по типу отходов'
    )
    city = django_filters.CharFilter(
        field_name='city',
        lookup_expr='icontains',
        help_text='Фильтр по городу'
    )

    class Meta:
        model = CollectionPoint
        fields = ['waste_types', 'city']

class CollectionPointViewSet(viewsets.ModelViewSet):
    queryset = CollectionPoint.objects.all()
    serializer_class = CollectionPointSerializer
    filterset_class = CollectionPointFilter
    filter_backends = [django_filters.DjangoFilterBackend, filters.SearchFilter]
    search_fields = ['city', 'address']
    permission_classes = []  # Разрешаем анонимный доступ

    def get_serializer_class(self):
        if self.action in ['create', 'update', 'partial_update']:
            return CollectionPointCreateSerializer
        return CollectionPointSerializer

    @swagger_auto_schema(
        manual_parameters=[
            openapi.Parameter(
                'waste_types',
                openapi.IN_QUERY,
                description="""
                Фильтр по типу отходов:
                
                Основные типы:
                - plastic (Пластик)
                - paper (Бумага)
                - metal (Металл)
                - glass (Стекло)
                
                Новые типы:
                - medical (Медицинские отходы)
                - construction (Строительные отходы)
                - agricultural (Сельские отходы)
                """,
                type=openapi.TYPE_STRING,
                required=False,
                enum=['plastic', 'paper', 'metal', 'glass', 'medical', 'construction', 'agricultural']
            ),
            openapi.Parameter(
                'city',
                openapi.IN_QUERY,
                description="Фильтр по городу",
                type=openapi.TYPE_STRING,
                required=False
            )
        ]
    )
    def list(self, request, *args, **kwargs):
        """
        Получить список всех пунктов приема отходов.
        
        Поддерживает фильтрацию по:
        - Типу отходов (waste_types)
        - Городу (city)
        - Поиску по адресу (search)
        
        Примеры:
        - /api/collection-points/?waste_types=medical
        - /api/collection-points/?city=Бишкек
        - /api/collection-points/?search=ул.+Ленина
        """
        return super().list(request, *args, **kwargs)

    @swagger_auto_schema(
        responses={
            200: openapi.Response(
                description="Список доступных типов отходов",
                schema=openapi.Schema(
                    type=openapi.TYPE_ARRAY,
                    items=openapi.Schema(
                        type=openapi.TYPE_OBJECT,
                        properties={
                            'value': openapi.Schema(type=openapi.TYPE_STRING),
                            'label': openapi.Schema(type=openapi.TYPE_STRING),
                        }
                    )
                )
            )
        }
    )
    @action(detail=False, methods=['get'])
    def waste_types(self, request):
        """
        Получить список всех доступных типов отходов
        """
        waste_types = [
            {'value': type_code, 'label': type_name}
            for type_code, type_name in CollectionPoint.WASTE_TYPES
        ]
        return Response(waste_types) 