package com.tazar.android.utils;

import android.content.Context;

import com.tazar.android.EcoupApplication;
import com.tazar.android.api.services.AuthService;
import com.tazar.android.config.ApiConfig;
import com.tazar.android.models.auth.RefreshTokenRequest;
import com.tazar.android.models.auth.TokenResponse;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Аутентификатор для обновления токена при получении ошибки 401
 */
public class TokenAuthenticator implements Authenticator {
    private final Context context;
    
    /**
     * Конструктор
     * @param context Контекст приложения
     */
    public TokenAuthenticator(Context context) {
        this.context = context;
    }
    
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        // Получаем экземпляр приложения через контекст
        EcoupApplication app = (EcoupApplication) context.getApplicationContext();
        PreferenceManager preferenceManager = app.getPreferenceManager();

        String refreshToken = preferenceManager.getRefreshToken();
        if (refreshToken == null) {
            preferenceManager.logout();
            return null;
        }
        
        // Если уже пытались обновить токен, чтобы избежать бесконечного цикла
        if (response.request().header("Authorization") != null && 
            response.request().header("Authorization").contains("Bearer") && 
            response.code() == 401) {
            // Выход из аккаунта, т.к. токен не удалось обновить
            preferenceManager.logout();
            return null;
        }
        
        try {
            // Создаем отдельный клиент для запроса обновления токена
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();
            
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
            AuthService authService = retrofit.create(AuthService.class);
            
            // Выполняем запрос на обновление токена
            RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);
            retrofit2.Response<TokenResponse> tokenResponse = authService.refreshToken(refreshRequest).execute();
            
            if (tokenResponse.isSuccessful() && tokenResponse.body() != null) {
                TokenResponse tokens = tokenResponse.body();
                
                // Сохраняем новые токены
                preferenceManager.saveTokens(
                        tokens.getAccessToken(),
                        tokens.getRefreshToken()
                );
                
                // Повторяем исходный запрос с новым токеном
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + tokens.getAccessToken())
                        .build();
            } else {
                // Выход из аккаунта при неудаче
                preferenceManager.logout();
                return null;
            }
        } catch (Exception e) {
            // Выход из аккаунта при ошибке
            preferenceManager.logout();
            return null;
        }
    }
} 