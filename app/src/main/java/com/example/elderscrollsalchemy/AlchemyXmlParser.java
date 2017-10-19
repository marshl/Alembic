package com.example.elderscrollsalchemy;

import android.app.Activity;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class AlchemyXmlParser {

    public List<AlchemyGame> parseXml(Activity context) throws IOException, XmlPullParserException {
        InputStream fileStream = context.getAssets().open("xml/ingredients.xml");

        List<AlchemyGame> gameList = new ArrayList<AlchemyGame>();

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(fileStream, null);
        parser.nextTag();
        this.readRootNode(parser, gameList);

        fileStream.close();

        return gameList;
    }

    private void readRootNode(XmlPullParser parser, List<AlchemyGame> gameList) throws IOException, XmlPullParserException {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("game")) {
                AlchemyGame game = this.readGameNode(parser);
                gameList.add(game);
                Log.d("XML", "Added game \"" + game.getGameName() + "\"");
            } else {
                throw new IOException("Expecting \"game\" node: instead found " + name);
            }
        }
    }

    private AlchemyGame readGameNode(XmlPullParser parser) throws IOException, XmlPullParserException {

        String gameName = null;
        String gamePrefix = null;
        ArrayList<AlchemyPackage> packages = new ArrayList<AlchemyPackage>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String nodeName = parser.getName();

            switch (parser.getName()) {
                case "name":
                    gameName = this.readText(parser);
                    break;
                case "prefix":
                    gamePrefix = this.readText(parser);
                    break;
                case "package":
                    AlchemyPackage alchemyPackage = this.readPackageNode(parser);
                    packages.add(alchemyPackage);
                    break;
                default:
                    throw new IOException("Unknown node type: \"" + nodeName + "\" when parsing root node");
            }
        }

        AlchemyGame game = new AlchemyGame(gameName, gamePrefix);
        game.packages.addAll(packages);

        return game;
    }

    private AlchemyPackage readPackageNode(XmlPullParser parser) throws IOException, XmlPullParserException {

        String packageName = null;
        List<Ingredient> ingredients = new ArrayList<Ingredient>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String nodeName = parser.getName();

            switch (nodeName) {
                case "name":
                    packageName = this.readText(parser);
                    break;
                case "ingredient":
                    Ingredient ingredient = this.readIngredientNode(parser);
                    ingredients.add(ingredient);
                    break;
                default:
                    throw new XmlPullParserException("Unknown node type \"" + nodeName + "\" found in package node " + packageName);
            }
        }

        AlchemyPackage alchemyPackage = new AlchemyPackage(packageName);
        for (Ingredient ingredient : ingredients) {
            ingredient.parentPackage = alchemyPackage;
            alchemyPackage.ingredients.add(ingredient);
        }

        return alchemyPackage;
    }


    private Ingredient readIngredientNode(XmlPullParser parser) throws IOException, XmlPullParserException {

        String ingredientName = null;
        List<String> ingredientEffects = new ArrayList<String>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nodeName = parser.getName();

            switch (nodeName) {
                case "name":
                    ingredientName = this.readText(parser);
                    break;
                case "effect":
                    ingredientEffects.add(this.readText(parser));
                    break;
                default:
                    throw new XmlPullParserException("Unknown node type \"" + nodeName + "\" found in ingredient node");
            }
        }

        return new Ingredient(ingredientName, ingredientEffects);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
