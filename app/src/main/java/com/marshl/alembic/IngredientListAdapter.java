package com.marshl.alembic;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class IngredientListAdapter extends BaseExpandableListAdapter {

    private final Activity context;

    private AlchemyGame alchemyGame;

    public IngredientListAdapter(Activity context, AlchemyGame alchemyGame) {
        super();
        this.context = context;
        this.alchemyGame = alchemyGame;
    }

    public void setGame(AlchemyGame game) {
        this.alchemyGame = game;
        this.notifyDataSetChanged();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        final AlchemyPackage alchemyPackage = this.alchemyGame.packages.get(groupPosition);
        return alchemyPackage.ingredients.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final AlchemyPackage alchemyPackage = this.alchemyGame.packages.get(groupPosition);
        final Ingredient ingredient = alchemyPackage.ingredients.get(childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.ingredient_child_row, parent, false);
        }

        convertView.setId(childPosition);
        final TextView textView = (TextView) convertView.findViewById(R.id.label);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        final CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.ingredient_checkbox);

        textView.setText(ingredient.getName());
        imageView.setImageResource(this.alchemyGame.getIngredientImageResource(ingredient, this.context));
        checkbox.setChecked(ingredient.isSelected());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        final AlchemyPackage alchemyPackage = this.alchemyGame.packages.get(groupPosition);
        return alchemyPackage.ingredients.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.alchemyGame.packages.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.alchemyGame.packages.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        final Resources res = context.getResources();
        final AlchemyPackage alchemyPackage = this.alchemyGame.packages.get(groupPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.ingredient_group_row,
                    parent, false);
        }

        convertView.setId(groupPosition);

        final TextView textView = (TextView) convertView.findViewById(R.id.ingredient_group_text);
        textView.setText(alchemyPackage.getPackageName());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
