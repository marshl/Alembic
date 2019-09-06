package com.marshl.elderalchemy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.marshl.util.ImageViewUtils;

import java.util.List;

public class EffectExpandableListAdapter extends BaseExpandableListAdapter {
    private final Activity context;
    private final AlchemyGame currentGame;

    public EffectExpandableListAdapter(Activity context, AlchemyGame game) {
        this.context = context;
        this.currentGame = game;
    }

    public Object getChild(int groupPosition, int childPosition) {

        AlchemyEffect effect = this.currentGame.getEffectByIndex(groupPosition);
        List<Ingredient> ingredients = this.currentGame.getIngredientsForEffect(effect.getCode());
        return ingredients.get(childPosition);
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

        TextView label = convertView.findViewById(R.id.effect_sub_text);
        ImageView image = convertView.findViewById(R.id.effect_sub_image);
        CheckBox checkbox = convertView.findViewById(R.id.effect_checkbox);

        label.setText(ingredient.getName());
        label.setTypeface(null, ingredient.isSelected() ? Typeface.BOLD : Typeface.ITALIC);
        image.setImageResource(this.currentGame.getIngredientImageResource(ingredient, this.context));
        if (ingredient.isSelected()) {
            ImageViewUtils.setUnlocked(image);
        } else {
            ImageViewUtils.setLocked(image);
        }
        checkbox.setChecked(ingredient.isSelected());

        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        if (this.currentGame.getAvailableEffectCount() <= groupPosition) {
            return 0;
        }

        AlchemyEffect effect = this.currentGame.getEffectByIndex(groupPosition);
        return this.currentGame.getIngredientsForEffect(effect.getCode()).size();
    }

    public Object getGroup(int groupPosition) {
        return this.currentGame.getEffectByIndex(groupPosition);
    }

    public int getGroupCount() {
        return this.currentGame.getAvailableEffectCount();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.effect_group_item,
                    parent, false);
        }

        AlchemyEffect effect = this.currentGame.getEffectByIndex(groupPosition);

        TextView textView = convertView.findViewById(R.id.effect_text_view);
        textView.setTypeface(null, effect.getCanBeCrafted() ? Typeface.BOLD : Typeface.ITALIC);
        textView.setText(effect.getName());

        ImageView imageView = convertView.findViewById(R.id.effect_image_view);
        int imageID = this.currentGame.getEffectImageResource(effect.getCode(), this.context);
        imageView.setImageResource(imageID);

        if (effect.getCanBeCrafted()) {
            ImageViewUtils.setUnlocked(imageView);
        } else {
            ImageViewUtils.setLocked(imageView);
        }
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
