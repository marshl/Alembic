package com.example.elderscrollsalchemy;

import java.util.ArrayList;


public class Ingredient {
    public final ArrayList<String> effects = new ArrayList<String>();
    public AlchemyPackage parentPackage;
    public String name;
    public int imageID;
    public boolean selected = false;
}

