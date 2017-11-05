package com.marshl.elderalchemy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends FragmentActivity {
    private static final int NUM_PAGES = 2;
    private static final String GAME_NAME_KEY = "GAME_NAME";
    private static final String SELECTED_INGREDIENTS_KEY = "SELECTED_INGREDIENTS";
    private static final String SHARED_PREFERENCE_KEY = "ELDERALCHEMY_SHARED_PREFS";

    private AlchemyGame currentGame;
    private Map<String, AlchemyGame> gameMap;
    private ViewPager viewPager;
    private FragmentStatePagerAdapter pagerAdapter;
    private EffectListFragment effectListFragment;
    private IngredientListFragment ingredientListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this.viewPager = (ViewPager) this.findViewById(R.id.pager);
        this.pagerAdapter = new IngredientListPagerAdapter(this.getSupportFragmentManager());
        this.viewPager.setAdapter(this.pagerAdapter);

        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Fragment fragment = null;

                if (position == 0 && MainActivity.this.ingredientListFragment != null) {
                    fragment = MainActivity.this.ingredientListFragment;
                } else if (position == 1 && MainActivity.this.effectListFragment != null) {
                    fragment = MainActivity.this.effectListFragment;
                }

                if (fragment != null) {
                    MainActivity.this.currentGame.recalculateIngredientEffects();
                    fragment.getArguments().remove(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME);
                    fragment.getArguments().putParcelable(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME, MainActivity.this.currentGame);

                    if (fragment == MainActivity.this.effectListFragment) {
                        MainActivity.this.effectListFragment.refreshEffects();
                    } else if (fragment == MainActivity.this.ingredientListFragment) {
                        MainActivity.this.ingredientListFragment.refreshIngredients();
                    }
                }

                MainActivity.this.pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        try {
            AlchemyXmlParser parser = new AlchemyXmlParser();
            this.gameMap = parser.parseXml(this);
        } catch (IOException | XmlPullParserException ex) {
            throw new RuntimeException(ex);
        }


        SharedPreferences settings = this.getSharedPreferences(SHARED_PREFERENCE_KEY, 0);

        String gameName = settings.getString(GAME_NAME_KEY, "mw");
        this.currentGame = gameMap.get(gameName);

        Set<String> selectedIngredients = settings.getStringSet(SELECTED_INGREDIENTS_KEY, null);
        if (selectedIngredients != null) {
            for (AlchemyPackage pack : this.currentGame.packages) {
                for (Ingredient ingredient : pack.ingredients) {
                    if (selectedIngredients.contains(ingredient.getName())) {
                        ingredient.setSelected(true);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {

        SharedPreferences settings = this.getSharedPreferences(SHARED_PREFERENCE_KEY, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(GAME_NAME_KEY, this.currentGame.getPrefix());

        Set<String> selectedIngredients = new HashSet<>();
        for (AlchemyPackage pack : this.currentGame.packages) {
            for (Ingredient ingred : pack.ingredients) {
                if (ingred.isSelected()) {
                    selectedIngredients.add(ingred.getName());
                }
            }
        }
        editor.putStringSet(SELECTED_INGREDIENTS_KEY, selectedIngredients);

        editor.apply();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (this.viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() - 1);
        }
    }

    private void switchGame(String prefix) {
        this.currentGame = this.gameMap.get(prefix);

        this.ingredientListFragment.getArguments().remove(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME);
        this.ingredientListFragment.getArguments().putParcelable(AlchemyGame.ALCHEMY_GAME_PARCEL_NAME, MainActivity.this.currentGame);
        this.viewPager.setCurrentItem(0);
        this.ingredientListFragment.refreshIngredients();
        this.viewPager.setAdapter(this.pagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ingredient_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_to_morrowind: {
                this.switchGame("mw");
                return true;
            }
            case R.id.switch_to_oblivion: {
                this.switchGame("ob");
                return true;
            }
            case R.id.switch_to_skyrim: {
                this.switchGame("sr");
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void onMixIngredientButtonDown(View view) {
        this.viewPager.setCurrentItem(1);
    }

    private class IngredientListPagerAdapter extends FragmentStatePagerAdapter {
        public IngredientListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    MainActivity.this.ingredientListFragment = IngredientListFragment.newInstance(MainActivity.this.currentGame);
                    return MainActivity.this.ingredientListFragment;
                case 1:
                    MainActivity.this.effectListFragment = EffectListFragment.newInstance(MainActivity.this.currentGame);
                    return MainActivity.this.effectListFragment;
                default:
                    throw new IllegalArgumentException("Unknown position " + position);
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
