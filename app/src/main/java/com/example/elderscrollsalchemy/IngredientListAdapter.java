package com.example.elderscrollsalchemy;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IngredientListAdapter extends BaseExpandableListAdapter
{
	private final Activity context;

	public IngredientListAdapter( Activity _context )
	{
		super();
		this.context = _context;
	}

	public Object getChild( int _groupPosition, int _childPosition )
	{
		final AlchemyGame game = AlchemyApplication.instance.currentGame;
		final AlchemyPackage alchemyPackage = game.packages.get( _groupPosition );
		return alchemyPackage.ingredients.get( _childPosition );
	}

	public long getChildId( int _groupPosition, int _childPosition)
	{
		return _childPosition;
	}
	
	public View getChildView( final int _groupPosition, final int _childPosition,
			boolean _isLastChild, View _convertView, ViewGroup _parent ) 
	{
		final AlchemyGame game = AlchemyApplication.instance.currentGame;
		if ( _convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater)context
					.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			
			_convertView = inflater.inflate( R.layout.ingredient_child_row,  _parent, false );
		}
		_convertView.setId( _childPosition );
		TextView textView = (TextView)_convertView.findViewById( R.id.label );
		ImageView imageView = (ImageView)_convertView.findViewById( R.id.icon );
		
		final AlchemyPackage alchemyPackage = game.packages.get( _groupPosition );
		final Ingredient ingredient = alchemyPackage.ingredients.get( _childPosition );
		
		textView.setText( ingredient.name );
		imageView.setImageResource( ingredient.imageID );
		
		if ( ingredient.selected )
		{
			_convertView.setBackgroundColor( 0xFFD7BC91 );
			textView.setTextColor( 0xFF000000 );
		}
		else
		{
			_convertView.setBackgroundColor( 0x00000000 );
			textView.setTextColor( 0xFFFFFFFF );
		}
		
		return _convertView;
	}

	public int getChildrenCount( int _groupPosition )
	{
		final AlchemyGame game = AlchemyApplication.instance.currentGame;
		final AlchemyPackage alchemyPackage = game.packages.get( _groupPosition );
		return alchemyPackage.ingredients.size();
	}

	public Object getGroup( int _groupPosition )
	{
		final AlchemyGame game = AlchemyApplication.instance.currentGame;
		return game.packages.get( _groupPosition );
	}

	public int getGroupCount()
	{
		final AlchemyGame game = AlchemyApplication.instance.currentGame;
		return game.packages.size();
	}

	public long getGroupId( int _groupPosition )
	{
		return _groupPosition;
	}

	public View getGroupView( int _groupPosition, boolean _isExpanded,
			View _convertView, ViewGroup _parent ) 
	{
		final AlchemyGame game = AlchemyApplication.instance.currentGame;
		final AlchemyPackage alchemyPackage = game.packages.get( _groupPosition );
		
		if ( _convertView == null )
		{
			LayoutInflater layoutInflater = (LayoutInflater)context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
			_convertView = layoutInflater.inflate( R.layout.ingredient_group_row,
					_parent, false );
		}
		
		_convertView.setId( _groupPosition );
		
		final TextView textView = (TextView)_convertView.findViewById( R.id.ingredient_group_text );
		textView.setText( alchemyPackage.name + " Ingredients");
		
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
		return true;
	}
}

/*
public class IngredientListAdapter extends ArrayAdapter<Ingredient>
{
	private final Context context;
	
	public IngredientListAdapter( Context _context, AlchemyGame _game )
	{
		super( _context, R.layout.ingredient_row );//_game.ingredients );
		this.context = _context;
		_game.ingredientAdapter = this;
	}
	
	@Override 
	public Ingredient getItem( int _position )
	{
		AlchemyApplication app = (AlchemyApplication)this.context.getApplicationContext();
		return app.currentGame.ingredients.get( _position );
	}
	
	@Override
	public View getView( int _position, View _convertView, ViewGroup _parent )
	{
		AlchemyApplication app = (AlchemyApplication)this.context.getApplicationContext();
		if ( _convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater)context
					.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			
			_convertView = inflater.inflate( R.layout.ingredient_row,  _parent, false );
		}
		_convertView.setId( _position );
		TextView textView = (TextView)_convertView.findViewById( R.id.label );
		ImageView imageView = (ImageView)_convertView.findViewById( R.id.icon );
		
		Ingredient ingredient = app.currentGame.ingredients.get( _position );
		textView.setText( ingredient.name );
		
		imageView.setImageResource( ingredient.imageID );
		
		textView.setSelected( ingredient.selected );
		
		if ( ingredient.selected )
		{
			_convertView.setBackgroundColor( 0xFFD7BC91 );
			textView.setTextColor( 0xFF000000 );
		}
		else
		{
			_convertView.setBackgroundColor( 0x00000000 );
			textView.setTextColor( 0xFFFFFFFF );
		}
		
		return _convertView;
	}
}*/