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

    public IngredientListAdapter(Activity _context, AlchemyGame alchemyGame) {
        super();
        this.context = _context;
        this.alchemyGame = alchemyGame;
    }

    public Object getChild(int _groupPosition, int _childPosition) {
        final AlchemyPackage alchemyPackage = this.alchemyGame.packages.get(_groupPosition);
        return alchemyPackage.ingredients.get(_childPosition);
    }

    public long getChildId(int _groupPosition, int _childPosition) {
        return _childPosition;
    }

    public View getChildView(final int _groupPosition, final int _childPosition,
                             boolean _isLastChild, View _convertView, ViewGroup _parent) {

        if (_convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            _convertView = inflater.inflate(R.layout.ingredient_child_row, _parent, false);
        }
        _convertView.setId(_childPosition);
        TextView textView = (TextView) _convertView.findViewById(R.id.label);
        ImageView imageView = (ImageView) _convertView.findViewById(R.id.icon);

        final AlchemyPackage alchemyPackage = this.alchemyGame.packages.get(_groupPosition);
        final Ingredient ingredient = alchemyPackage.ingredients.get(_childPosition);

        textView.setText(ingredient.getName());
        imageView.setImageResource(ingredient.imageID);

        if (ingredient.selected) {
            _convertView.setBackgroundColor(0xFFD7BC91);
            textView.setTextColor(0xFF000000);
        } else {
            _convertView.setBackgroundColor(0x00000000);
            textView.setTextColor(0xFFFFFFFF);
        }

        return _convertView;
    }

    public int getChildrenCount(int _groupPosition) {
        final AlchemyPackage alchemyPackage = this.alchemyGame.packages.get(_groupPosition);
        return alchemyPackage.ingredients.size();
    }

    public Object getGroup(int _groupPosition) {
        return this.alchemyGame.packages.get(_groupPosition);
    }

    public int getGroupCount() {
        return this.alchemyGame.packages.size();
    }

    public long getGroupId(int _groupPosition) {
        return _groupPosition;
    }

    public View getGroupView(int _groupPosition, boolean _isExpanded,
                             View _convertView, ViewGroup _parent) {

        final Resources res = context.getResources();
        final AlchemyPackage alchemyPackage = this.alchemyGame.packages.get(_groupPosition);

        if (_convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _convertView = layoutInflater.inflate(R.layout.ingredient_group_row,
                    _parent, false);
        }

        _convertView.setId(_groupPosition);

        final TextView textView = (TextView) _convertView.findViewById(R.id.ingredient_group_text);
        textView.setText(res.getString(R.string.ingredient_group_title, alchemyPackage.getPackageName()));

        return _convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int _groupPosition, int _childPosition) {
        return true;
    }
}
