package com.portablecollections.portablecollections;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Collectable.class}, version = 1)
public abstract class CollectableDatabase extends RoomDatabase {

    public abstract CollectableDao collectableDao();
    private static CollectableDatabase collectableDatabase;

    static CollectableDatabase getInstance(Context context) {
        if(collectableDatabase == null) {
            collectableDatabase = Room.databaseBuilder(context.getApplicationContext(), CollectableDatabase.class, "collectables")
                    .build();
        }
        return collectableDatabase;
    }
}