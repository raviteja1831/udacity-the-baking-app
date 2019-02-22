package com.android.thebakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.thebakingapp.BakingStepDetailActivity;
import com.android.thebakingapp.ItemDetailActivity;
import com.android.thebakingapp.R;
import com.android.thebakingapp.dto.Recipe;
import com.android.thebakingapp.dto.Step;
import com.android.thebakingapp.fragment.BakingStepDetailFragment;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.android.thebakingapp.util.Constants.*;

public class BakingStepsAdapter extends RecyclerView.Adapter<BakingStepsAdapter.StepViewHolder> {

    private static final String TAG = BakingStepsAdapter.class.getSimpleName();

    private final boolean mTwoPane;
    private final ItemDetailActivity mParentActivity;

    private Recipe mRecipe;

    private List<Step> mBakingSteps;

    public BakingStepsAdapter(ItemDetailActivity parentActivity, Recipe recipe, boolean twoPane) {
        this.mParentActivity = parentActivity;
        this.mRecipe = recipe;
        this.mTwoPane = twoPane;

        this.mBakingSteps = recipe.getSteps();
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.step_item, viewGroup, false);
        StepViewHolder stepViewHolder = new StepViewHolder(view);
        return stepViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mBakingSteps == null ? 0 : mBakingSteps.size();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_bakingSteps)
        TextView tvBakingSteps;

        StepViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int index) {

//            int formatStepId = Integer.parseInt(mBakingSteps.get(index).getId()) + 1;
//            String stepId = String.valueOf(formatStepId);

            // yellow cake stepId's are messed up - so taking out step number for now

            String shortDescription = mBakingSteps.get(index).getShortDescription();

            tvBakingSteps.setText(shortDescription);
        }

        @Override
        public void onClick(View bakingStepsView) {
            Bundle bundle = new Bundle();
            if (mTwoPane) {
                bundle.putString(BakingStepDetailFragment.STEP_ID, mBakingSteps.get(getAdapterPosition()).getId());

                BakingStepDetailFragment bakingStepDetailFragment = new BakingStepDetailFragment();
                bakingStepDetailFragment.setArguments(bundle);

                mParentActivity
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.baking_steps_ScrollView, bakingStepDetailFragment)
                        .commit();

                bundle.putParcelable(BAKING_STEPS, Parcels.wrap(mRecipe.getSteps()));
                bundle.putInt(CURRENT_BAKING_STEP, getAdapterPosition());

                bundle.putString(BAKING_STEP_VIDEO_URL, mRecipe.getSteps().get(getAdapterPosition()).getVideoURL());
                bundle.putString(BAKING_STEP_DESCRIPTION, mRecipe.getSteps().get(getAdapterPosition()).getDescription());

            } else {
                Context context = bakingStepsView.getContext();

                Intent bakingStepDetailIntent = new Intent(context, BakingStepDetailActivity.class);

                bakingStepDetailIntent.putExtra(BAKING_STEPS, Parcels.wrap(mRecipe.getSteps()));
                bakingStepDetailIntent.putExtra(CURRENT_BAKING_STEP, getAdapterPosition());

                bakingStepDetailIntent.putExtra(BAKING_STEP_VIDEO_URL, mRecipe.getSteps().get(getAdapterPosition()).getVideoURL());
                bakingStepDetailIntent.putExtra(BAKING_STEP_DESCRIPTION, mRecipe.getSteps().get(getAdapterPosition()).getDescription());

                context.startActivity(bakingStepDetailIntent);
            }
        }
    }
}
