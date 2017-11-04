package com.marshl.elderalchemy;

import android.app.Activity;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AlchemyXmlParser {

    public HashMap<String, AlchemyGame> parseXml(Activity context) throws IOException, XmlPullParserException {
        InputStream fileStream = context.getAssets().open("xml/ingredients.xml");

        HashMap<String, AlchemyGame> gameList = new HashMap<>();

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(fileStream, null);
        parser.nextTag();
        this.readRootNode(parser, gameList);

        fileStream.close();

        return gameList;
    }

    private void readRootNode(XmlPullParser parser, Map<String, AlchemyGame> gameList) throws IOException, XmlPullParserException {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("game")) {
                AlchemyGame game = this.readGameNode(parser);
                gameList.put(game.getPrefix(), game);
            } else {
                throw new IOException("Expecting \"game\" node: instead found " + name);
            }
        }
    }

    private AlchemyGame readGameNode(XmlPullParser parser) throws IOException, XmlPullParserException {

        String gameName = null;
        String gamePrefix = null;
        ArrayList<AlchemyPackage> packages = new ArrayList<>();
        ArrayList<AlchemyEffect> effects = new ArrayList<>();

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
                case "effect":
                    AlchemyEffect effect = this.readEffectNode(parser);
                    effects.add(effect);
                    break;
                default:
                    throw new IOException("Unknown node type: \"" + nodeName + "\" when parsing root node");
            }
        }

        AlchemyGame game = new AlchemyGame(gameName, gamePrefix);
        game.packages.addAll(packages);
        for (AlchemyEffect effect : effects) {
            game.effects.put(effect.getCode(), effect);
        }

        return game;
    }

    private AlchemyEffect readEffectNode(XmlPullParser parser) throws IOException, XmlPullParserException {
        String effectCode = null;
        String effectName = null;
        String effectImage = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nodeName = parser.getName();

            switch (nodeName) {
                case "code":
                    effectCode = this.readText(parser);
                    break;
                case "name":
                    effectName = this.readText(parser);
                    break;
                case "image":
                    effectImage = this.readText(parser);
                    break;
                default:
                    throw new XmlPullParserException("Unknown node type \"" + nodeName + "\" found in effect node");
            }
        }

        return new AlchemyEffect(effectCode, effectName, effectImage);
    }

    private AlchemyPackage readPackageNode(XmlPullParser parser) throws IOException, XmlPullParserException {

        String packageName = null;
        List<Ingredient> ingredients = new ArrayList<>();

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
            alchemyPackage.ingredients.add(ingredient);
        }

        return alchemyPackage;
    }


    private Ingredient readIngredientNode(XmlPullParser parser) throws IOException, XmlPullParserException {

        String ingredientName = null;
        String ingredientImage = null;
        List<String> ingredientEffects = new ArrayList<>();

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
                case "image":
                    ingredientImage = this.readText(parser);
                    break;
                default:
                    throw new XmlPullParserException("Unknown node type \"" + nodeName + "\" found in ingredient node");
            }
        }

        return new Ingredient(ingredientName, ingredientImage, ingredientEffects);
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
