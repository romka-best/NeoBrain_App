package com.itschool.neobrain;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import java.util.Objects;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

public class NeoIntro extends IntroActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                .title(R.string.profile_for_intro)
                .description(R.string.step_profile)
                .image(R.drawable.smiling_face_with_heart_shaped_eyes)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.apps_for_intro)
                .description(R.string.step_apps)
                .image(R.drawable.radio)
                .background(R.color.colorPrimaryDark)
                .backgroundDark(R.color.colorPrimary)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.chats_for_intro)
                .description(R.string.step_chats)
                .image(R.drawable.speech_balloon)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.covid_for_intro)
                .description(R.string.step_covid)
                .image(R.drawable.care_new_emoji_react)
                .background(R.color.colorPrimaryDark)
                .backgroundDark(R.color.colorPrimary)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.lenta_for_intro)
                .description(R.string.step_lenta)
                .image(R.drawable.compass)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.finish_intro)
                .description(R.string.step_finish)
                .image(R.drawable.heavy_black_heart)
                .background(R.color.colorPrimaryDark)
                .backgroundDark(R.color.colorPrimary)
                .scrollable(false)
                .build());

        addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override public void onPageSelected(int position) {
                if(position == 7){
                    SharedPreferences sp = Objects.requireNonNull(MainActivity.getContextOfApplication()).getSharedPreferences(MY_SETTINGS,
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("watchedIntro", true);
                    editor.apply();
                }
            }
            @Override public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
