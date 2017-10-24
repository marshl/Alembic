package com.marshl.alembic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

public class IngredientListFragment extends Fragment implements DialogInterface.OnClickListener {

    private AlchemyGame game;
    private IngredientListAdapter ingredientListAdapter;
    private ExpandableListView ingredientListView;

    public static IngredientListFragment newInstance(AlchemyGame game) {
        IngredientListFragment fragment = new IngredientListFragment();

        Bundle args = new Bundle();
        args.putParcelable(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.game = this.getArguments().getParcelable(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.ingredient_list_fragment, container, false);

        this.ingredientListView = (ExpandableListView) rootView.findViewById(R.id.ingredient_listview);
        this.ingredientListAdapter = new IngredientListAdapter(this.getActivity(), this.game);
        this.ingredientListView.setAdapter(this.ingredientListAdapter);

        this.ingredientListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);

                    final Resources res = getResources();

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Select an Option");
                    builder.setPositiveButton("Select all", IngredientListFragment.this)
                            .setNeutralButton("Select None", IngredientListFragment.this)
                            .setNegativeButton("Cancel", IngredientListFragment.this);
                    builder.show();


                    return true;
                }

                return false;
            }
        });

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

        return rootView;
    }

    public void refreshIngredients() {
        this.ingredientListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.d("Alembic", "Which: " + which);
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                this.setAllIngredientSelection(true);
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                this.setAllIngredientSelection(false);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    }

    private void setAllIngredientSelection(boolean selected) {
        for (AlchemyPackage pack : this.game.packages) {
            for (Ingredient ingred : pack.ingredients) {
                ingred.selected = selected;
            }
        }

        this.ingredientListAdapter.notifyDataSetChanged();
    }
}
