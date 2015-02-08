package com.example.elderscrollsalchemy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AlchemyGame
{
	public String name;
	public String prefix;
	
	public ArrayList<AlchemyPackage> packages = new ArrayList<AlchemyPackage>();
	public HashMap<String, ArrayList<Ingredient> > effectToIngredientMap;
	public ArrayList<String> effectList;
	
	public void recalculateIngredientEffects()
	{
		this.effectToIngredientMap = new HashMap<String, ArrayList<Ingredient>>();
		this.effectList = new ArrayList<String>();
		
		for ( AlchemyPackage alchemyPackage : this.packages )
		{
			for( Ingredient ingredient : alchemyPackage.ingredients )
			{
				if ( !ingredient.selected )
				{
					continue;
				}
				
				for ( String effect : ingredient.effects )
				{
					if ( this.effectToIngredientMap.get( effect ) == null )
					{
						this.effectToIngredientMap.put( effect, new ArrayList<Ingredient>() );
						this.effectList.add( effect );
					}
					this.effectToIngredientMap.get( effect ).add( ingredient );
				}
			}
		}
		
		for ( int i = 0; i < effectList.size(); )
		{
			String effect = this.effectList.get( i );
			if ( this.effectToIngredientMap.get( effect ).size() < 2 )
			{
				this.effectToIngredientMap.remove( effect );
				this.effectList.remove( i );
			}
			else
			{
				++i;
			}
		}
		Collections.sort( this.effectList );
	}

	public void removeAllIngredients()
	{
		for ( AlchemyPackage pack : this.packages )
		{
			pack.toggleAllIngredients( false );
		}
	}
}
