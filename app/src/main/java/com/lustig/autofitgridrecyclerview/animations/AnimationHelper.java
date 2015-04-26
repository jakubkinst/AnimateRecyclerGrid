package com.lustig.autofitgridrecyclerview.animations;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;

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

    public static final long DEFAULT_DURATION_MS = 200;

    private static final String DEFAULT_PROPERTY_TO_ANIMATE = "alpha";

    /* Type of property of View being animated */
    private String mViewPropertyToAnimate = DEFAULT_PROPERTY_TO_ANIMATE;

    /* Duration of animations, set to default value, but can be changed */
    private long mAnimationDuration = DEFAULT_DURATION_MS;

    /* Value to determine if an animation is currently occurring */
    private boolean isCurrentlyAnimating = false;

    /* First, I need an animation queue. I should use a generic view, I'll try that first */
    private ArrayList<View> mViewsToAnimate = new ArrayList<View>();

    /* Next I'll need a method to add to the animation queue */
    public void addViewToQueue(View viewToAnimate) {

        /**
         * If I've already animated the view, don't do it again
         */
        if (viewToAnimate.getVisibility() == View.VISIBLE) {
            return;
        }

        mViewsToAnimate.add(viewToAnimate);

        /* This method is the meat and potatoes of this class */
        startAnimationChain();
    }

    /* This method will be in charge of starting the domino effect */
    private void startAnimationChain() {

        /* If there is currently not an animation happening, start one */
        if (mViewsToAnimate.size() >= 2 && !isCurrentlyAnimating) {

            animateSingleView(mViewsToAnimate.get(0));

            /**
             * If we are currently animating, just wait. The next view animation should
             * automatically be spawned
             */
        } else if (isCurrentlyAnimating) {

            // Just wait, animations should continue until the list is empty

        }

    }

    private void animateSingleView(final View v) {

        /* Right now, using a single animation, will change in the future */
        ObjectAnimator animator =
                ObjectAnimator.ofFloat(v, mViewPropertyToAnimate, 0f, 1f);

        animator.setDuration(mAnimationDuration);

        animator.addListener(
                new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                        v.setVisibility(View.VISIBLE);

                        /* Remove the currently animating View from the list */
                        mViewsToAnimate.remove(v);

                        /* Notify the class Object that an animation is happening */
                        isCurrentlyAnimating = true;

                        /**
                         * If, after removing the currently animating view, the list is not empty,
                         * start the next animation after the current animation is halfway done.
                         */
                        if (!mViewsToAnimate.isEmpty()) {

                            /* Set a timer for (mAnimationDuration / 2) ms to start next animation */
                            final Handler handler = new Handler();

                            handler.postDelayed(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        // Animate the first item in the list because each time
                                        // an animation starts, it is removed from the list
                                        if (!mViewsToAnimate.isEmpty()) {
                                            animateSingleView(mViewsToAnimate.get(0));
                                        }
                                    }
                                }, mAnimationDuration / 6);
                        }
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        /**
                         * Setting this boolean flag could potentially cause issues as I'm going
                         * to have to use a Runnable to wait some time before starting the next
                         * animation. If there are any bugs, come back here, debug, and make sure
                         * that this flag is behaving as expected
                         */

                        /* Notify the class Object that the current animation has finished */
                        isCurrentlyAnimating = false;

                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // Ignored intentionally
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        // Ignored intentionally
                    }
                });

        animator.start();

    }

}
