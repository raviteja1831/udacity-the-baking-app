package com.android.thebakingapp.dto;

import org.parceler.Parcel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Parcel
public class Step {

    private String id;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;
}