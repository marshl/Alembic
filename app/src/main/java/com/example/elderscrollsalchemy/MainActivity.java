package com.example.elderscrollsalchemy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends Activity {
    public IngredientListAdapter ingredientListAdapter;
    private ExpandableListView ingredientListView;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        this.setContentView(R.layout.activity_main);

        if (AlchemyApplication.instance == null) {
            AlchemyApplication.instance = new AlchemyApplication();

            try {
                AlchemyXmlParser parser = new AlchemyXmlParser();
                parser.parseXml(this);
                Log.d("XML", "Xml Successfully parsed");
            } catch (IOException | XmlPullParserException _e) {
                _e.printStackTrace();
            }

            AlchemyApplication.instance.currentGame = AlchemyApplication.instance.gameMap.get("Morrowind");
        }

        this.ingredientListView = (ExpandableListView) this.findViewById(R.id.ingredient_listview);
        this.ingredientListAdapter = new IngredientListAdapter(this);
        this.ingredientListView.setAdapter(this.ingredientListAdapter);

        this.ingredientListView.setOnChildClickListener(new IngredientOnChildClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ingredient_menu, _menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu _menu) {
        String switchGameTitle = AlchemyApplication.instance.currentGame.getPrefix().equals("mw")
                ? "Oblivion" : "Morrowind";
        switchGameTitle = "Switch to " + switchGameTitle;
        MenuItem switchGameItem = _menu.findItem(R.id.switch_game);
        switchGameItem.setTitle(switchGameTitle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        switch (_item.getItemId()) {
            case R.id.switch_game: {
                AlchemyApplication.instance.switchGame();
                this.ingredientListView.setScrollY(0);
                this.ingredientListView.setScrollY(0);
                for (int i = 0; i < this.ingredientListAdapter.getGroupCount(); ++i) {
                    this.ingredientListView.collapseGroup(i);
                }
                this.ingredientListAdapter.notifyDataSetChanged();
                return true;
            }
            case R.id.remove_ingredients: {
                AlchemyApplication.instance.removeAllIngredients();
                this.ingredientListAdapter.notifyDataSetChanged();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(_item);
            }
        }
    }

    public void onMixIngredientButtonDown(View _view) {
        AlchemyApplication.instance.currentGame.recalculateIngredientEffects();

        Intent intent = new Intent(this, EffectListActivity.class);

        startActivity(intent);
    }
}
