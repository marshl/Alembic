package com.example.elderscrollsalchemy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

class EffectOnChildClickListener implements OnChildClickListener {
    @Override
    public boolean onChildClick(ExpandableListView _parent, View _view, int _groupPosition, int _childPosition, long _id) {
        if (AlchemyApplication.instance.ingredientToRemove != null) {
            return true;
        }

        Ingredient ingredient = AlchemyApplication.instance.ingredientToRemove = (Ingredient) _parent.getExpandableListAdapter()
                .getChild(_groupPosition, _childPosition);

        RemoveIngredientDialogListener dialogListener = new RemoveIngredientDialogListener(ingredient);

        AlertDialog.Builder builder = new AlertDialog.Builder(_view.getContext());
        builder.setMessage("Remove " + ingredient.name + "?").setPositiveButton("Yes", dialogListener)
                .setNegativeButton("No", dialogListener);

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                AlchemyApplication.instance.ingredientToRemove = null;
            }
        });

        builder.show();

        return true;
    }
}
