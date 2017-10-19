package com.example.elderscrollsalchemy;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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

    public Object getChild(int groupPosition, int childPosition) {
        final AlchemyPackage alchemyPackage = this.alchemyGame.packages.get(groupPosition);
        return alchemyPackage.ingredients.get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.ingredient_child_row, parent, false);
        }
        convertView.setId(childPosition);
        TextView textView = (TextView) convertView.findViewById(R.id.label);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);

        final AlchemyPackage alchemyPackage = this.alchemyGame.packages.get(groupPosition);
        final Ingredient ingredient = alchemyPackage.ingredients.get(childPosition);

        textView.setText(ingredient.getName());
        imageView.setImageResource(ingredient.imageID);

        if (ingredient.selected) {
            convertView.setBackgroundColor(0xFFD7BC91);
            textView.setTextColor(0xFF000000);
        } else {
            convertView.setBackgroundColor(0x00000000);
            textView.setTextColor(0xFFFFFFFF);
        }

        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        final AlchemyPackage alchemyPackage = this.alchemyGame.packages.get(groupPosition);
        return alchemyPackage.ingredients.size();
    }

    public Object getGroup(int groupPosition) {
        return this.alchemyGame.packages.get(groupPosition);
    }

    public int getGroupCount() {
        return this.alchemyGame.packages.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

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
