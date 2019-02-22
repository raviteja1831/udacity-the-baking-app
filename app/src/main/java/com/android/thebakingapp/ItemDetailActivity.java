package com.android.thebakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.thebakingapp.adapter.BakingStepsAdapter;
import com.android.thebakingapp.dto.Ingredient;
import com.android.thebakingapp.dto.Recipe;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;
import static com.android.thebakingapp.util.Constants.RECIPE;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeListMainActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    private static String TAG = ItemDetailActivity.class.getSimpleName();

    private static Recipe recipeFromIntent;

    @BindView(R.id.tv_ingredients)
    TextView tvIngredients;
//    @BindView((R.id.tv_ingredientsAmount))
//    TextView tvIngredientsAmount;
    // thought of doing the quantity and measure different way and so the above textView

    @BindView(R.id.fab_addToWidget)
    FloatingActionButton addToWidget;


    @BindView(R.id.rv_recipeSteps)
    RecyclerView recyclerView;

    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        ButterKnife.bind(this);

        populateToolbar();

        if (findViewById(R.id.baking_steps_ScrollView) != null) {
            mTwoPane = true;
        }

        addToWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipeToWidget();
            }
        });

        populateIngredients();

        setupRecyclerView();
    }

    private void populateToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra(RECIPE)) {
            recipeFromIntent = Parcels.unwrap(getIntent().getParcelableExtra(RECIPE));

            getSupportActionBar().setTitle(recipeFromIntent.getName());
        }
    }

    private void populateIngredients() {
        String ingredients = buildIngredients();

        // Log.d(TAG, "ingredients string - " + ingredients);

        tvIngredients.setText(ingredients);


        // tvIngredients.setText(ingredients.toString());
        // tvIngredientsAmount.setText(ingredientAmount.toString());
    }

    private String buildIngredients() {
        StringBuilder ingredients = new StringBuilder();

        // StringBuilder ingredientAmount = new StringBuilder();

        for (int i = 0; i < recipeFromIntent.getIngredients().size(); i++) {

            ingredients.append(recipeFromIntent.getIngredients().get(i).getIngredient());
            ingredients.append("  - ");
            ingredients.append(recipeFromIntent.getIngredients().get(i).getQuantity());
            ingredients.append(" ");
            ingredients.append(recipeFromIntent.getIngredients().get(i).getMeasure());

            if (i != (recipeFromIntent.getIngredients().size() - 1)) {
                ingredients.append("\n");
            }
        }
        return ingredients.toString();
    }

    private void setupRecyclerView() {
        BakingStepsAdapter bakingStepsAdapter = new BakingStepsAdapter(this, recipeFromIntent, mTwoPane);
        recyclerView.setAdapter(bakingStepsAdapter);
        recyclerView.setFocusable(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECIPE, Parcels.wrap(recipeFromIntent));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addRecipeToWidget() {
        String ingredients = buildIngredients();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int[] appWidgetIdsArr = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientAppWidgetProvider.class));

        if (appWidgetIdsArr.length == 0) {
            Toast.makeText(this, "Please add widget to view recipes from home screen.", Toast.LENGTH_SHORT).show();
        } else {

            for (int anAppWidgetIdsArr : appWidgetIdsArr) {
                String recipeName = recipeFromIntent.getName();
                IngredientAppWidgetProvider.updateAppWidget(this, appWidgetManager, anAppWidgetIdsArr, recipeName, ingredients);
                Toast.makeText(this, recipeName + " recipe uploaded to Widget!", Toast.LENGTH_SHORT).show();
            }
        }

    }
}