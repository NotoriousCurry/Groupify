package com.tssssa.sgaheer.groupify;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by sgaheer on 16/05/2016.
 */
public class AnimateLevel extends Animation {
    private UserLevelCircle circle;

    private float oldAngle;
    private float newAngle;

    public AnimateLevel(UserLevelCircle circle, int newAngle) {
        this.oldAngle = circle.getAngle();
        this.newAngle = newAngle;
        this.circle = circle;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime);

        circle.setAngle(angle);
        circle.requestLayout();
    }
}
