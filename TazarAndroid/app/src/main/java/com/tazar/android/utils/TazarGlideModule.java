package com.tazar.android.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader;
import com.bumptech.glide.load.engine.executor.GlideExecutor;

import java.io.InputStream;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

@GlideModule
public final class TazarGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Настройка исполнителей с увеличенным количеством потоков
        builder.setSourceExecutor(
            GlideExecutor.newSourceBuilder()
                .setThreadCount(4)
                .setThreadTimeoutMillis(30000)
                .build()
        );
        
        builder.setDiskCacheExecutor(
            GlideExecutor.newDiskCacheBuilder()
                .setThreadCount(4)
                .setThreadTimeoutMillis(30000)
                .build()
        );

        // Настройка размера кэша
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
            .setBitmapPoolScreens(2)
            .setMemoryCacheScreens(2)
            .build();

        // Настройка кэша в памяти
        builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));
        builder.setBitmapPool(new LruBitmapPool(calculator.getBitmapPoolSize()));

        // Настройка кэша на диске (100 МБ)
        int diskCacheSizeBytes = 1024 * 1024 * 100;
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));

        // Настройки по умолчанию
        builder.setDefaultRequestOptions(
            new RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .timeout(30000)
        );
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // Настраиваем OkHttp клиент
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

        // Регистрируем OkHttp для загрузки изображений
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
} 