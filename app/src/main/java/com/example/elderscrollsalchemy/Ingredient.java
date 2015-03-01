package com.example.elderscrollsalchemy;

import java.util.ArrayList;
import java.util.Comparator;


public class Ingredient
{
	public AlchemyPackage parentPackage;

	public String name;
	public int imageID;
	public boolean selected = false;
	
	public final ArrayList<String> effects = new ArrayList<String>();
}

