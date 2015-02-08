package com.example.elderscrollsalchemy;

import java.util.ArrayList;

public class AlchemyPackage
{
	public String name = "UNDEFINED";
	public ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	public AlchemyGame parentGame;
	
	public void toggleAllIngredients( boolean _selected )
	{
		for ( Ingredient ingred : this.ingredients )
		{
			ingred.selected = _selected;
		}
	}
}
