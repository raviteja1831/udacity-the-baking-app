package com.android.thebakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.thebakingapp.ItemDetailActivity;
import com.android.thebakingapp.R;
import com.android.thebakingapp.dto.Recipe;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.android.thebakingapp.util.Constants.RECIPE;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    private static final String TAG = RecipesAdapter.class.getSimpleName();

    private List<Recipe> mRecipes;
    private Context context;

    public RecipesAdapter(List<Recipe> recipeList, Context context) {
        this.mRecipes = recipeList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View reciepView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_item, viewGroup, false);

        RecipeViewHolder recipeViewHolder = new RecipeViewHolder(reciepView);

        return recipeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int position) {
        recipeViewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mRecipes == null ? 0 : mRecipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_recipeName)
        TextView tvRecipeName;
        @BindView(R.id.iv_recipeImage)
        ImageView ivRecipeImage;

        @BindView(R.id.tv_servings)
        TextView tvServingSize;

        RecipeViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        void bind(int index) {
            tvRecipeName.setText(mRecipes.get(index).getName());
            tvServingSize.setText("Serving size - " + mRecipes.get(index).getServings());

            try {
                if (!StringUtils.isEmpty(mRecipes.get(index).getImage())) {
                    Picasso.get()
                            .load(mRecipes.get(index).getImage())
                            .into(ivRecipeImage);
                }
            } catch (Exception e) {
                Log.d(TAG, "exception loading image - " + e.getLocalizedMessage());
            }
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();

            Intent itemDetailActivityIntent = new Intent(context, ItemDetailActivity.class);

            Bundle bundle = new Bundle();
            bundle.putParcelable(RECIPE, Parcels.wrap(mRecipes.get(getAdapterPosition())));

            itemDetailActivityIntent.putExtras(bundle);

            context.startActivity(itemDetailActivityIntent);
        }
    }
}
