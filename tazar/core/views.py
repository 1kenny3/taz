from django.shortcuts import render, redirect
from django.contrib.auth import login, authenticate, logout
from .models import User, CollectionPoint
from .forms import UserRegisterForm, CollectionPointForm
from rest_framework import viewsets
from .serializers import UserSerializer, CollectionPointSerializer
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from django.core import serializers
import json
from django.conf import settings

class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer

class CollectionPointViewSet(viewsets.ModelViewSet):
    queryset = CollectionPoint.objects.all()
    serializer_class = CollectionPointSerializer

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
        username = request.POST['username']
        password = request.POST['password']
        user = authenticate(request, username=username, password=password)
        if user is not None:
            login(request, user)
            return redirect('home')
        else:
            messages.error(request, 'Неверное имя пользователя или пароль')
    return render(request, 'core/login.html')

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
    return render(request, 'core/dashboard.html')

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
