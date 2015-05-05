package com.lustig.autofitgridrecyclerview.animations;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    /**************************************************************************
     **************************************************************************
     * CLASS VARIABLES ************************************** CLASS VARIABLES *
     **************************************************************************
     **************************************************************************/

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

    private int mNumVisibleRows = -1;

    private int mNumColumns = -1;

    private int mTotalItems = -1;

    private int mTimeIntervals = -1;

    private int mNumChildren = -1;

    private AutoFitRecyclerView mRecyclerView;

    private GridLayoutManager mManager;

    private boolean mFirstAnimationsCompleted = false;

    private long mAnimStartTime;
    private long mAnimEndTime;

    private long mTimeToCompleteScreenAnimation;


    /**
     * This will be how long it would hypothetically take the animation to propogate through all
     * Views in the Adapter. So, something like numTotalItems * deltaT, but I'll have to verify
     */
    private long mTimeToCompleteTotalAnimation;

    private int mChildrenAttached = 0;
    private int mRowsAdded = 0;

    private boolean mIsScrollingDown = false;

    private int mNewRowNumber;

    /**************************************************************************
     **************************************************************************
     * END - CLASS VARIABLES ************************** END - CLASS VARIABLES *
     **************************************************************************
     **************************************************************************/



    /**************************************************************************
     **************************************************************************
     * CONSTRUCTORS ******************************************** CONSTRUCTORS *
     **************************************************************************
     **************************************************************************/

    /**
     * Not changing anything, just seeing if I'll get the same behavior if I pass the recyclerView
     * into the constructor and do logic that way.
     */
    public AnimationHelper(AutoFitRecyclerView recyclerView) {

        setRecyclerView(recyclerView);

        mFirstAnimationsCompleted = false;

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

                        mNumChildren = mRecyclerView.getChildCount();


                        /**
                         * The reason why using lastItemPosition and firstItem position didn't work
                         * is because a View can be attached to the RecyclerView, but not visible
                         */
//                        mNumVisibleRows = (int) Math.ceil(((double) (lastItemPosition - firstItemPosition) / (double) mNumColumns));
                        mNumVisibleRows = (int) Math.ceil(( (double) mNumChildren / (double) mNumColumns));

                        mTotalItems = mRecyclerView.getAdapter().getItemCount();

                        Log.d("Lustig", "Number of Rows: " + mNumVisibleRows);

                        Log.d("Lustig", "Number of Children: " + mNumChildren);

                        mChildrenAttached += mNumChildren;

                        Log.d("Lustig", "Number of Items: " + mTotalItems);

                        mViewsToAnimate = new View[mNumVisibleRows][mNumColumns];

                        mTimeIntervals = mNumColumns + mNumVisibleRows - 2;

                        Log.d("Lustig", "Animation rows: " + mNumVisibleRows);

                        Log.d("Lustig", "Animation columns: " + mNumColumns);

                        Log.d("Lustig", "Animation time intervals: " + mTimeIntervals);

                        animateInitiallyVisibleViews();
                    }
                });

        mRecyclerView.setOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (dy > 0) {
                            mIsScrollingDown = true;
                        } else {
                            mIsScrollingDown = false;
                        }
                    }
                });
    }

    /**************************************************************************
     **************************************************************************
     * CLASS METHODS ****************************************** CLASS METHODS *
     **************************************************************************
     **************************************************************************/






    public void setRecyclerView(AutoFitRecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }




    private void animateInitiallyVisibleViews() {

        /**
         * This first for loop gets all current children that can be animated
         */
        for (int row = 0; row < mNumVisibleRows; ++row) {

            for (int column = 0; column < mNumColumns; ++column) {

                Log.d("Lusti", "(row, column): (" + row + ", " + column + ")");

                int viewNumber = (row * (mNumColumns) + column);

                mViewsToAnimate[row][column] = mRecyclerView.getChildAt(viewNumber);
            }
        }

        /**
         * This for loop adds each view to a queue to animate.
         *
         * I can probably refactor this logic into a much simpler for loop.
         * ToDo look into simpler implementation of this for loop
         */
        for (int T = 0; T <= (mTimeIntervals); T++) {

            for (
                    int row = 0, column = T;

                    row <= T && column >= 0;

                    row++, column--
                                            ) {

                if ((column < mNumColumns && row < mNumVisibleRows)) {
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

        /**
         * If the first wave of animations has happened 
         * && the user is scrolling down
         * && the new row coming into view has NOT been shown already
         * 
         * Use a boolean array to represent row numbers that 
         */
        if (mIsScrollingDown && mFirstAnimationsCompleted) {

            mChildrenAttached++;

            Log.d("Lusti", "Children attached: " + mChildrenAttached);

            /**
             * If the number of children added (after the initial animation) is a multiple of the
             * number of columns, we've got a new row to figure out whether to set visible immediately,
             * show partial animation for, or queue for animation.
             */
            if (mChildrenAttached % mNumColumns == 0) {

//                Log.d("Lusti", "children attached: " + mChildrenAttached);
//                Log.d("Lusti", "num columns: " + mNumColumns);

                onNewRowAttached();
            }
        }
    }

    private void onNewRowAttached() {

        mRowsAdded++;

        Log.d("Lusti", "Total rows added: " + mRowsAdded);
//        Log.d("Lusti", "Current row number: " + mNewRowNumber);
    }


    public void onClickOfCard() {

        makeViewsInvisible();

        animateInitiallyVisibleViews();
    }

    public void makeViewsInvisible() {

        /* Disappear all images */
        for (int i = 0; i < mNumChildren; i++) {

            mRecyclerView.getLayoutManager().getChildAt(i).setVisibility(View.INVISIBLE);
        }

    }

}
