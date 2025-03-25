package com.tazar.android.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * Создает временный файл для хранения изображения
     * @param context Контекст приложения
     * @return Файл для изображения
     * @throws IOException если не удается создать файл
     */
    public static File createImageFile(Context context) throws IOException {
        // Создаем уникальное имя файла
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        
        // Получаем директорию для хранения файлов
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        
        // Создаем временный файл
        return File.createTempFile(
                imageFileName,  /* префикс */
                ".jpg",         /* суффикс */
                storageDir      /* директория */
        );
    }

    /**
     * Копирует содержимое из Uri в File
     * @param context Контекст приложения
     * @param uri Uri источника
     * @param destFile Файл назначения
     * @return true, если копирование успешно, иначе false
     */
    public static boolean copyUriToFile(Context context, Uri uri, File destFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e(TAG, "Не удалось открыть поток для чтения: " + uri);
                return false;
            }
            
            OutputStream outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[4 * 1024]; // 4KB буфер
            int read;
            
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            
            outputStream.flush();
            
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Ошибка при закрытии выходного потока", e);
            }
            
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Ошибка при закрытии входного потока", e);
            }
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при копировании файла", e);
            return false;
        }
    }
} 