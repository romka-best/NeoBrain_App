package com.example.neobrain.Controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.R;
import com.example.neobrain.changehandler.ScaleFadeChangeHandler;
import com.example.neobrain.widget.ElasticDragDismissFrameLayout;

import butterknife.ButterKnife;

public class PhotoController extends Controller {
    private final ElasticDragDismissFrameLayout.ElasticDragDismissCallback dragDismissListener = new ElasticDragDismissFrameLayout.ElasticDragDismissCallback() {
        public void onDragDismissed() {
            overridePopHandler(new ScaleFadeChangeHandler());
            getRouter().popController(PhotoController.this);
        }
    };

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.photo_controller, container, false);
        ButterKnife.bind(this, view);
        onViewBound(view);
        return view;
    }

    protected void onViewBound(@NonNull View view) {
        ((ElasticDragDismissFrameLayout) view).addListener(dragDismissListener);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);

        ((ElasticDragDismissFrameLayout) view).removeListener(dragDismissListener);
    }
}
