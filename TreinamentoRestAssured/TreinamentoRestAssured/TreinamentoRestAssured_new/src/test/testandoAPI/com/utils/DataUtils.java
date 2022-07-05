package com.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataUtils {
    public static String getDataDiferencaDeDias(Integer qtdDias){
        Calendar calendar = Calendar.getInstance();
        //Adiciona os dias passadaos para o parametro na data atual. se for <0, vai subtrair.
        calendar.add(Calendar.DAY_OF_MONTH, qtdDias);
        return getDataFormatada(calendar.getTime());
    }

    public static String getDataFormatada(Date data){
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(data);
    }
}
