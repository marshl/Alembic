package com.example.elderscrollsalchemy;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class IngredientOnChildClickListener implements OnChildClickListener
{

	@Override
	public boolean onChildClick( ExpandableListView _parent, View _view,
			int _groupPosition, int _childPosition, long _id )
	{
		IngredientListAdapter adapter = (IngredientListAdapter)_parent.getExpandableListAdapter();
		Object obj = adapter.getChild( _groupPosition, _childPosition );

		Ingredient ingredient = (Ingredient)obj;
		ingredient.selected = !ingredient.selected;
		
		adapter.notifyDataSetChanged();
		
		return true;
	}

}
