from django import template

register = template.Library()

@register.filter(name='add_class')
def add_class(field, css_class):
    return field.as_widget(attrs={
        "class": f"{css_class} {field.css_classes() if hasattr(field, 'css_classes') else ''}"
    }) 