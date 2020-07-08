package com.itschool.neobrain;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.itschool.neobrain.R;

public class NeoIntro extends IntroActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO

        setFullscreen(true);
        setButtonBackFunction(BUTTON_BACK_FUNCTION_SKIP);
        setButtonNextFunction(BUTTON_NEXT_FUNCTION_NEXT_FINISH);
        setButtonBackVisible(true);
        setButtonNextVisible(true);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.neohello)
                .description(R.string.neodescription)
                .image(R.drawable.woman_and_man_holding_hands_medium_light_skin_tone_dark_skin_tone)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.reg)
                .description(R.string.step_registration)
                .image(R.drawable.thinking_face)
                .background(R.color.colorPrimaryDark)
                .backgroundDark(R.color.colorPrimary)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.deathsPerOneMillion)
                .image(R.drawable.woman_and_man_holding_hands_medium_light_skin_tone_dark_skin_tone)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build());

//        setNavigationPolicy(new NavigationPolicy() {
//            @Override public boolean canGoForward(int position) {
//                return true;
//            }
//
//            @Override public boolean canGoBackward(int position) {
//                return false;
//            }
//        });
    }
}
