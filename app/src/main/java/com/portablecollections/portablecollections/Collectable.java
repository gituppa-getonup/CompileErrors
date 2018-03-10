package com.portablecollections.portablecollections;

// POJO

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;

import java.io.Serializable;
@Entity(tableName = "collectables")
public class Collectable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public static int id;

    public static String name;
    public static String description;
    public static String country;
    public static String city;

    public Collectable() {
    }

    public static Collectable fromContentValues(ContentValues contentValues) {
        final Collectable collectable = new Collectable();
        if(contentValues.containsKey(String.valueOf(id))) {
            collectable.setId(contentValues.getAsInteger(String.valueOf(id)));
        }
        if(contentValues.containsKey(name)) {
            collectable.setName(contentValues.getAsString(name));
        }
        if(contentValues.containsKey(description)) {
            collectable.setDescription(contentValues.getAsString(description));
        }
        if(contentValues.containsKey(country)) {
            collectable.setCountry(contentValues.getAsString(country));
        }
        if(contentValues.containsKey(city)) {
            collectable.setCity(contentValues.getAsString(city));
        }
        return collectable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
