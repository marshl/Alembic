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
    private String name;
    private String prefix;

    public AlchemyGame(Parcel in) {
        super();
        this.name = in.readString();
        this.prefix = in.readString();

        Object[] packageArray = in.readArray(AlchemyPackage.class.getClassLoader());

        for (Object pkg : packageArray) {
            this.packages.add((AlchemyPackage) pkg);
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
                if (!ingredient.selected) {
                    continue;
                }

                for (String effect : ingredient.effects) {
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

    public void removeAllIngredients() {
        for (AlchemyPackage pack : this.packages) {
            pack.toggleAllIngredients(false);
        }
    }


    public int getEffectIcon(Activity context, String effectName, String prefix) {
        effectName = effectName.replace("Agility", "Attribute")
                .replace("Endurance", "Attribute")
                .replace("Intelligence", "Attribute")
                .replace("Luck", "Attribute")
                .replace("Personality", "Attribute")
                .replace("Speed", "Attribute")
                .replace("Strength", "Attribute")
                .replace("Willpower", "Attribute");

        if (this.getGameName().equals("Oblivion")) {
            if (effectName.startsWith("Restore")) {
                effectName = "Restore";
            } else if (effectName.startsWith("Cure")) {
                effectName = "Cure";
            } else if (effectName.startsWith("Damage")) {
                effectName = "Damage";
            } else if (effectName.endsWith("Damage") && !effectName.startsWith("Reflect")) {
                effectName = effectName.replace(" Damage", "");
            } else if (effectName.startsWith("Fortify")) {
                effectName = "Fortify";
            } else if (effectName.startsWith("Resist")) {
                effectName = "Resist";
            }
        }

        String imageName = effectName.toLowerCase().replace(' ', '_').replace("'", "").replace('-', '_').replace(".", "");
        imageName = prefix + "_" + imageName;

        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName().toLowerCase());
    }

    public int getIngredientImageID(Activity context, Ingredient ingredient) {
        String imageName = ingredient.getName().toLowerCase().replace(' ', '_').replace("'", "").replace("-", "_").replace(".", "");
        imageName = this.getPrefix() + "_" + imageName;
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName().toLowerCase());
    }
}
