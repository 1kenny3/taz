package com.tazar.android.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Утилитарный класс для работы с URL API
 */
public class ApiUrlUtil {
    /**
     * Генерирует URL API для доступа из разных устройств
     * @param context Контекст приложения
     * @param port Порт API сервера
     * @return URL для доступа к API серверу
     */
    public static String generateApiUrl(Context context, int port) {
        // Для эмулятора Android используем специальный IP, соответствующий localhost компьютера
        if (isEmulator()) {
            return "http://10.0.2.2:" + port + "/";
        }
        
        // Для реального устройства пытаемся получить IP устройства
        String deviceIp = getLocalIpAddress();
        if (deviceIp != null) {
            return "http://" + deviceIp + ":" + port + "/";
        }
        
        // Если не удалось определить IP, используем localhost
        return "http://127.0.0.1:" + port + "/";
    }
    
    /**
     * Проверяет, запущено ли приложение в эмуляторе
     * @return true, если приложение запущено в эмуляторе
     */
    public static boolean isEmulator() {
        return android.os.Build.MODEL.contains("sdk") 
                || android.os.Build.MODEL.contains("Emulator") 
                || android.os.Build.MODEL.contains("Android SDK");
    }
    
    /**
     * Получает локальный IP-адрес устройства
     * @return IP-адрес устройства или null, если не удалось определить
     */
    public static String getLocalIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        if (sAddr.indexOf(':') < 0) { // Фильтруем IPv6-адреса
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            // Игнорируем ошибки
        }
        return null;
    }
    
    /**
     * Получает URL API сервера с учетом настроек и текущей среды
     * @param context Контекст приложения
     * @return URL API сервера
     */
    public static String getApiUrl(Context context) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        
        // Проверяем, задан ли кастомный URL
        String customUrl = preferenceManager.getApiUrl();
        if (customUrl != null && !customUrl.isEmpty()) {
            return customUrl;
        }
        
        // Если нет, генерируем URL на основе окружения
        return generateApiUrl(context, 8000);
    }
    
    /**
     * Сохраняет URL API сервера в настройках
     * @param context Контекст приложения
     * @param apiUrl URL API сервера
     */
    public static void saveApiUrl(Context context, String apiUrl) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.setApiUrl(apiUrl);
    }
} 