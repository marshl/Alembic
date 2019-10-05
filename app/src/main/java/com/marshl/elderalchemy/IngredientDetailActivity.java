package com.marshl.elderalchemy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class IngredientDetailActivity extends AppCompatActivity {

    private Ingredient ingredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_detail);

        Intent intent = this.getIntent();
        this.ingredient = intent.getParcelableExtra("ingredient");

        TextView ingredientNameLabel = this.findViewById(R.id.ingredient_detail_name);
        ImageView ingredientImageView = this.findViewById(R.id.ingredient_detail_image);
        TextView ingredientGoldLabel = this.findViewById(R.id.ingredient_detail_gold_text);
        TextView ingredientWeightLabel = this.findViewById(R.id.ingredient_detail_weight_text);
        TextView ingredientDescriptionLabel = this.findViewById(R.id.ingredient_detail_description);

        ingredientNameLabel.setText(this.ingredient.getName());
        ingredientDescriptionLabel.setText(this.ingredient.getDescription());
        int imageResource = this.getResources().getIdentifier(ingredient.getImage(), "drawable", this.getPackageName().toLowerCase());
        ingredientImageView.setImageResource(imageResource);
        DecimalFormat df = new DecimalFormat("#.##");
        ingredientGoldLabel.setText(df.format(ingredient.getValue()));
        ingredientWeightLabel.setText(df.format(ingredient.getWeight()));

    }

}
