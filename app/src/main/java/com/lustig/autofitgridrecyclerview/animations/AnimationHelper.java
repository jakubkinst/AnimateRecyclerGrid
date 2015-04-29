package com.lustig.autofitgridrecyclerview.animations;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.lustig.autofitgridrecyclerview.adapters.NumberedAdapter;
import com.lustig.autofitgridrecyclerview.recyclers.AutoFitRecyclerView;

/**
 * With this class, I am going to achieve the radial reaction effect described on the
 *  Material Guidelines docs here:
 *  http://www.google.com/design/spec/animation/responsive-interaction.html#responsive-interaction-user-input
 */

/**
 * Questions / Concerns
 *
 * How many animations can happen at one time?
 * How should I deal with that?
 * Will a simple boolean flag work for 2, 3 or more animations occurring in parallel?
 */

/**
 * I'm not worried about efficiency so much as actually doing what I want to do.
 * After I get something working, I can work on finding better ways to do things,
 * but if I bog myself down with trying to pre-optimize, ain't shit gon' get did...
 */

public class AnimationHelper {

    public static final long DEFAULT_DURATION_MS = 1350;

    private static final String DEFAULT_PROPERTY_TO_ANIMATE = "alpha";

    /* Type of property of View being animated */
    private String mViewPropertyToAnimate = DEFAULT_PROPERTY_TO_ANIMATE;

    /* Duration of animations, set to default value, but can be changed */
    private long mAnimationDuration = DEFAULT_DURATION_MS;

    /* Length of time to wait before spawning next wave of animations */
    private long mDeltaT = (long) (mAnimationDuration / 3.5);

    /* Rather than adding Views one by one, I'm now going to try passing in an array of Views */
    private View[][] mViews;

    /* Rather than adding Views one by one, I'm now going to try passing in an array of Views */
    private View[][] mViewsToAnimate;

    private int mNumRows = -1;

    private int mNumColumns = -1;

    private int mTotalItems = -1;

    private int mTimeIntervals = -1;

    private int mNumChildren = -1;

    private AutoFitRecyclerView mRecyclerView;

    private GridLayoutManager mManager;


    /**
     * Not changing anything, just seeing if I'll get the same behavior if I pass the recyclerView
     * into the constructor and do logic that way.
     */
    public AnimationHelper(AutoFitRecyclerView recyclerView) {

        mRecyclerView = recyclerView;



        mManager = ((GridLayoutManager) mRecyclerView.getLayoutManager());

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        mNumColumns = ((NumberedAdapter) mRecyclerView.getAdapter()).getNumColumns();

                        int firstItemPosition = mManager.findFirstCompletelyVisibleItemPosition();
                        int lastItemPosition = mManager.findLastVisibleItemPosition();

                        Log.d("IMPORTANT", "" + firstItemPosition);
                        Log.d("IMPORTANT", "" + lastItemPosition);

                        mNumRows = (int) Math.ceil(((double) (lastItemPosition - firstItemPosition) / (double) mNumColumns));
                        mNumChildren = mRecyclerView.getChildCount();

                        mTotalItems = mRecyclerView.getAdapter().getItemCount();

                        Log.d("IMPORTANT", "Number of Rows: " + mNumRows);
                        Log.d("IMPORTANT", "Number of Children: " + mNumChildren);

                        Log.d("IMPORTANT", "Number of Items: " + mTotalItems);



                        mViewsToAnimate = new View[mNumRows][mNumColumns];

                        for (int row = 0; row < mNumRows; ++row) {

                            for (int column = 0; column < mNumColumns; ++column) {

                                int viewNumber = (row * (mNumColumns) + column);

                                Log.d("Lustig", "adding row " + row + ", column " + column);
                                Log.d("Lustig", "View number: " + viewNumber);

                                mViewsToAnimate[row][column] = mRecyclerView.getChildAt(viewNumber);

                            }
                        }

                        mTimeIntervals = mNumColumns + mNumRows - 2;

                        Log.d("Lustig", "Animation rows: " + mNumRows);

                        Log.d("Lustig", "Animation columns: " + mNumColumns);

                        Log.d("Lustig", "Animation time intervals: " + mTimeIntervals);

                        animateAllViews();

                    }

                });

    }

    public AnimationHelper(View[][] viewsToAnimate, AutoFitRecyclerView recyclerView) {

        mRecyclerView = recyclerView;

        mViews = viewsToAnimate;

        mNumRows = mViews.length;
        mNumColumns = mViews[0].length;

        mTimeIntervals = mNumColumns + mNumRows - 2;

        Log.d("Lustig", "Animation rows: " + mNumRows);

        Log.d("Lustig", "Animation columns: " + mNumColumns);

        Log.d("Lustig", "Animation time intervals: " + mTimeIntervals);

        animateAllViews();
    }

    private void animateAllViews() {

        for (int T = 0; T <= (mTimeIntervals); T++) {

            for (
                    int row = 0, column = T;

                    row <= T && column >= 0;

                    row++, column--) {

                if ((column < mNumColumns && row < mNumRows)) {
                    Log.d("Animation", "T = " + T + ":\t(" + row + ", " + column + ")");


                    final View v = mViewsToAnimate[row][column];
                    final Handler handler = new Handler();

                    handler.postDelayed(
                            new Runnable() {

                                @Override
                                public void run() {

                                    animateSingleView(v);

                                }
                            }, mDeltaT * T);

                }

            }

        }

    }

    private void animateSingleView(final View v) {

        /* Right now, using a single animation, will change in the future */
        ObjectAnimator animator =
                ObjectAnimator.ofFloat(v, mViewPropertyToAnimate, 0f, 1f);

        animator.setDuration(mAnimationDuration);

        v.setVisibility(View.VISIBLE);

        animator.start();

    }

    protected class IntelliView {

        public int row = -1;

        public int col = -1;

        public int timeInterval = -1;

    }

}
