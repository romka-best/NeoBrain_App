package com.example.neobrain.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.example.neobrain.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FAB_Float_on_Scroll extends FloatingActionButton.Behavior {

    public FAB_Float_on_Scroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && child.getVisibility() != View.INVISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(child.getContext(), R.anim.alpha_to_zero);
            child.startAnimation(animation);
            child.setVisibility(View.INVISIBLE);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(child.getContext(), R.anim.alpha_to_one);
            child.startAnimation(animation);
            child.setVisibility(View.VISIBLE);
        }
        setAutoHideEnabled(true);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}