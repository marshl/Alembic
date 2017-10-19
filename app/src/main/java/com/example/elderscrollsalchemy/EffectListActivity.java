package com.example.elderscrollsalchemy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Collections;

public class EffectListActivity extends Activity implements DialogInterface.OnClickListener {

    private ExpandableListView expListView;
    private EffectExpandableListAdapter viewAdapter;

    private AlchemyGame currentGame;

    private Ingredient selectedIngredient = null;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        //instance = this;

        super.onCreate(_savedInstanceState);
        this.setContentView(R.layout.activity_effect_list);

        Intent intent = this.getIntent();
        this.currentGame = intent.getParcelableExtra(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME);
        this.currentGame.recalculateIngredientEffects();

        this.expListView = (ExpandableListView) findViewById(R.id.effect_list);

        this.viewAdapter = new EffectExpandableListAdapter(this, currentGame);
        expListView.setAdapter(this.viewAdapter);


        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                selectedIngredient = (Ingredient)parent.getExpandableListAdapter().getChild(groupPosition,childPosition);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Remove " + selectedIngredient.getName() + "?").setPositiveButton("Yes", EffectListActivity.this)
                        .setNegativeButton("No", EffectListActivity.this);
                builder.show();
                return true;
            }
        });
    }

    public void removeIngredient(Ingredient ingredient) {
        ArrayList<String> openItems = new ArrayList<String>();

        for (int i = 0; i < this.currentGame.effectList.size(); ++i) {
            if (this.expListView.isGroupExpanded(i)) {
                openItems.add(this.currentGame.effectList.get(i));
            }
        }

        ingredient.selected = false;

        this.currentGame.recalculateIngredientEffects();

        for (int i = 0; i < this.currentGame.effectList.size(); ++i) {
            String effectName = this.currentGame.effectList.get(i);
            if (Collections.frequency(openItems, effectName) > 0) {
                expListView.expandGroup(i);
            } else {
                expListView.collapseGroup(i);
            }
        }

        this.viewAdapter.notifyDataSetChanged();
        //TODO: MainActivity.instance.ingredientListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: {
                this.removeIngredient(this.selectedIngredient);
                break;
            }
        }
        this.selectedIngredient = null;
    }
}
