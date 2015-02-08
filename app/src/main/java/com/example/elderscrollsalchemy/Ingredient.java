package com.example.elderscrollsalchemy;

import java.util.ArrayList;
import java.util.Comparator;


public class Ingredient
{
	public AlchemyPackage parentPackage;
	
	public int id;
	public String name;
	public int imageID;
	public boolean selected = false;
	
	public ArrayList<String> effects = new ArrayList<String>();
	
	public static class IngredientComparer implements Comparator<Ingredient>
	{
	    @Override
	    public int compare( Ingredient _o1, Ingredient _o2 )
	    {
	        return _o1.name.compareTo( _o2.name );
	    }
	}
}

