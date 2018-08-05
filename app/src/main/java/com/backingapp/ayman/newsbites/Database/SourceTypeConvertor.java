package com.backingapp.ayman.newsbites.Database;

import android.arch.persistence.room.TypeConverter;

import com.backingapp.ayman.newsbites.Models.Source;

public class SourceTypeConvertor {

    @TypeConverter
    public static String fromSource(Source source) {
        return source.getId() + "," + source.getName();
    }

    @TypeConverter
    public static Source toSource(String sourceString) {
        String[] sourceDetails = sourceString.split(",");
        return new Source(sourceDetails[0], sourceDetails[1]);
    }

}
