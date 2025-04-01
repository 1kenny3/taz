package com.tazar.android.utils;

import android.content.Context;

import com.tazar.android.TazarApplication;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Интерцептор для добавления токена авторизации в заголовки запросов
 */
public class TokenInterceptor implements Interceptor {
    private final Context context;
    
    /**
     * Конструктор
     * @param context Контекст приложения
     */
    public TokenInterceptor(Context context) {
        this.context = context;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        
        // Получаем экземпляр приложения через контекст
        TazarApplication app = (TazarApplication) context.getApplicationContext();
        String accessToken = app.getPreferenceManager().getAccessToken();
        
        // Если токен существует, добавляем его в заголовок запроса
        if (accessToken != null && !accessToken.isEmpty()) {
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", "Bearer " + accessToken);
            
            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }
        
        // Если токена нет, отправляем запрос без изменений
        return chain.proceed(original);
    }
} 