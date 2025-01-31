from django import template

register = template.Library()

@register.filter(name='filter_achievement')
def filter_achievement(user_achievements, achievement):
    return user_achievements.filter(achievement=achievement).exists()

@register.filter(name='add_class')
def add_class(field, css_class):
    return field.as_widget(attrs={'class': css_class}) 