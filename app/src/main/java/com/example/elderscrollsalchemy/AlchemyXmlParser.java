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

    public List<AlchemyGame> parseXml(Activity _context) throws IOException, XmlPullParserException {
        InputStream fileStream = _context.getAssets().open("xml/ingredients.xml");

        List<AlchemyGame> gameList = new ArrayList<AlchemyGame>();

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(fileStream, null);
        parser.nextTag();
        this.readRootNode(parser, gameList);

        fileStream.close();

        return gameList;
    }

    private void readRootNode(XmlPullParser _parser, List<AlchemyGame> gameList) throws IOException, XmlPullParserException {
        while (_parser.next() != XmlPullParser.END_TAG) {
            if (_parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = _parser.getName();

            if (name.equals("game")) {
                AlchemyGame game = this.readGameNode(_parser);
                gameList.add(game);
                Log.d("XML", "Added game \"" + game.getGameName() + "\"");
            } else {
                throw new IOException("Expecting \"game\" node: instead found " + name);
            }
        }
    }

    private AlchemyGame readGameNode(XmlPullParser _parser) throws IOException, XmlPullParserException {

        String gameName = null;
        String gamePrefix = null;
        ArrayList<AlchemyPackage> packages = new ArrayList<AlchemyPackage>();

        while (_parser.next() != XmlPullParser.END_TAG) {
            if (_parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String nodeName = _parser.getName();

            switch (_parser.getName()) {
                case "name":
                    gameName = this.readText(_parser);
                    break;
                case "prefix":
                    gamePrefix = this.readText(_parser);
                    break;
                case "package":
                    AlchemyPackage alchemyPackage = this.readPackageNode(_parser);
                    packages.add(alchemyPackage);
                    break;
                default:
                    throw new IOException("Unknown node type: \"" + nodeName + "\" when parsing root node");
            }
        }

        AlchemyGame game = new AlchemyGame(gameName, gamePrefix);
        game.packages.addAll(packages);

        for (AlchemyPackage alchemyPackage : game.packages) {
            alchemyPackage.setParentGame(game);
        }
        return game;
    }

    private AlchemyPackage readPackageNode(XmlPullParser _parser) throws IOException, XmlPullParserException {

        String packageName = null;
        List<Ingredient> ingredients = new ArrayList<Ingredient>();

        while (_parser.next() != XmlPullParser.END_TAG) {
            if (_parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String nodeName = _parser.getName();

            switch (nodeName) {
                case "name":
                    packageName = this.readText(_parser);
                    break;
                case "ingredient":
                    Ingredient ingredient = this.readIngredientNode(_parser);
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


    private Ingredient readIngredientNode(XmlPullParser _parser) throws IOException, XmlPullParserException {

        String ingredientName = null;
        List<String> ingredientEffects = new ArrayList<String>();

        while (_parser.next() != XmlPullParser.END_TAG) {
            if (_parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nodeName = _parser.getName();

            switch (nodeName) {
                case "name":
                    ingredientName = this.readText(_parser);
                    break;
                case "effect":
                    ingredientEffects.add(this.readText(_parser));
                    break;
                default:
                    throw new XmlPullParserException("Unknown node type \"" + nodeName + "\" found in ingredient node");
            }
        }

        return new Ingredient(ingredientName, ingredientEffects);
    }

    private String readText(XmlPullParser _parser) throws IOException, XmlPullParserException {
        String result = "";
        if (_parser.next() == XmlPullParser.TEXT) {
            result = _parser.getText();
            _parser.nextTag();
        }
        return result;
    }
}
