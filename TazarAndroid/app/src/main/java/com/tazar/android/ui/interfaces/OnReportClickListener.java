package com.tazar.android.ui.interfaces;

import com.tazar.android.models.TrashReport;

/**
 * Интерфейс для обработки нажатий на элементы списка отчетов
 */
public interface OnReportClickListener {
    /**
     * Вызывается при нажатии на элемент списка
     * @param report Отчет, на который нажали
     */
    void onReportClick(TrashReport report);
} 