package com.marshl.alembic;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlchemyGame implements Parcelable {

    public static final String ALCHEMY_GAME_PARCEL_NAME = "ALCHEMY_GAME";
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AlchemyGame createFromParcel(Parcel in) {
            return new AlchemyGame(in);
        }

        public AlchemyGame[] newArray(int size) {
            return new AlchemyGame[size];
        }
    };

    public final ArrayList<AlchemyPackage> packages = new ArrayList<AlchemyPackage>();
    public Map<String, List<Ingredient>> effectToIngredientMap = new HashMap<String, List<Ingredient>>();
    public List<String> effectList = new ArrayList<String>();
    public final Map<String, AlchemyEffect> effects = new HashMap<>();
    private final String name;
    private final String prefix;

    private AlchemyGame(Parcel in) {
        super();
        this.name = in.readString();
        this.prefix = in.readString();

        Object[] packageArray = in.readArray(AlchemyPackage.class.getClassLoader());

        for (Object pkg : packageArray) {
            this.packages.add((AlchemyPackage) pkg);
        }

        Object[] effectArray = in.readArray(AlchemyEffect.class.getClassLoader());
        for (Object obj : effectArray) {
            AlchemyEffect effect = (AlchemyEffect) obj;
            this.effects.put(effect.getCode(), effect);
        }
    }

    public AlchemyGame(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.prefix);
        parcel.writeArray(this.packages.toArray());
        parcel.writeArray(this.effects.values().toArray());
    }

    public String getGameName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void recalculateIngredientEffects() {
        this.effectToIngredientMap = new HashMap<>();
        this.effectList = new ArrayList<>();

        for (AlchemyPackage alchemyPackage : this.packages) {
            for (Ingredient ingredient : alchemyPackage.ingredients) {
                if (!ingredient.isSelected()) {
                    continue;
                }

                for (String effect : ingredient.effectCodes) {
                    if (this.effectToIngredientMap.get(effect) == null) {
                        this.effectToIngredientMap.put(effect, new ArrayList<Ingredient>());
                        this.effectList.add(effect);
                    }
                    this.effectToIngredientMap.get(effect).add(ingredient);
                }
            }
        }

        for (int i = 0; i < effectList.size(); ) {
            String effect = this.effectList.get(i);
            if (this.effectToIngredientMap.get(effect).size() < 2) {
                this.effectToIngredientMap.remove(effect);
                this.effectList.remove(i);
            } else {
                ++i;
            }
        }
        Collections.sort(this.effectList);
    }

    public int getEffectImageResource(String effectCode, Activity context) {
        AlchemyEffect effect = this.effects.get(effectCode);
        return context.getResources().getIdentifier(effect.getImage(), "drawable", context.getPackageName().toLowerCase());
    }

    public int getIngredientImageResource(Ingredient ingredient, Activity context) {
        return context.getResources().getIdentifier(ingredient.getImage(), "drawable", context.getPackageName().toLowerCase());
    }
}
