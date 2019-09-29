package com.marshl.elderalchemy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        List<String> expandedEffects = this.getExpandedEffectCodes();
        this.viewAdapter.notifyDataSetChanged();
        this.setExpandedEffects(expandedEffects);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.game = this.getArguments().getParcelable(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.effect_list_fragment, container, false);

        this.expListView = rootView.findViewById(R.id.effect_list);

        this.viewAdapter = new EffectExpandableListAdapter(this.getActivity(), game);
        this.expListView.setAdapter(this.viewAdapter);
        this.expListView.setEmptyView(rootView.findViewById(R.id.empty_effect_view));

        this.expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                EffectExpandableListAdapter adapter = (EffectExpandableListAdapter) parent.getExpandableListAdapter();
                Ingredient ing = (Ingredient) adapter.getChild(groupPosition, childPosition);
                toggleIngredient(ing);
                return true;
            }
        });

        this.expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("EffectListFragment", "Position " + position);
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);
                    EffectListFragment.this.selectedIngredient = (Ingredient) viewAdapter.getChild(groupPosition, childPosition);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Choose one");
                    builder.setPositiveButton("Select all", EffectListFragment.this)
                            .setNegativeButton("Select None", EffectListFragment.this)
                            .setNeutralButton("Details", EffectListFragment.this);
                    builder.show();
                    return true;
                }

                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                this.setAllIngredientSelection(true);
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                this.showIngredientDetail(this.selectedIngredient);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                this.setAllIngredientSelection(false);
                break;
        }
    }

    private void showIngredientDetail(Ingredient ingredient) {
        Intent intent = new Intent(this.getActivity(), IngredientDetailActivity.class);
        intent.putExtra("ingredient", ingredient);
        this.startActivity(intent);
    }

    private void setAllIngredientSelection(boolean selected) {
        List<String> expandedEffects = this.getExpandedEffectCodes();
        this.game.setIngredientSelection(selected);
        this.game.recalculateIngredientEffects();
        this.viewAdapter.notifyDataSetChanged();
        this.setExpandedEffects(expandedEffects);
    }

    private List<String> getExpandedEffectCodes() {
        List<String> expandedEffects = new ArrayList<>();

        for (int i = 0; i < this.game.getAvailableEffectCount(); ++i) {
            if (this.expListView.isGroupExpanded(i)) {
                expandedEffects.add(this.game.getEffectByIndex(i).getCode());
            }
        }
        return expandedEffects;
    }

    private void setExpandedEffects(List<String> effectList) {
        for (int i = 0; i < this.game.getAvailableEffectCount(); ++i) {
            AlchemyEffect effect = this.game.getEffectByIndex(i);
            if (effect == null) {
                continue;
            }
            if (Collections.frequency(effectList, effect.getCode()) > 0) {
                expListView.expandGroup(i);
            } else {
                expListView.collapseGroup(i);
            }
        }
    }

    private void toggleIngredient(Ingredient ing) {
        ing.setSelected(!ing.isSelected());
        List<String> expandedEffects = this.getExpandedEffectCodes();
        this.game.recalculateIngredientEffects();
        this.viewAdapter.notifyDataSetChanged();
        this.setExpandedEffects(expandedEffects);
    }
}
