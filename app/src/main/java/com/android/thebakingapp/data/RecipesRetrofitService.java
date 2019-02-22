package com.android.thebakingapp.data;

import com.android.thebakingapp.dto.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipesRetrofitService {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getAllRecipes();
}