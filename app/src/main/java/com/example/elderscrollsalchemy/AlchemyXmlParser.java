package com.example.elderscrollsalchemy;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.util.Log;
import android.util.Xml;

class AlchemyXmlParser
{
    private Activity context;
	
	public void parseXml( Activity _context ) throws IOException, XmlPullParserException
	{
		this.context = _context;
		InputStream fileStream = _context.getAssets().open( "xml/ingredients.xml" );
		
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature( XmlPullParser.FEATURE_PROCESS_NAMESPACES, false );
		parser.setInput( fileStream, null );
		parser.nextTag();
		this.readRootNode( parser );
		
		fileStream.close();
	}

	private void readRootNode( XmlPullParser _parser ) throws IOException, XmlPullParserException
	{
		while ( _parser.next() != XmlPullParser.END_TAG )
		{
			if ( _parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}

			String name = _parser.getName();
			
			if ( name.equals( "game" ) )
			{
				AlchemyGame game = this.readGameNode( _parser );
				AlchemyApplication.instance.gameMap.put( game.name, game );
				Log.d( "XML", "Added game \"" + game.name + "\"" );
			}
			else
			{
				throw new IOException( "Expecting \"game\" node: instead found " + name );
			}
		}
	}

	private AlchemyGame readGameNode( XmlPullParser _parser ) throws IOException, XmlPullParserException
	{
		AlchemyGame game = new AlchemyGame();
		while ( _parser.next() != XmlPullParser.END_TAG )
		{
			if ( _parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String nodeName = _parser.getName();
			
			if ( nodeName.equals( "name" ) )
			{
				game.name = this.readText( _parser );
			}
			else if ( nodeName.equals( "prefix" ) )
			{
				game.prefix = this.readText( _parser );
			}
			else if ( nodeName.equals( "package" ) )
			{
				AlchemyPackage alchemyPackage = this.readPackageNode( _parser );
				game.packages.add( alchemyPackage );
				alchemyPackage.parentGame = game;
			}
			else
			{
				throw new IOException( "Unknown node type: \"" + nodeName + "\" when parsing root node");
			}
		}
		
		for ( AlchemyPackage alchemyPackage : game.packages )
		{
			for ( Ingredient ingredient : alchemyPackage.ingredients )
			{
				ingredient.imageID = AlchemyApplication.instance.getIngredientImageID( this.context, ingredient );
			}
		}
		return game;
	}
	
	private AlchemyPackage readPackageNode( XmlPullParser _parser ) throws IOException, XmlPullParserException
	{
		AlchemyPackage alchemyPackage = new AlchemyPackage();
		
		while ( _parser.next() != XmlPullParser.END_TAG )
		{
			if ( _parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String nodeName = _parser.getName();
			
			if ( nodeName.equals( "name" ) )
			{
				alchemyPackage.name = this.readText( _parser );
			}
			else if ( nodeName.equals( "ingredient" ) )
			{
				Ingredient ingredient = this.readIngredientNode( _parser );
				alchemyPackage.ingredients.add( ingredient );
				ingredient.parentPackage = alchemyPackage;
			}
			else
			{
				throw new XmlPullParserException( "Unknown node type \"" + nodeName + "\" found in package node " + alchemyPackage.name );
			}
		}
		return alchemyPackage;
	}
	
	
	private Ingredient readIngredientNode( XmlPullParser _parser ) throws IOException, XmlPullParserException
	{
		Ingredient ingredient = new Ingredient();
		
		while ( _parser.next() != XmlPullParser.END_TAG )
		{
			if ( _parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			String nodeName = _parser.getName();
			
			if ( nodeName.equals( "name" ) )
			{
				ingredient.name = this.readText( _parser );
			}
			else if ( nodeName.equals( "effect" ) )
			{
				ingredient.effects.add( this.readText( _parser ) );
			}
			else
			{
				throw new XmlPullParserException( "Unknown node type \"" + nodeName + "\" found in ingredient node" );
			}
		}
		
		return ingredient;
	}
	
	private String readText( XmlPullParser _parser ) throws IOException, XmlPullParserException
	{
		String result = "";
		if ( _parser.next() == XmlPullParser.TEXT )
		{
			result = _parser.getText();
			_parser.nextTag();
		}
		return result;
	}
	
	
	/*private void skip( XmlPullParser _parser ) throws XmlPullParserException, IOException
	{
		if ( _parser.getEventType() != XmlPullParser.START_TAG )
		{
			throw new IllegalStateException();
		}
		
		int depth = 1;
		
		while ( depth != 0 )
		{
			switch ( _parser.next() )
			{
			case XmlPullParser.START_TAG:
				++depth;
				break;
			case XmlPullParser.END_TAG:
				--depth;
				break;
			}
		}
	}*/
}
