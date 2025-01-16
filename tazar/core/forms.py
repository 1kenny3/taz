from django import forms
from django.contrib.auth.forms import UserCreationForm
from .models import User, CollectionPoint

class UserRegisterForm(UserCreationForm):
    email = forms.EmailField(required=False)

    class Meta:
        model = User
        fields = ['username', 'email', 'password1', 'password2']

    def clean_email(self):
        return self.cleaned_data.get('email') 

class CollectionPointForm(forms.ModelForm):
    class Meta:
        model = CollectionPoint
        fields = ['city', 'address', 'waste_types', 'latitude', 'longitude'] 