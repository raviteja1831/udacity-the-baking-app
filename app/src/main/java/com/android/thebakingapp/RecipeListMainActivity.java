package com.android.thebakingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.android.thebakingapp.adapter.RecipesAdapter;
import com.android.thebakingapp.dto.Recipe;
import com.android.thebakingapp.network.DownloadRecipes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeListMainActivity extends AppCompatActivity implements DownloadRecipes.Callback {

    private static final String TAG = RecipeListMainActivity.class.getSimpleName();

    public static List<Recipe> recipes = new ArrayList<>();
    @BindView(R.id.rv_recipes)
    RecyclerView recyclerView;

    private RecipesAdapter recipesAdapter;

    @Nullable
    private RecipesIdlingResource idlingResource;

    @VisibleForTesting
    @NonNull
    public RecipesIdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new RecipesIdlingResource();
        }
        return idlingResource;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recipe_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getIdlingResource();

        DownloadRecipes.volleyJsonArrayRequest(this, RecipeListMainActivity.this, idlingResource);
    }

    @Override
    public void onDownloadFinish(List<Recipe> recipesList) {
        recipes = recipesList;
        setupRecyclerView();
    }

    private void setupRecyclerView() {

        // https://stackoverflow.com/questions/9279111/determine-if-the-device-is-a-smartphone-or-tablet

        if (getResources().getBoolean(R.bool.isTablet)) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        recyclerView.setHasFixedSize(true);

        recipesAdapter = new RecipesAdapter(recipes, this);
        recyclerView.setAdapter(recipesAdapter);

    }
}
