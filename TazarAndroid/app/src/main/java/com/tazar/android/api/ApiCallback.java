package com.tazar.android.api;

/**
 * Интерфейс для обратных вызовов API
 * @param <T> Тип возвращаемых данных
 */
public interface ApiCallback<T> {
    void onSuccess(T result);
    void onError(String message);
} 