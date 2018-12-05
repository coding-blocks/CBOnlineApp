package com.codingblocks.cbonlineapp.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;

public class AnnouncementConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Announcement> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Announcement>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<Announcement> someObjects) {
        return gson.toJson(someObjects);
    }
}