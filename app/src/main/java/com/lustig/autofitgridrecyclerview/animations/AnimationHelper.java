package com.lustig.autofitgridrecyclerview.animations;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.lustig.autofitgridrecyclerview.interfaces.OnChildAttachedListener;
import com.lustig.autofitgridrecyclerview.recyclers.AutoFitRecyclerView;

/**
 * With this class, I am going to achieve the radial reaction effect described on the
 * Material Guidelines docs here:
 * http://www.google.com/design/spec/animation/responsive-interaction.html#responsive-interaction-user-input
 * <p/>
 * Questions / Concerns
 * <p/>
 * How many animations can happen at one time?
 * How should I deal with that?
 * Will a simple boolean flag work for 2, 3 or more animations occurring in parallel?
 * <p/>
 * Questions / Concerns
 * <p/>
 * How many animations can happen at one time?
 * How should I deal with that?
 * Will a simple boolean flag work for 2, 3 or more animations occurring in parallel?
 * <p/>
 * Questions / Concerns
 * <p/>
 * How many animations can happen at one time?
 * How should I deal with that?
 * Will a simple boolean flag work for 2, 3 or more animations occurring in parallel?
 * <p/>
 * Questions / Concerns
 * <p/>
 * How many animations can happen at one time?
 * How should I deal with that?
 * Will a simple boolean flag work for 2, 3 or more animations occurring in parallel?
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

    public static final boolean DEBUG = false;

    /**
     * This is the time that it takes one cell to complete an animation.
     */
    public static final long DEFAULT_DURATION_MS = 350;

    public static final long DEFAULT_DEBUG_DURATION_MS = 2000;

    private static final String DEFAULT_PROPERTY_TO_ANIMATE = "alpha";

    /* Type of property of View being animated */
    private String mViewPropertyToAnimate = DEFAULT_PROPERTY_TO_ANIMATE;

    /* Duration of animations, set to default value, but can be changed */
    private long mAnimationDuration = DEBUG ? DEFAULT_DEBUG_DURATION_MS : DEFAULT_DURATION_MS;

    /* Length of time to wait before spawning next wave of animations */
    private long mDeltaT = (long) (mAnimationDuration / 3.5);

    /* Rather than adding Views one by one, I'm now going to try passing in an array of Views */
    private View[][] mViewsToAnimate;

    private View[][] mPendingViews;

    private int mNumInitiallyVisibleRows = -1;

    private int mNumColumns = -1;

    private int mTotalItems = -1;

    private int mInitialAnimationTimeIntervals = -1;

    private int mNumChildren = -1;

    private AutoFitRecyclerView mRecyclerView;

    private GridLayoutManager mManager;

    private boolean mFirstAnimationsCompleted = false;

    private long mAnimStartTime;

    private long mAnimEndTime;

    private long mTimeToCompleteScreenAnimation;

    private int mNumChildrenAttached = 0;

    private int mCurrentRowNumber = 0;

    private boolean mIsScrollingDown = true;

    private int mNumRows = -1;

    private int mTotalAnimationTimeIntervals;

    /**
     * Array to track whether or not each View has been animated
     *
     * ToDo use structs or an array of objects to better organize code
     */
    private boolean[][] mViewHasAnimated;

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

                        int firstItemPosition = mManager.findFirstVisibleItemPosition();
                        int lastItemPosition = mManager.findLastVisibleItemPosition();

                        Log.d("IMPORTANT", "First visible pos: " + firstItemPosition);
                        Log.d("IMPORTANT", "Last visible pos: " + lastItemPosition);

                        /**
                         * The reason why using lastItemPosition and firstItem position didn't work
                         * is because a View can be attached to the RecyclerView, but not visible
                         */
