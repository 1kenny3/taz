from django import forms
from django.contrib.auth.forms import UserCreationForm
from .models import User, CollectionPoint, TrashReport

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

class LoginForm(forms.Form):
    username = forms.CharField(
        label='Имя пользователя',
        widget=forms.TextInput(attrs={
            'placeholder': 'Введите имя пользователя'
        })
    )
    password = forms.CharField(
        label='Пароль',
        widget=forms.PasswordInput(attrs={
            'placeholder': 'Введите пароль'
        })
    ) 

class TrashReportForm(forms.ModelForm):
    class Meta:
        model = TrashReport
        fields = ['address', 'description', 'photo', 'latitude', 'longitude']
        widgets = {
            'latitude': forms.HiddenInput(),
            'longitude': forms.HiddenInput(),
        } 

class UserProfileForm(forms.ModelForm):
    class Meta:
        model = User
        fields = ['profile_photo', 'bio', 'email']
        widgets = {
            'bio': forms.Textarea(attrs={
                'rows': 3,
                'class': 'w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-green-500 resize-none',
                'placeholder': 'Расскажите о себе...'
            }),
            'email': forms.EmailInput(attrs={
                'class': 'w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-green-500',
                'placeholder': 'Ваш email'
            })
        } 