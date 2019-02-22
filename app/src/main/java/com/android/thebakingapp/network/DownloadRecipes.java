package com.android.thebakingapp.network;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.thebakingapp.RecipesIdlingResource;
import com.android.thebakingapp.dto.Recipe;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// https://www.androidtutorialpoint.com/networking/android-volley-tutorial/

public class DownloadRecipes {
    private static final String TAG = DownloadRecipes.class.getSimpleName();

    final static List<Recipe> mRecipes = new ArrayList<>();

    public interface Callback {
        void onDownloadFinish(List<Recipe> recipes);
    }

   public static void volleyJsonArrayRequest(Context context, final Callback callback,
                                       @Nullable final RecipesIdlingResource idlingResource) {

        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        String cloudfrontURL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(cloudfrontURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject recipeJSONObject;
                mRecipes.clear();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        recipeJSONObject = response.getJSONObject(i);

                        Gson gson = new GsonBuilder().create();

                        Recipe recipe = gson.fromJson(recipeJSONObject.toString(), Recipe.class);
                        mRecipes.add(recipe);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onDownloadFinish(mRecipes);

                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

        requestQueue.add(jsonArrayReq);
    }
}
