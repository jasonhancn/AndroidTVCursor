package com.github.jasonhancn.tvcursor;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

@SuppressLint("Registered")
public class TvCursorActivity extends AppCompatActivity{
    private TvCursorManager tvCursorManager;
    private ViewGroup contentView;
    private boolean isIMEMode = false;
    private boolean ready = false;

    @SuppressLint("InflateParams")
    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = getLayoutInflater();
        contentView = (ViewGroup) inflater.inflate(layoutResID, null);
        setContentView(contentView);
        initCursor();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!ready && hasFocus) {
            contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = contentView.getRootView().getHeight() - contentView.getHeight();
                    isIMEMode = heightDiff > 100;
                }
            });
            ready = true;
        }
    }

    private void initCursor() {
        tvCursorManager = new TvCursorManager(contentView);
        tvCursorManager.showCursorView();
    }

    public void showCursor(){
        tvCursorManager.setShowCursor(true);
    }

    public void hideCursor(){
        tvCursorManager.setShowCursor(false);
    }

    public boolean isShowCursor(){
        return tvCursorManager.isShowCursor();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (tvCursorManager != null && tvCursorManager.isShowCursor() && !isIMEMode) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN ||
                    event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP ||
                    event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT ||
                    event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT ||
                    event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER)
                return tvCursorManager.onDpadClicked(event);
        }
        return super.dispatchKeyEvent(event);
    }
}
