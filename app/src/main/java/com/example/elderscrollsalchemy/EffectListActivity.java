package com.example.elderscrollsalchemy;
 
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListView;
 
public class EffectListActivity extends Activity
{
	public static EffectListActivity instance;
	
    private ExpandableListView expListView;
    private EffectExpandableListAdapter viewAdapter;
   
    @Override
    protected void onCreate( Bundle _savedInstanceState )
    {
    	instance = this;
    	
        super.onCreate( _savedInstanceState );
        this.setContentView( R.layout.activity_effect_list );
 
        expListView = (ExpandableListView)findViewById( R.id.effect_list );
        
        AlchemyGame currentGame = AlchemyApplication.instance.currentGame;
        this.viewAdapter = new EffectExpandableListAdapter(
               this, currentGame );
        expListView.setAdapter( this.viewAdapter );
        
        expListView.setOnChildClickListener( new EffectOnChildClickListener() );
    }

	public void removeIngredient( Ingredient _ingredient )
	{
		ArrayList <String> openItems = new ArrayList<String>();

    	for ( int i = 0; i < AlchemyApplication.instance.currentGame.effectList.size(); ++i )
    	{
    		if ( this.expListView.isGroupExpanded( i ) )
    		{
    			openItems.add( AlchemyApplication.instance.currentGame.effectList.get( i ) );
    		}
    	}
    	
    	_ingredient.selected = false;
    	
    	AlchemyApplication.instance.currentGame.recalculateIngredientEffects();

		for ( int i = 0; i < AlchemyApplication.instance.currentGame.effectList.size(); ++i )
		{
			String effectName = AlchemyApplication.instance.currentGame.effectList.get( i );
			if ( Collections.frequency( openItems, effectName ) > 0 )
			{
				expListView.expandGroup( i );
			}
			else
			{
				expListView.collapseGroup( i );
			}
		}
		
		this.viewAdapter.notifyDataSetChanged();
		MainActivity.instance.ingredientListAdapter.notifyDataSetChanged();
	}
}