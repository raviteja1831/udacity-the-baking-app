package com.android.thebakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.thebakingapp.dto.Step;
import com.android.thebakingapp.fragment.BakingStepDetailFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.android.thebakingapp.util.Constants.*;

public class BakingStepDetailActivity extends AppCompatActivity {
    private static final String TAG = BakingStepDetailActivity.class.getSimpleName();

    private List<Step> bakingSteps = new ArrayList<>();
    private int currentStep = 0;

    private String videoURL = "";
    private String stepDescription = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_baking_step_detail);
        Toolbar toolbar = findViewById(R.id.toolbar_bakingStepDetail);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra(BAKING_STEP_VIDEO_URL)) {
            bakingSteps = Parcels.unwrap(getIntent().getParcelableExtra(BAKING_STEPS));
            currentStep = getIntent().getIntExtra(CURRENT_BAKING_STEP, 0);

            videoURL = getIntent().getStringExtra(BAKING_STEP_VIDEO_URL);
            stepDescription = getIntent().getStringExtra(BAKING_STEP_DESCRIPTION);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {

            Bundle bundleArgsForStepDetailFragment = new Bundle();

            bundleArgsForStepDetailFragment.putParcelable(BAKING_STEPS, Parcels.wrap(bakingSteps));
            bundleArgsForStepDetailFragment.putInt(CURRENT_BAKING_STEP, currentStep);

            bundleArgsForStepDetailFragment.putString(BAKING_STEP_VIDEO_URL, videoURL);
            bundleArgsForStepDetailFragment.putString(BAKING_STEP_DESCRIPTION, stepDescription);

            bundleArgsForStepDetailFragment.putString(
                    BakingStepDetailFragment.STEP_ID,
                    getIntent().getStringExtra(BakingStepDetailFragment.STEP_ID));

            BakingStepDetailFragment bakingStepDetailFragment = new BakingStepDetailFragment();
            bakingStepDetailFragment.setArguments(bundleArgsForStepDetailFragment);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.baking_steps_ScrollView, bakingStepDetailFragment)
                    .commit();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            navigateUpTo(new Intent(this,
                    ItemDetailActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
