package com.example.elderscrollsalchemy;

import android.app.AlertDialog;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class EffectOnChildClickListener implements OnChildClickListener
{
	@Override
	public boolean onChildClick( ExpandableListView _parent, View _view, int _groupPosition, int _childPosition, long _id )
	{
		if ( AlchemyApplication.instance.ingredientToRemove != null )
		{
			return true;
		}
		
		Ingredient ingred = AlchemyApplication.instance.ingredientToRemove = (Ingredient)_parent.getExpandableListAdapter()
				.getChild( _groupPosition, _childPosition );
		
		RemoveIngredientDialogListener dialogListener = new RemoveIngredientDialogListener( ingred );
		
		AlertDialog.Builder builder = new AlertDialog.Builder( _view.getContext() );
		builder.setMessage( "Remove " + ingred.name + "?" ).setPositiveButton( "Yes", dialogListener )
	        .setNegativeButton( "No", dialogListener ).show();
	
		return true;
	}
}
