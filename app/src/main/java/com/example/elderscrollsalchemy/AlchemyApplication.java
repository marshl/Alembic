package com.example.elderscrollsalchemy;

import android.app.Activity;

import java.util.HashMap;

public class AlchemyApplication {
    public static AlchemyApplication instance;

    public final HashMap<String, AlchemyGame> gameMap = new HashMap<String, AlchemyGame>();

    public AlchemyGame currentGame;

    public Ingredient ingredientToRemove;

    public int getIngredientImageID(Activity _context, Ingredient _ingredient) {
        String imageName = _ingredient.name.toLowerCase().replace(' ', '_').replace("'", "").replace("-", "_").replace(".", "");
        String prefix = _ingredient.parentPackage.parentGame.getPrefix();
        imageName = prefix + "_" + imageName;
        return _context.getResources().getIdentifier(imageName, "drawable", _context.getPackageName().toLowerCase());
    }

    public int getEffectIcon(Activity _context, String _effectName, String _prefix) {
        _effectName = _effectName.replace("Agility", "Attribute")
                .replace("Endurance", "Attribute")
                .replace("Intelligence", "Attribute")
                .replace("Luck", "Attribute")
                .replace("Personality", "Attribute")
                .replace("Speed", "Attribute")
                .replace("Strength", "Attribute")
                .replace("Willpower", "Attribute");

        if (this.currentGame.getGameName().equals("Oblivion")) {
            if (_effectName.startsWith("Restore")) {
                _effectName = "Restore";
            } else if (_effectName.startsWith("Cure")) {
                _effectName = "Cure";
            } else if (_effectName.startsWith("Damage")) {
                _effectName = "Damage";
            } else if (_effectName.endsWith("Damage") && !_effectName.startsWith("Reflect")) {
                _effectName = _effectName.replace(" Damage", "");
            } else if (_effectName.startsWith("Fortify")) {
                _effectName = "Fortify";
            } else if (_effectName.startsWith("Resist")) {
                _effectName = "Resist";
            }
        }

        String imageName = _effectName.toLowerCase().replace(' ', '_').replace("'", "").replace('-', '_').replace(".", "");
        imageName = _prefix + "_" + imageName;

        return _context.getResources().getIdentifier(imageName, "drawable", _context.getPackageName().toLowerCase());
    }

    public void switchGame() {
        String otherName = this.currentGame.getGameName().equals("Morrowind") ? "Oblivion" : "Morrowind";
        this.currentGame = this.gameMap.get(otherName);
    }

    public void removeAllIngredients() {
        this.currentGame.removeAllIngredients();
    }
}
