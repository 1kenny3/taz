# TAZAR - Платформа для решения экологических проблем 🌍

## О проекте

TAZAR - это веб-платформа, которая помогает решать экологические проблемы через:
- Отслеживание мест скопления мусора 🗑️
- Организацию пунктов сбора отходов ♻️
- Геймификацию эко-активности 🎮

## Основной функционал

### 1. Отчеты о мусоре
- Создание отчетов с фото и геолокацией
- Отслеживание статуса уборки
- Начисление баллов за активность

### 2. Пункты сбора
- Интерактивная карта пунктов приема
- Фильтрация по типам отходов
- Добавление новых точек сбора

### 3. Система достижений
- Автоматическое начисление достижений
- Разные типы наград (за отчеты, баллы, активность)
- Отслеживание прогресса

## API Документация

### Swagger UI
Документация API доступна по адресу:
```
/swagger/
/redoc/
```

### Основные эндпоинты

#### Достижения
```
GET /api/achievements/ - Список всех достижений
POST /api/achievements/ - Создание нового достижения
GET /api/achievements/{id}/ - Детали достижения
PUT /api/achievements/{id}/ - Обновление достижения
DELETE /api/achievements/{id}/ - Удаление достижения
GET /api/achievements/stats/ - Статистика по достижениям
GET /api/achievements/{id}/users/ - Пользователи с достижением
```

#### Пользовательские достижения
```
GET /api/user-achievements/ - Достижения текущего пользователя
POST /api/user-achievements/ - Присвоение достижения
```

## Установка и запуск

1. Клонируйте репозиторий:
```bash
git clone https://github.com/your-username/tazar.git
cd tazar
```

2. Создайте виртуальное окружение:
```bash
python -m venv venv
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate  # Windows
```

3. Установите зависимости:
```bash
pip install -r requirements.txt
```

4. Примените миграции:
```bash
python manage.py migrate
```

5. Создайте суперпользователя:
```bash
python manage.py createsuperuser
```

6. Запустите сервер:
```bash
python manage.py runserver
```

## Технологии

- Django 4.2
- Django REST Framework
- PostgreSQL
- Swagger/OpenAPI
- JWT Authentication
- Leaflet Maps

## Тестирование

1. Запустите тесты:
```bash
python manage.py test
```

2. Проверьте API через Swagger UI:
```
http://localhost:8000/swagger/
```

