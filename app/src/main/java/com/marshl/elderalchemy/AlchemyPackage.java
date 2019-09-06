package com.marshl.elderalchemy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AlchemyPackage implements Parcelable {
    
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AlchemyPackage createFromParcel(Parcel in) {
            return new AlchemyPackage(in);
        }

        public AlchemyPackage[] newArray(int size) {
            return new AlchemyPackage[size];
        }
    };
    final ArrayList<Ingredient> ingredients = new ArrayList<>();
    private final String name;

    private AlchemyPackage(Parcel in) {
        super();
        this.name = in.readString();

        Object[] ingredientArray = in.readArray(Ingredient.class.getClassLoader());
        for (Object ingredient : ingredientArray) {
            this.ingredients.add((Ingredient) ingredient);
        }
    }

    AlchemyPackage(String name) {
        this.name = name;
    }

    String getPackageName() {
        return this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeArray(this.ingredients.toArray());
    }
}
