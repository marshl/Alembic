package com.example.elderscrollsalchemy;

import java.util.ArrayList;

public class AlchemyPackage
{
	public String name = "UNDEFINED";
	public final ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	public AlchemyGame parentGame;
	
	public void toggleAllIngredients( boolean _selected )
	{
		for ( Ingredient ingredient : this.ingredients )
		{
			ingredient.selected = _selected;
		}
	}
}
