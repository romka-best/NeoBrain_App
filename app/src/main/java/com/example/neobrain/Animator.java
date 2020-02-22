package com.example.neobrain;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;

public class Animator extends AnimatorChangeHandler {
    @NonNull
    @Override
    protected android.animation.Animator getAnimator(@NonNull ViewGroup container,
                                                     @Nullable View from,
                                                     @Nullable View to,
                                                     boolean isPush,
                                                     boolean toAddedToContainer) {

        if (to == null){
            throw new IllegalArgumentException("123");
        }
        AnimatorSet allAnim = new AnimatorSet();
        androidx.constraintlayout.widget.ConstraintLayout detailView = (androidx.constraintlayout.widget.ConstraintLayout) to;
        Button btn_reg = (Button) detailView.findViewById(R.id.regButton);
        ObjectAnimator regAnim =
                ObjectAnimator.ofFloat(btn_reg,
                        View.TRANSLATION_Y,
                        -btn_reg.getHeight(),
                        0);
        ObjectAnimator hideFromViewAnim = ObjectAnimator.ofFloat(from, View.ALPHA,
                1, 0);
        allAnim.playTogether(hideFromViewAnim,
                regAnim);
        allAnim.setDuration(1000);
        allAnim.setInterpolator(new FastOutSlowInInterpolator());

        allAnim.start();

        return allAnim;
    }

    @Override
    protected void resetFromView(@NonNull View from) {
        from.setAlpha(1);
    }
}
