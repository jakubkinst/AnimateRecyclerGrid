package com.lustig.autofitgridrecyclerview.animations;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.lustig.autofitgridrecyclerview.adapters.NumberedAdapter;
import com.lustig.autofitgridrecyclerview.interfaces.OnChildAttachedListener;
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

public class AnimationHelper implements OnChildAttachedListener {

    /**
     * This is the time that it takes one cell to complete an animation.
     */
    public static final long DEFAULT_DURATION_MS = 350;

    public static final long DEFAULT_DEBUG_DURATION_MS = 1250;

    private static final String DEFAULT_PROPERTY_TO_ANIMATE = "alpha";

    /* Type of property of View being animated */
    private String mViewPropertyToAnimate = DEFAULT_PROPERTY_TO_ANIMATE;

    /* Duration of animations, set to default value, but can be changed */
    private long mAnimationDuration = DEFAULT_DEBUG_DURATION_MS;

    /* Length of time to wait before spawning next wave of animations */
    private long mDeltaT = (long) (mAnimationDuration / 3.5);

    /* Rather than adding Views one by one, I'm now going to try passing in an array of Views */
    private View[][] mViewsToAnimate;

    private int mNumRows = -1;

    private int mNumColumns = -1;

    private int mTotalItems = -1;

    private int mTimeIntervals = -1;

    private int mNumChildren = -1;

    private int mPreviousLastVisibleChildPosition = -1;

    private int mCurrentLastVisibleChildPosition = -1;

    private int mCurrentScrollRow = -1;

    private AutoFitRecyclerView mRecyclerView;

    private GridLayoutManager mManager;

    private boolean mFirstAnimationsCompleted = false;

    private long mAnimStartTime;
    private long mAnimEndTime;

    private long mTimeToCompleteScreenAnimation;

    private long mTimeToCompleteTotalAnimation;

    /**
     * This one is called by MainActivity in order to pass it to the RecyclerView
     *
     * However, the side effect is that 2 instances of AnimationHelper are being created.
     * One is doing all the animating and the other is communicating with the RecyclerView.
     *
     * Let's fix that...
     */
    public AnimationHelper() {
        log("Default AnimationHelper constructor");
    }

    public void setRecyclerView(AutoFitRecyclerView recyclerView) {

        mRecyclerView = recyclerView;

    }


    /**
     * Not changing anything, just seeing if I'll get the same behavior if I pass the recyclerView
     * into the constructor and do logic that way.
     */
    public AnimationHelper(AutoFitRecyclerView recyclerView) {

        setRecyclerView(recyclerView);

        mRecyclerView.setOnChildAttachedListener(this);

        mManager = (GridLayoutManager) mRecyclerView.getLayoutManager();

        /**
         * This onGlobalLayoutListener listens for when the layout has been completed and I can
         * start the animation. It then gathers all of the views that can be animated and starts
         * the animation. I need to break up this code into smaller, more understandable chunks.
         */
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        mNumColumns = ((NumberedAdapter) mRecyclerView.getAdapter()).getNumColumns();

                        int firstItemPosition = mManager.findFirstVisibleItemPosition();
                        int lastItemPosition = mManager.findLastVisibleItemPosition();

                        Log.d("IMPORTANT", "First visible pos: " + firstItemPosition);
                        Log.d("IMPORTANT", "Last visible pos: " + lastItemPosition);

                        mNumRows = (int) Math.ceil(((double) (lastItemPosition - firstItemPosition) / (double) mNumColumns));

                        mNumChildren = mRecyclerView.getChildCount();

                        mTotalItems = mRecyclerView.getAdapter().getItemCount();

                        Log.d("Lustig", "Number of Rows: " + mNumRows);

                        Log.d("Lustig", "Number of Children: " + mNumChildren);

                        Log.d("Lustig", "Number of Items: " + mTotalItems);

                        mViewsToAnimate = new View[mNumRows][mNumColumns];

//                        for (int row = 0; row < mNumRows; ++row) {
//
//                            for (int column = 0; column < mNumColumns; ++column) {
//
//                                int viewNumber = (row * (mNumColumns) + column);
//
//                                mViewsToAnimate[row][column] = mRecyclerView.getChildAt(viewNumber);
//                            }
//                        }

                        mTimeIntervals = mNumColumns + mNumRows - 2;

                        Log.d("Lustig", "Animation rows: " + mNumRows);

                        Log.d("Lustig", "Animation columns: " + mNumColumns);

                        Log.d("Lustig", "Animation time intervals: " + mTimeIntervals);

                        animateAllViews();
                    }
                });
    }

    private void animateAllViews() {

        for (int row = 0; row < mNumRows; ++row) {

            for (int column = 0; column < mNumColumns; ++column) {

                int viewNumber = (row * (mNumColumns) + column);

                mViewsToAnimate[row][column] = mRecyclerView.getChildAt(viewNumber);
            }
        }

        for (int T = 0; T <= (mTimeIntervals); T++) {

            for (
                    int row = 0, column = T;

                    row <= T && column >= 0;

                    row++, column--
                    ) {

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

        mAnimStartTime = System.currentTimeMillis();

        final Handler animDoneHandler = new Handler();

        animDoneHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {

                        Log.d("Lustig", "First run of anims completed");

                        mFirstAnimationsCompleted = true;

                        mAnimEndTime = System.currentTimeMillis();

                        mTimeToCompleteScreenAnimation = mAnimEndTime - mAnimStartTime;

                        Log.d("Lustig", "Screen animation completed in " + mTimeToCompleteScreenAnimation + "ms");
                    }
                }, mDeltaT * (mTimeIntervals + 1)
        );

    }

    private void animateSingleView(final View v) {

        /* Right now, using a single animation, will change in the future */
        ObjectAnimator animator =
                ObjectAnimator.ofFloat(v, mViewPropertyToAnimate, 0f, 1f);

        animator.setDuration(mAnimationDuration);

        v.setVisibility(View.VISIBLE);

        animator.start();
    }

    @Override
    public void onChildAttached(View child) {

        if (mFirstAnimationsCompleted) {
            child.setVisibility(View.VISIBLE);
        }
    }


    public void onClickOfCard() {

        makeViewsInvisible();

        animateAllViews();
    }

    private void log(String msg) {

        Log.d("Lustig", msg);

    }

    public void makeViewsInvisible() {

        /* Disappear all images */
        for (int i = 0; i < mNumChildren; i++) {

            mRecyclerView.getLayoutManager().getChildAt(i).setVisibility(View.INVISIBLE);
        }

    }

}
