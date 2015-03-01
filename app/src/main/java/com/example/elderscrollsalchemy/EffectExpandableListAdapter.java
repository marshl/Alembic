package com.example.elderscrollsalchemy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EffectExpandableListAdapter extends BaseExpandableListAdapter
{
	private final Activity context;
	private final AlchemyGame currentGame;

	public EffectExpandableListAdapter( Activity _context, AlchemyGame _game )
	{
		this.context = _context;
		this.currentGame = _game;
	}

	public Object getChild( int _groupPosition, int _childPosition )
	{
		return currentGame.effectToIngredientMap.get( currentGame.effectList.get( _groupPosition ) ).get( _childPosition );
	}

	public long getChildId( int _groupPosition, int _childPosition)
	{
		return _childPosition;
	}
	
	public View getChildView( final int _groupPosition, final int _childPosition,
			boolean _isLastChild, View _convertView, ViewGroup _parent ) 
	{
		final Ingredient ingredient = (Ingredient)getChild( _groupPosition, _childPosition );
		LayoutInflater inflater = context.getLayoutInflater();
		
		if ( _convertView == null )
		{
			_convertView = inflater.inflate( R.layout.effect_child_item, _parent, false );
		}
		_convertView.setId( _childPosition );
		
		TextView label = (TextView)_convertView.findViewById( R.id.effect_sub_text );
		ImageView image = (ImageView)_convertView.findViewById( R.id.effect_sub_image );
		
		label.setText( ingredient.name );
		image.setImageResource( ingredient.imageID );
		
		return _convertView;
	}

	public int getChildrenCount( int _groupPosition )
	{
		if ( currentGame.effectList.size() <= _groupPosition )
		{
			return 0;
		}
		String str = currentGame.effectList.get( _groupPosition );
		return currentGame.effectToIngredientMap.get( str ).size();
	}

	public Object getGroup( int _groupPosition )
	{
		return currentGame.effectList.get( _groupPosition );
	}

	public int getGroupCount()
	{
		return currentGame.effectList.size();
	}

	public long getGroupId( int _groupPosition )
	{
		return _groupPosition;
	}

	public View getGroupView( int _groupPosition, boolean _isExpanded,
			View _convertView, ViewGroup _parent ) 
	{
		String effectName = (String)getGroup( _groupPosition );
		if ( _convertView == null )
		{
			LayoutInflater layoutInflator = (LayoutInflater)context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
			_convertView = layoutInflator.inflate( R.layout.effect_group_item,
					_parent, false );
		}
		
		TextView textView = (TextView)_convertView.findViewById( R.id.effect_text_view );
		textView.setTypeface( null, Typeface.BOLD );
		textView.setText( effectName );
		
		//AlchemyApplication app = (AlchemyApplication)this.context.getApplication();
		
		ImageView imageView = (ImageView)_convertView.findViewById( R.id.effect_image_view );
		int imageID = AlchemyApplication.instance.getEffectIcon( this.context, effectName, this.currentGame.prefix );
		imageView.setImageResource( imageID );
		_convertView.setId( _groupPosition );
		return _convertView;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public boolean isChildSelectable( int _groupPosition, int _childPosition ) 
	{
		return AlchemyApplication.instance.ingredientToRemove == null;
	}
}