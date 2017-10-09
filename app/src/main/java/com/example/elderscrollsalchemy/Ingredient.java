package com.example.elderscrollsalchemy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;


public class Ingredient implements Parcelable {
    public static final String INGREDIENT_PARCEL_NAME = "PARAMS";
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public List<String> effects;
    public AlchemyPackage parentPackage;
    public int imageID;
    public boolean selected = false;
    private String name;

    public Ingredient(String name, List<String> effects) {
        this.name = name;
        this.effects = effects;
    }

    public Ingredient(Parcel in) {
        super();
        this.name = in.readString();
        String[] tempEffects = new String[in.readInt()];
        in.readStringArray(tempEffects);
        this.effects.addAll(Arrays.asList(tempEffects));
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeInt(this.effects.size());
        parcel.writeStringArray(this.effects.toArray(new String[0]));
    }
}

