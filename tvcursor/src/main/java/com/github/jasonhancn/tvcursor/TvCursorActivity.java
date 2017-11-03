package com.github.jasonhancn.tvcursor;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

@SuppressLint("Registered")
public class TvCursorActivity extends AppCompatActivity {
    private TvCursorManager tvCursorManager;
    private ViewGroup contentView;
    private boolean isIMEMode = false;
    private boolean ready = false;

    @SuppressLint("InflateParams")
    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.full, null);
        contentView = (ViewGroup) inflater.inflate(layoutResID, viewGroup);
        contentView.requestDisallowInterceptTouchEvent(true);
        setContentView(contentView);
        initCursor();
    }

    // 判断是否弹出了输入法框
    // 当失去焦点时，如果显示部分和屏幕本身的高度差大于100，则视为弹出了输入法框
    // 当弹出输入法框，停止转发Dpad的按键事件
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

    public void showCursor() {
        tvCursorManager.setShowCursor(true);
    }

    public void hideCursor() {
        tvCursorManager.setShowCursor(false);
    }

    public boolean isShowCursor() {
        return tvCursorManager.isShowCursor();
    }

    public void setScrollTargetView(View view) {
        tvCursorManager.setScrollTargetView(view);
    }

    public void setCursorSize(int size) {
        tvCursorManager.setCursorSize(size);
    }

    public void setCursorResource(int pointerResource, int pointerSize, int pointerX, int pointerY) {
        tvCursorManager.setCursorResource(pointerResource, pointerSize, pointerX, pointerY);
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
