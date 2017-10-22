package com.marshl.alembic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class IngredientListFragment extends Fragment {

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

}
