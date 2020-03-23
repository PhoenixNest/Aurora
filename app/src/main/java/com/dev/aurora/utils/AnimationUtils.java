package com.dev.aurora.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.AnimationSet;
import com.amap.api.maps.model.animation.ScaleAnimation;


public class AnimationUtils {

    private static AnimationUtils animationUtils;

    public static AnimationUtils getInstance() {
        if (animationUtils == null) {
            animationUtils = new AnimationUtils();
        }

        return animationUtils;
    }

    public AnimationSet getAMapMarkerAnimation() {
        AnimationSet animationSet = new AnimationSet(false);
        Animation scale = new ScaleAnimation(0f, 1f, 0f, 1f);
        Animation alpha = new AlphaAnimation(0f, 1f);
        animationSet.addAnimation(alpha);
        animationSet.addAnimation(scale);
        animationSet.setDuration(300);
        animationSet.setInterpolator(new LinearInterpolator());

        return animationSet;
    }

    public void alphaSuddenHideAnimation(View targetView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "alpha", 1f, 0f);
        animator.setDuration(0);
        animator.start();
    }

    public void showAlphaAnimation(View targetView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "alpha", 0f, 1f);
        animator.setDuration(500);
        animator.start();
    }
}
