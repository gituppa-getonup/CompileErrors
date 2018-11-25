package com.portablecollections.portablecollections;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "collectables")
public class Collectable implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    private String name;
    private String description;
    private String country;
    private String city;
    private String imageUri;
    private boolean wantIt;
    private boolean gotIt;
    private int number;

    public Collectable() {
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        out.writeString(description);
        out.writeString(country);
        out.writeString(city);
        out.writeString(imageUri);
        out.writeInt(wantIt ? 1 : 0);
        out.writeInt(gotIt ? 1 : 0);
        out.writeInt(number);
    }

    public static final Parcelable.Creator<Collectable> CREATOR
            = new Parcelable.Creator<Collectable>() {
        public Collectable createFromParcel(Parcel in) {
            return new Collectable(in);
        }
        public Collectable[] newArray(int size) {
            return new Collectable[size];
        }
    };

    private Collectable(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
        country = in.readString();
        city = in.readString();
        imageUri = in.readString();
        wantIt = in.readInt() == 1;
        gotIt = in.readInt() == 1;
        number = in.readInt();
    }


    public static Collectable fromContentValues(ContentValues contentValues) {
        Collectable collectable = new Collectable();
        if(contentValues.containsKey("id")) {
            collectable.setId(contentValues.getAsLong("id"));
        }
        if(contentValues.containsKey("name")) {
            collectable.setName(contentValues.getAsString("name"));
        }
        if(contentValues.containsKey("description")) {
            collectable.setDescription(contentValues.getAsString("description"));
        }
        if(contentValues.containsKey("country")) {
            collectable.setCountry(contentValues.getAsString("country"));
        }
        if(contentValues.containsKey("city")) {
            collectable.setCity(contentValues.getAsString("city"));
        }
        if(contentValues.containsKey("imageUri")) {
            collectable.setImageUri(contentValues.getAsString("imageUri"));
        }
        if(contentValues.containsKey("wantIt")) {
            collectable.setWantIt(contentValues.getAsBoolean("wantIt"));
        }
        if(contentValues.containsKey("gotIt")) {
            collectable.setGotIt(contentValues.getAsBoolean("gotIt"));
        }
        if(contentValues.containsKey("number")) {
            collectable.setNumber(contentValues.getAsInteger("number"));
        }
        return collectable;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getImageUri() { return imageUri; }

    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public boolean getWantIt() { return  wantIt; }

    public void setWantIt(boolean wantIt) { this.wantIt = wantIt; }

    public boolean getGotIt() { return gotIt; }

    public void setGotIt(boolean gotIt) { this.gotIt = gotIt; }

    public int getNumber() { return number; }

    public void setNumber(int number) { this.number = number; }

}
