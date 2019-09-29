package com.marshl.elderalchemy;

import android.os.Parcel;
import android.os.Parcelable;

public class IngredientEffect implements Parcelable {
    public static final Creator<IngredientEffect> CREATOR = new Creator<IngredientEffect>() {
        public IngredientEffect createFromParcel(Parcel in) {
            return new IngredientEffect(in);
        }

        public IngredientEffect[] newArray(int size) {
            return new IngredientEffect[size];
        }
    };
    private final String name;
    private float valueMultiplier;
    private float magnitudeMultiplier;

    IngredientEffect(String name) {
        this.name = name;
        this.valueMultiplier = 0;
        this.magnitudeMultiplier = 0;
    }

    private IngredientEffect(Parcel in) {
        super();
        this.name = in.readString();
        this.valueMultiplier = in.readFloat();
        this.magnitudeMultiplier = in.readFloat();
    }

    public String getName() {
        return this.name;
    }

    public void setValueMultiplier(float multiplier) {
        this.valueMultiplier = multiplier;
    }

    public void setMagnitudeMultiplier(float multiplier) {
        this.magnitudeMultiplier = multiplier;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeFloat(this.valueMultiplier);
        parcel.writeFloat(this.magnitudeMultiplier);
    }
}