//                        mNumInitiallyVisibleRows = (int) Math.ceil(((double) (lastItemPosition - firstItemPosition) / (double) mNumColumns));
                        mNumInitiallyVisibleRows = (int) Math.ceil(((double) mNumChildren / (double) mNumColumns));

                        Log.d("Lustig", "Number of Rows: " + mNumInitiallyVisibleRows);

                        Log.d("Lustig", "Number of Children: " + mNumChildren);
                        Log.d("Lustig", "Children attached: " + mNumChildrenAttached);

                        Log.d("Lustig", "Last visible row (0 offset): " + (mNumInitiallyVisibleRows - 1));

                        mCurrentRowNumber = mNumInitiallyVisibleRows;

                        Log.d("Lustig", "First row to make a determination: " + mCurrentRowNumber);

                        Log.d("Lustig", "Number of Items: " + mTotalItems);

                        Log.d("Lustig", "Total rows: " + mNumRows);

                        mInitialAnimationTimeIntervals = mNumColumns + mNumInitiallyVisibleRows - 2;
                        mTotalAnimationTimeIntervals = mNumColumns + mNumRows - 2;

                        Log.d("Lustig", "Animation rows: " + mNumInitiallyVisibleRows);

                        Log.d("Lustig", "Animation columns: " + mNumColumns);

                        Log.d("Lustig", "Initial animation time intervals: " + mInitialAnimationTimeIntervals);
                        Log.d("Lustig", "Complete animation time intervals: " + mTotalAnimationTimeIntervals);

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

    // ToDo organize methods into helpers and other categories
    public void setRecyclerView(AutoFitRecyclerView recyclerView) {

        mRecyclerView = recyclerView;
    }


    private void animateInitiallyVisibleViews() {

        /**
         * This first for loop gets all current children that can be animated
         *
         * ToDo refactor this code to add children in onChildAttached
         */
        for (int row = 0; row < mNumInitiallyVisibleRows; ++row) {

            for (int column = 0; column < mNumColumns; ++column) {

//                Log.d("Lusti", "(row, column): (" + row + ", " + column + ")");

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
        for (int T = 0; T <= (mTotalAnimationTimeIntervals); T++) {

            for (
                    int row = 0, column = T;

                    row <= T && column >= 0;

                    row++, column--
                    ) {

                if (column < mNumColumns) {

                    /**
                     * If the row is visible in the initial viewing of the screen, go ahead and
                     * animate it.
                     */
                    if (row < mNumInitiallyVisibleRows) {

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

                    /*
                     * If the row is between the below bounds, we need to use a TimerTask
                     * to determine what to do with them.
                     *
                     * - - -Or I guess I should use a handler in exactly the same way I do above,
                     * just to call different logic.
                     */
                    } else if (mNumInitiallyVisibleRows <= row && row < mNumRows) {

                        Log.d("Complete Animation", "T = " + T + ":\t(" + row + ", " + column + ")");

                        final int handlerRow = row;
                        final int handlerColumn = column;

                        final int handlerTime = T;

                        final Handler handler = new Handler();

                        handler.postDelayed(
                                new Runnable() {

                                    @Override
                                    public void run() {

//                                        Log.d("Lusti", "(" + handlerRow + ", " + handlerColumn + ") should animate now at time " + handlerTime);

                                        final View v = mPendingViews[handlerRow][handlerColumn];

                                        if (v != null) {

                                            Log.d("Lusti", "Pending animation attempting now with " + handlerRow + ", " + handlerColumn);

                                            animateSingleView(v);
                                        }

                                        mViewHasAnimated[handlerRow][handlerColumn] = true;
                                    }

                                }, mDeltaT * T);
                    }
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
                }, mDeltaT * (mInitialAnimationTimeIntervals + 1)
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


    /**
     * In onChildAttached, I need to add the child to an array of rows and columns
     * to be able to bring an entire row into visibility or perform other animations.
     * @param child
     */
    @Override
    public void onChildAttached(View child) {

        Log.d("Lusti", "------------------------------------------------------------------");
        Log.d("Lusti", "----------------- NEW CHILD HAS BEEN ATTACHED! -------------------");
        Log.d("Lusti", "------------------------------------------------------------------");

        child.setVisibility(View.INVISIBLE);

        if (!mFirstAnimationsCompleted && (mPendingViews == null)) {

            mTotalItems = mRecyclerView.getAdapter().getItemCount();

            mNumColumns = mRecyclerView.getNumColumns();

            mNumRows = (int) Math.ceil((double) mTotalItems / (double) mNumColumns);

            Log.d("Lusti", "What data is available here?");
            Log.d("Lusti", "Rows: " + mNumRows);
            Log.d("Lusti", "Columns: " + mNumColumns);

            mPendingViews = new View[mNumRows][mNumColumns];
            mViewHasAnimated = new boolean[mNumRows][mNumColumns];
        }

        int childPositionInRow = mNumChildrenAttached % mNumColumns;

        if (!mIsScrollingDown) {
            mNumChildrenAttached--;
        } else {
            mNumChildrenAttached++;
        }

        mCurrentRowNumber = (int) (Math.ceil( (double) mNumChildrenAttached / (double) mNumColumns) - 1);

        Log.d("Lusti", "Number children attached: " + mNumChildrenAttached);
        Log.d("Lusti", "Zero offset child title: " + (mNumChildrenAttached - 1));

        Log.d("Lusti", "Child position: (" + mCurrentRowNumber + ", " + childPositionInRow + ") has attached");

        if (mPendingViews[mCurrentRowNumber][childPositionInRow] == null) {

            mPendingViews[mCurrentRowNumber][childPositionInRow] = child;

        }

        if (mViewHasAnimated[mCurrentRowNumber][childPositionInRow]) {
            Log.d("Lusti", "Child position: (" + mCurrentRowNumber + ", " + childPositionInRow + ") has animated");
            child.setVisibility(View.VISIBLE);
        }
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
