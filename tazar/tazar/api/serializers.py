from rest_framework import serializers
from core.models import CollectionPoint, TrashReport

class CollectionPointSerializer(serializers.ModelSerializer):
    waste_type_display = serializers.SerializerMethodField()

    class Meta:
        model = CollectionPoint
        fields = ['id', 'city', 'address', 'latitude', 'longitude', 
                 'waste_types', 'waste_type_display']

    def get_waste_type_display(self, obj):
        return dict(CollectionPoint.WASTE_TYPES).get(obj.waste_types, '')

class CollectionPointCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = CollectionPoint
        fields = ['city', 'address', 'latitude', 'longitude', 'waste_types']
        extra_kwargs = {
            'waste_types': {
                'help_text': """
                Доступные типы отходов:
                - plastic (Пластик)
                - paper (Бумага)
                - metal (Металл)
                - glass (Стекло)
                - medical (Медицинские отходы)
                - construction (Строительные отходы)
                - agricultural (Сельские отходы)
                """
            }
        } 