package com.marshl.alembic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EffectExpandableListAdapter extends BaseExpandableListAdapter {
    private final Activity context;
    private final AlchemyGame currentGame;

    public EffectExpandableListAdapter(Activity context, AlchemyGame game) {
        this.context = context;
        this.currentGame = game;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return currentGame.effectToIngredientMap.get(currentGame.effectList.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Ingredient ingredient = (Ingredient) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.effect_child_item, parent, false);
        }
        convertView.setId(childPosition);

        TextView label = (TextView) convertView.findViewById(R.id.effect_sub_text);
        ImageView image = (ImageView) convertView.findViewById(R.id.effect_sub_image);

        label.setText(ingredient.getName());
        image.setImageResource(this.currentGame.getIngredientImageID(this.context, ingredient));

        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        if (currentGame.effectList.size() <= groupPosition) {
            return 0;
        }
        String str = currentGame.effectList.get(groupPosition);
        return currentGame.effectToIngredientMap.get(str).size();
    }

    public Object getGroup(int groupPosition) {
        return currentGame.effectList.get(groupPosition);
    }

    public int getGroupCount() {
        return currentGame.effectList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String effectName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflator.inflate(R.layout.effect_group_item,
                    parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.effect_text_view);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(effectName);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.effect_image_view);
        int imageID = this.currentGame.getEffectIcon(this.context, effectName, this.currentGame.getPrefix());
        imageView.setImageResource(imageID);
        convertView.setId(groupPosition);
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
