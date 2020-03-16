package com.dev.aurora.utils;

import android.view.animation.LinearInterpolator;

import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.AnimationSet;
import com.amap.api.maps.model.animation.ScaleAnimation;


public class AnimationUtils {

    public static AnimationSet getAMapMarkerAnimation() {
        AnimationSet animationSet = new AnimationSet(false);
        Animation scale = new ScaleAnimation(0f, 1f, 0f, 1f);
        Animation alpha = new AlphaAnimation(0f, 1f);
        animationSet.addAnimation(alpha);
        animationSet.addAnimation(scale);
        animationSet.setDuration(300);
        animationSet.setInterpolator(new LinearInterpolator());

        return animationSet;
    }
}
