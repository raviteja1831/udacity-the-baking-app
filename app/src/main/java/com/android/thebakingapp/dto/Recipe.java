package com.android.thebakingapp.dto;

import org.parceler.Parcel;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Parcel
public class Recipe {
    private String id;
    private String name;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private int servings;
    private String image;
}
