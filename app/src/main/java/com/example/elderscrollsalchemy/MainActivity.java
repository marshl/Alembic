package com.example.elderscrollsalchemy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {
    private List<AlchemyGame> gameMap;
    public IngredientListAdapter ingredientListAdapter;
    public AlchemyGame currentGame;
    private ExpandableListView ingredientListView;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        this.setContentView(R.layout.activity_main);


        try {
            AlchemyXmlParser parser = new AlchemyXmlParser();
            this.gameMap = parser.parseXml(this);
        } catch (IOException | XmlPullParserException ex) {
            throw new RuntimeException(ex);
        }

        this.currentGame = this.gameMap.get(0);

        this.ingredientListView = (ExpandableListView) this.findViewById(R.id.ingredient_listview);
        this.ingredientListAdapter = new IngredientListAdapter(this, this.currentGame);
        this.ingredientListView.setAdapter(this.ingredientListAdapter);

        this.ingredientListView.setOnChildClickListener(new IngredientOnChildClickListener());
    }

    public void switchGame() {
        //String otherName = this.currentGame.getGameName().equals("Morrowind") ? "Oblivion" : "Morrowind";
        //this.currentGame = this.gameMap.get(otherName);
        this.currentGame = this.currentGame == this.gameMap.get(0) ? this.gameMap.get(1) : this.gameMap.get(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ingredient_menu, _menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu _menu) {
        String switchGameTitle = this.currentGame.getPrefix().equals("mw") ? "Oblivion" : "Morrowind";
        switchGameTitle = "Switch to " + switchGameTitle;
        MenuItem switchGameItem = _menu.findItem(R.id.switch_game);
        switchGameItem.setTitle(switchGameTitle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        switch (_item.getItemId()) {
            case R.id.switch_game: {
                this.switchGame();
                this.ingredientListView.setScrollY(0);
                this.ingredientListView.setScrollY(0);
                for (int i = 0; i < this.ingredientListAdapter.getGroupCount(); ++i) {
                    this.ingredientListView.collapseGroup(i);
                }
                this.ingredientListAdapter.notifyDataSetChanged();
                return true;
            }
            case R.id.remove_ingredients: {
                this.currentGame.removeAllIngredients();
                this.ingredientListAdapter.notifyDataSetChanged();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(_item);
            }
        }
    }

    public void onMixIngredientButtonDown(View _view) {
        this.currentGame.recalculateIngredientEffects();
        Intent intent = new Intent(this, EffectListActivity.class);
        intent.putExtra(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME, this.currentGame);
        startActivity(intent);
    }
}
