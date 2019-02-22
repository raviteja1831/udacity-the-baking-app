package com.android.thebakingapp.dto;

import org.parceler.Parcel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Parcel
public class Ingredient {
    private String quantity;
    private String measure;
    private String ingredient;
}

