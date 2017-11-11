package com.marshl.elderalchemy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EffectListFragment extends Fragment implements DialogInterface.OnClickListener {

    private ExpandableListView expListView;
    private EffectExpandableListAdapter viewAdapter;

    private AlchemyGame game;

    private Ingredient selectedIngredient = null;


    public static EffectListFragment newInstance(AlchemyGame game) {
        EffectListFragment fragment = new EffectListFragment();

        Bundle args = new Bundle();
        args.putParcelable(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME, game);
        fragment.setArguments(args);
        return fragment;
    }

    public void refreshEffects() {
        this.viewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.game = this.getArguments().getParcelable(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_effect_list, container, false);

        this.expListView = (ExpandableListView) rootView.findViewById(R.id.effect_list);

        this.viewAdapter = new EffectExpandableListAdapter(this.getActivity(), game);
        this.expListView.setAdapter(this.viewAdapter);
        this.expListView.setEmptyView(rootView.findViewById(R.id.empty_effect_view));

        this.expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final Resources res = getResources();
                selectedIngredient = (Ingredient) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage(res.getString(R.string.remove_ingredient, selectedIngredient.getName()));
                builder.setPositiveButton(res.getString(R.string.remove_ingredient_yes), EffectListFragment.this)
                        .setNegativeButton(res.getString(R.string.remove_ingredient_no), EffectListFragment.this);
                builder.show();
                return true;
            }
        });

        return rootView;
    }

    private void removeIngredient(Ingredient ingredient) {
        List<String> expandedEffects = new ArrayList<>();

        for (int i = 0; i < this.game.getAvailableEffectCount(); ++i) {
            if (this.expListView.isGroupExpanded(i)) {
                expandedEffects.add(this.game.getEffectByIndex(i).getCode());
            }
        }

        ingredient.setSelected(false);
        this.game.recalculateIngredientEffects();

        for (int i = 0; i < this.game.getAvailableEffectCount(); ++i) {
            AlchemyEffect effect = this.game.getEffectByIndex(i);
            if (Collections.frequency(expandedEffects, effect.getCode()) > 0) {
                expListView.expandGroup(i);
            } else {
                expListView.collapseGroup(i);
            }
        }

        this.viewAdapter.notifyDataSetChanged();
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
