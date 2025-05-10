from PIL import Image, ImageDraw
import os

# Загружаем оригинальное изображение
input_path = "/Users/kenny/Desktop/taz/ChatGPT%20Image%208%20%D0%BC%D0%B0%D1%8F%202025%20%D0%B3.%2C%2001_46_20-Photoroom.png"
original_image = Image.open(input_path).convert("RGBA")

# Определяем базовый путь для Android проекта
android_res_path = "/Users/kenny/Desktop/taz/TazarAndroid/app/src/main/res"

# Определяем размеры mipmap
sizes = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

def create_circular_mask(size):
    mask = Image.new('L', (size, size), 0)
    draw = ImageDraw.Draw(mask)
    draw.ellipse((0, 0, size, size), fill=255)
    return mask

# Масштабируем изображение для каждого размера и сохраняем
output_paths = {}
for name, size in sizes.items():
    # Создаем директорию, если она не существует
    output_dir = os.path.join(android_res_path, name)
    os.makedirs(output_dir, exist_ok=True)
    
    # Масштабируем изображение
    resized_image = original_image.resize((size, size), Image.LANCZOS)
    
    # Сохраняем обычную версию
    output_path = os.path.join(output_dir, "ic_launcher.png")
    resized_image.save(output_path, format="PNG")
    output_paths[f"{name}/ic_launcher"] = output_path
    
    # Создаем и сохраняем круглую версию
    mask = create_circular_mask(size)
    circular_image = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    circular_image.paste(resized_image, mask=mask)
    
    # Сохраняем как round и foreground (они одинаковые в нашем случае)
    round_output_path = os.path.join(output_dir, "ic_launcher_round.png")
    circular_image.save(round_output_path, format="PNG")
    output_paths[f"{name}/ic_launcher_round"] = round_output_path
    
    foreground_output_path = os.path.join(output_dir, "ic_launcher_foreground.png")
    circular_image.save(foreground_output_path, format="PNG")
    output_paths[f"{name}/ic_launcher_foreground"] = foreground_output_path

print("Иконки успешно сгенерированы в следующих путях:")
for name, path in output_paths.items():
    print(f"{name}: {path}")
