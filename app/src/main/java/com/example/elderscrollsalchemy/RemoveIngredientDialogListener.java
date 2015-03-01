package com.example.elderscrollsalchemy;

import android.content.DialogInterface;

public class RemoveIngredientDialogListener implements DialogInterface.OnClickListener
{
	private final Ingredient ingredient;
	
	public RemoveIngredientDialogListener( Ingredient _ingredient )
	{
		this.ingredient = _ingredient;
	}
	
	@Override
    public void onClick( DialogInterface dialog, int which )
    {
        switch  ( which )
        {
        case DialogInterface.BUTTON_POSITIVE:
        {
        	EffectListActivity.instance.removeIngredient( this.ingredient );
            break;
        }
        }
        AlchemyApplication.instance.ingredientToRemove = null;
    }
}
