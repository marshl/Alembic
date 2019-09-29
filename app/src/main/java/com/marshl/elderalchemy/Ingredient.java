package com.marshl.elderalchemy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Ingredient implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
    private final String name;
    private final String image;
    private final int value;
    private final float weight;
    private ArrayList<IngredientEffect> effects = new ArrayList<IngredientEffect>();
    private boolean selected = false;

    Ingredient(String name, String imageName, int value, float weight, ArrayList<IngredientEffect> effects) {
        this.name = name;
        this.effects = effects;
        this.image = imageName;
        this.value = value;
        this.weight = weight;
    }

    private Ingredient(Parcel in) {
        super();
        this.name = in.readString();
        this.image = in.readString();
        this.selected = in.readInt() == 1;
        this.value = in.readInt();
        this.weight = in.readFloat();
        in.readTypedList(this.effects, IngredientEffect.CREATOR);
    }

    boolean isSelected() {
        return selected;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    String getImage() {
        return image;
    }

    public String getName() {
        return this.name;
    }

    public float getValue() {
        return this.value;
    }

    public float getWeight() {
        return this.weight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.image);
        parcel.writeInt(this.selected ? 1 : 0);
        parcel.writeInt(this.value);
        parcel.writeFloat(this.weight);
        parcel.writeTypedList(this.effects);
    }

    List<IngredientEffect> getFirstEffects(int effectCount) {
        if (effectCount == 0) {

            return this.effects;
//            return Arrays.asList(this.effects);
        }

        return this.effects.subList(0, Math.min(effectCount, this.effects.size()));
//        return Arrays.asList(this.effects).subList(0, Math.min(effectCount, this.effects.length));
    }
}

