package com.marshl.elderalchemy;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private final List<AlchemyPackage> packages = new ArrayList<>();
    private final Map<String, AlchemyEffect> effectMap = new HashMap<>();
    private final String name;
    private final String prefix;
    private Map<String, List<Ingredient>> effectToIngredientMap = new HashMap<>();
    private List<String> availableEffects = new ArrayList<>();
    private List<String> levels;
    private int currentLevelIndex;

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
            this.effectMap.put(effect.getCode(), effect);
        }

        String[] lvls = null;
        in.readStringArray(lvls);
        if (lvls != null) {
            this.levels = Arrays.asList(lvls);
        }

        this.currentLevelIndex = in.readInt();
    }

    AlchemyGame(String name, String prefix, List<String> levels) {
        this.name = name;
        this.prefix = prefix;
        this.levels = levels;
        if (this.levels != null) {
            this.currentLevelIndex = this.levels.size() - 1;
        } else {
            this.currentLevelIndex = -1;
        }

    }

    void addPackage(AlchemyPackage pkg) {
        this.packages.add(pkg);
    }

    int getPackageCount() {
        return this.packages.size();
    }

    AlchemyPackage getPackage(int index) {
        return this.packages.get(index);
    }

    void addEffect(AlchemyEffect effect) {
        this.effectMap.put(effect.getCode(), effect);
    }

    int getAvailableEffectCount() {
        return this.availableEffects.size();
    }

    AlchemyEffect getEffectByIndex(int index) {
        return this.getEffect(this.availableEffects.get(index));
    }

    private AlchemyEffect getEffect(String code) {
        return this.effectMap.get(code);
    }

    List<Ingredient> getIngredientsForEffect(String effectCode) {
        return this.effectToIngredientMap.get(effectCode);
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
        parcel.writeArray(this.effectMap.values().toArray());
        parcel.writeStringArray(this.levels != null ? this.levels.toArray(new String[this.levels.size()]) : null);
        parcel.writeInt(this.currentLevelIndex);
    }

    String getPrefix() {
        return this.prefix;
    }

    void recalculateIngredientEffects() {
        this.effectToIngredientMap = new HashMap<>();
        this.availableEffects = new ArrayList<>();

        for (AlchemyPackage alchemyPackage : this.packages) {
            for (Ingredient ingredient : alchemyPackage.ingredients) {
                for (String effectCode : ingredient.getFirstEffects(this.currentLevelIndex + 1)) {
                    if (this.effectToIngredientMap.get(effectCode) == null) {
                        this.effectToIngredientMap.put(effectCode, new ArrayList<Ingredient>());
                        this.availableEffects.add(effectCode);
                    }
                    this.effectToIngredientMap.get(effectCode).add(ingredient);
                }
            }
        }

        for (Map.Entry<String, List<Ingredient>> entry : this.effectToIngredientMap.entrySet()) {

            AlchemyEffect effect = this.getEffect(entry.getKey());
            if (effect == null) {
                Log.e("AlchemyGame", "Unknown effect \"" + entry.getKey() + "\"");
                continue;
            }
            List<Ingredient> ingredients = entry.getValue();
            effect.setCanBeCrafted(ingredients.size() >= 2);
            int selectedIngredientCount = 0;
            for (Ingredient ing : ingredients) {
                if (ing.isSelected()) {
                    ++selectedIngredientCount;
                }
            }
            effect.setCanBeCrafted(selectedIngredientCount >= 2);

            Collections.sort(entry.getValue(), new Comparator<Ingredient>() {
                @Override
                public int compare(Ingredient a, Ingredient b) {
                    if (a.isSelected() && !b.isSelected()) {
                        return -1;
                    }

                    if (b.isSelected() && !a.isSelected()) {
                        return 1;
                    }

                    return a.getName().compareTo(b.getName());
                }
            });
        }

        Collections.sort(this.availableEffects, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                AlchemyEffect e1 = AlchemyGame.this.getEffect(o1);
                AlchemyEffect e2 = AlchemyGame.this.getEffect(o2);
                if (e1 == null || e2 == null) {
                    Log.e("AlchemyGame", "Could not compare effects " + o1 + " and " + o2);
                    return 0;
                }
                if (e1.getCanBeCrafted() && !e2.getCanBeCrafted()) {
                    return -1;
                }

                if (e2.getCanBeCrafted() && !e1.getCanBeCrafted()) {
                    return 1;
                }

                return e1.getName().compareTo(e2.getName());
            }
        });
    }

    int getEffectImageResource(String effectCode, Activity context) {
        AlchemyEffect effect = this.effectMap.get(effectCode);
        return context.getResources().getIdentifier(effect.getImage(), "drawable", context.getPackageName().toLowerCase());
    }

    int getIngredientImageResource(Ingredient ingredient, Activity context) {
        return context.getResources().getIdentifier(ingredient.getImage(), "drawable", context.getPackageName().toLowerCase());
    }

    void setIngredientSelection(boolean selected) {
        for (AlchemyPackage pack : this.packages) {
            for (Ingredient ingred : pack.ingredients) {
                ingred.setSelected(selected);
            }
        }
    }

    void loadSelectedIngredients(Set<String> ingredients) {
        for (AlchemyPackage pack : this.packages) {
            for (Ingredient ingredient : pack.ingredients) {
                ingredient.setSelected(ingredients.contains(ingredient.getName()));
            }
        }
    }

    Set<String> getselectedIngredientSet() {
        Set<String> selectedIngredients = new HashSet<>();
        for (AlchemyPackage pack : this.packages) {
            for (Ingredient ingred : pack.ingredients) {
                if (ingred.isSelected()) {
                    selectedIngredients.add(ingred.getName());
                }
            }
        }

        return selectedIngredients;
    }

    boolean hasSkillLevels() {
        return this.levels != null;
    }

    List<String> getLevels() {
        return levels;
    }

    int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    void setCurrentLevel(int level) {
        if (level == -1 && this.levels != null) {
            this.currentLevelIndex = this.levels.size();
        } else {
            this.currentLevelIndex = level;
        }

        this.recalculateIngredientEffects();
    }
}
