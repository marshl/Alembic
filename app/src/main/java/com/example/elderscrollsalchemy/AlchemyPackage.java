package com.example.elderscrollsalchemy;

import java.util.ArrayList;

public class AlchemyPackage {
    public final ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    public String name = "UNDEFINED";
    public AlchemyGame parentGame;

    public void toggleAllIngredients(boolean _selected) {
        for (Ingredient ingredient : this.ingredients) {
            ingredient.selected = _selected;
        }
    }
}
