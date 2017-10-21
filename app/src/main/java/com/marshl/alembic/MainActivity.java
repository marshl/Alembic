package com.marshl.alembic;

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
import java.util.List;

public class MainActivity extends Activity {
    public IngredientListAdapter ingredientListAdapter;
    public AlchemyGame currentGame;
    private List<AlchemyGame> gameMap;
    private ExpandableListView ingredientListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        this.ingredientListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                IngredientListAdapter adapter = (IngredientListAdapter) parent.getExpandableListAdapter();
                Object obj = adapter.getChild(groupPosition, childPosition);

                Ingredient ingredient = (Ingredient) obj;
                ingredient.selected = !ingredient.selected;

                adapter.notifyDataSetChanged();

                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                parent.setItemChecked(index, true);
                return true;
            }
        });
    }

    public void switchGame() {
        this.currentGame = this.currentGame == this.gameMap.get(0) ? this.gameMap.get(1) : this.gameMap.get(0);
        this.ingredientListAdapter.setGame(this.currentGame);
        this.ingredientListView.setScrollY(0);
        this.ingredientListView.setScrollY(0);
        for (int i = 0; i < this.ingredientListAdapter.getGroupCount(); ++i) {
            this.ingredientListView.collapseGroup(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ingredient_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String switchGameTitle = this.currentGame.getPrefix().equals("mw") ? "Oblivion" : "Morrowind";
        switchGameTitle = "Switch to " + switchGameTitle;
        MenuItem switchGameItem = menu.findItem(R.id.switch_game);
        switchGameItem.setTitle(switchGameTitle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_game: {
                this.switchGame();
                return true;
            }
            case R.id.remove_ingredients: {
                this.currentGame.removeAllIngredients();
                this.ingredientListAdapter.notifyDataSetChanged();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void onMixIngredientButtonDown(View view) {
        this.currentGame.recalculateIngredientEffects();
        Intent intent = new Intent(this, EffectListActivity.class);
        intent.putExtra(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME, this.currentGame);
        startActivity(intent);
    }
}
