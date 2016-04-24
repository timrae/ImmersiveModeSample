/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.immersive;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ImmersiveActivity extends AppCompatActivity {
    private static final int INITIAL_HIDE_DELAY = 300;
    private static final boolean USE_FULLSCREEN = true;

    /** Change USE_LEGACY to true if you want to use the original layout file without
     * the appcompat toolbar. To fully replicate the behavior in the original sample,
     * you'll also need to extend from Activity instead of AppCompatActivity, set the
     * theme to ImmersiveThemeLegacy instead of ImmersiveTheme, and comment out the
     * get/set supportActionBar method calls.
     */
    private static final boolean USE_LEGACY = false;
    private View mDecorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (USE_LEGACY) {
            setContentView(R.layout.immersive_activity_legacy);
        } else {
            setContentView(R.layout.immersive_activity);
        }
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        mDecorView = getWindow().getDecorView();
        mDecorView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int flags) {
                        boolean visible = (flags & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                        controlsView.animate()
                                .alpha(visible ? 1 : 0)
                                .translationY(visible ? 0 : controlsView.getHeight());
                        if (getSupportActionBar() == null) {
                            return;
                        }
                        if (visible) {
                            getSupportActionBar().show();
                        } else {
                            getSupportActionBar().hide();
                        }
                    }
                });
        contentView.setClickable(true);
        final GestureDetector clickDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        if (!USE_FULLSCREEN) {
                            return true;
                        }
                        boolean visible = (mDecorView.getSystemUiVisibility()
                                & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                        if (visible) {
                            hideSystemUI();
                        } else {
                            showSystemUI();
                        }
                        return true;
                    }
                });
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return clickDetector.onTouchEvent(motionEvent);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!USE_FULLSCREEN) {
            return;
        }
        // When the window loses focus (e.g. the action overflow is shown),
        // cancel any pending hide action. When the window gains focus,
        // hide the system UI.
        if (hasFocus) {
            delayedHide(INITIAL_HIDE_DELAY);
        } else {
            mHideHandler.removeMessages(0);
        }
    }

    private void hideSystemUI() {
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private final Handler mHideHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            hideSystemUI();
        }
    };

    private void delayedHide(int delayMillis) {
        mHideHandler.removeMessages(0);
        mHideHandler.sendEmptyMessageDelayed(0, delayMillis);
    }
}
