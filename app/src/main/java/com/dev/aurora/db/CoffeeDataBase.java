package com.dev.aurora.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SearchItem.class}, version = 1, exportSchema = false)
public abstract class CoffeeDataBase extends RoomDatabase {
    private static CoffeeDataBase INSTANCE;

    public static synchronized CoffeeDataBase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CoffeeDataBase.class, "coffeeDB")
                    .build();
        }

        return INSTANCE;
    }

    public abstract SearchDao getSearchDao();
}
