package com.marshl.alembic;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AlchemyPackage implements Parcelable {

    public static final String ALCHEMY_PACKAGE_PARCEL_NAME = "ALCHEMY_PACKAGE";
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AlchemyPackage createFromParcel(Parcel in) {
            return new AlchemyPackage(in);
        }

        public AlchemyPackage[] newArray(int size) {
            return new AlchemyPackage[size];
        }
    };
    public final ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    private String name = "UNDEFINED";
    public AlchemyPackage(Parcel in) {
        super();
        this.name = in.readString();

        Object[] ingredientArray = in.readArray(Ingredient.class.getClassLoader());
        for (Object ingredient : ingredientArray) {
            this.ingredients.add((Ingredient) ingredient);
        }
    }

    public AlchemyPackage(String name) {
        this.name = name;
    }

    public String getPackageName() {
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
