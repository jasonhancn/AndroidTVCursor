package com.github.jasonhancn.tvcursor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

class TvCursorView extends FrameLayout {
    private ImageView mCursorView;
    private Bitmap mCursorBitmap;
    private View mTargetView;
    // 操作对应的按键事件
    private static final int KEYCODE_UP = KeyEvent.KEYCODE_DPAD_UP;
    private static final int KEYCODE_DOWN = KeyEvent.KEYCODE_DPAD_DOWN;
    private static final int KEYCODE_LEFT = KeyEvent.KEYCODE_DPAD_LEFT;
    private static final int KEYCODE_RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT;
    private static final int KEYCODE_CENTER = KeyEvent.KEYCODE_DPAD_CENTER;
    // 鼠标的起始位置
    private static final int MOUSE_START_X = 250;
    private static final int MOUSE_START_Y = 350;
    // 鼠标的移动步长
    private static final int MOUSE_MOVE_STEP = 20;
    // 鼠标指针大小的偏移量
    private int mOffsetX;
    private int mOffsetY;
    // 鼠标的当前位置
    private int mCursorX = MOUSE_START_X;
    private int mCursorY = MOUSE_START_Y;
    // 鼠标的前一位置
    private int mLastCursorX = mCursorX;
    private int mLastCursorY = mCursorY;

    private static final int defTimes = 400;
    private static final int defMaxSpeed = 7;

    private boolean isShowCursor = false;
    private boolean isKeyEventConsumed = false;
    private int mSpeed = 1;
    private long mLastEventTime;

    public TvCursorView(@NonNull Context context) {
        super(context);
    }

    TvCursorView(Context context, View targetView) {
        super(context);
        mTargetView = targetView;
        Drawable drawable = getResources().getDrawable(R.mipmap.shubiao);
        mCursorBitmap = drawableToBitmap(drawable);
        mCursorView = new ImageView(getContext());
        mCursorView.setImageBitmap(mCursorBitmap);
        addView(mCursorView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mOffsetX = mCursorBitmap.getWidth();
        mOffsetY = mCursorBitmap.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCursorView != null && mCursorBitmap != null) {
            mCursorView.measure(MeasureSpec.makeMeasureSpec(mCursorBitmap.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mCursorBitmap.getHeight(), MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mCursorView != null) {
            mCursorView.layout(mCursorX, mCursorY, mCursorX + mCursorView.getMeasuredWidth(), mCursorY + mCursorView.getMeasuredHeight());
        }
    }

    boolean isShowCursor() {
        return isShowCursor;
    }

    void setShowCursor(boolean is) {
        isShowCursor = is;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return Bitmap.createScaledBitmap(bitmap, 50, 50, true);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 除了已经定义的事件之外，抛出其他事件
        Log.i("dispatchKeyEvent", "action=" + event.getAction() + " keycode=" + event.getKeyCode());
        switch (event.getKeyCode()) {
            case KEYCODE_UP:
            case KEYCODE_DOWN:
            case KEYCODE_LEFT:
            case KEYCODE_RIGHT:
            case KEYCODE_CENTER:
                if (isShowCursor()) {
                    return onDpadClicked(event);
                }
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    boolean onDpadClicked(KeyEvent event) {
        if (!isShowCursor) {
            return false;
        }
        if (event.getKeyCode() == KEYCODE_CENTER) {
            Log.d("click", "center");
//                sendMotionEvent(mCursorX + mOffsetX, mCursorY + mOffsetY, event.getAction());
            sendMotionEvent(mCursorX, mCursorY, event.getAction());
        } else {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (!isKeyEventConsumed) {
                    if (event.getDownTime() - mLastEventTime < defTimes) {
                        if (mSpeed < defMaxSpeed) {
                            mSpeed++;
                        }
                    } else {
                        mSpeed = 1;
                    }
                }
                mLastEventTime = event.getDownTime();
                dispatchKeyEventToCursor(event);
                isKeyEventConsumed = true;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                if (!isKeyEventConsumed) {
                    dispatchKeyEventToCursor(event);
                }
                isKeyEventConsumed = false;
            }
        }
        return true;
    }

    private void dispatchKeyEventToCursor(KeyEvent event) {
        if (event.getKeyCode() == KEYCODE_CENTER) {
            sendMotionEvent(mCursorX + mOffsetX, mCursorY + mOffsetY, event.getAction());
        } else {
            moveCursor(event, mSpeed);
        }
    }

    private void moveCursor(KeyEvent event, int times) {
        int mMoveDis = times * MOUSE_MOVE_STEP;
        switch (event.getKeyCode()) {
            case KEYCODE_UP:
                if (mCursorY - mMoveDis >= 0) {
                    mCursorY = mCursorY - mMoveDis;
                } else {
                    mCursorY = 0;
                }
                break;
            case KEYCODE_LEFT:
                mCursorX = (mCursorX - mMoveDis > 0) ? mCursorX - mMoveDis : 0;
                break;
            case KEYCODE_DOWN:
                if (mCursorY + mMoveDis < getMeasuredHeight() - mMoveDis) {
                    mCursorY = mCursorY + mMoveDis;
                } else {
                    mCursorY = getMeasuredHeight() - mOffsetY;
                }
                break;
            case KEYCODE_RIGHT:
                mCursorX = (mCursorX + mMoveDis < getMeasuredWidth() - mOffsetX) ? mCursorX + mMoveDis : getMeasuredWidth() - mOffsetX;
                break;
        }
        if (mLastCursorX == mCursorX && mLastCursorY == mCursorY) {
            return;
        }
        mLastCursorX = mCursorX;
        mLastCursorY = mCursorY;
        requestLayout();
        sendMotionEvent(mCursorX + mOffsetX, mCursorY + mOffsetY, MotionEvent.ACTION_HOVER_MOVE);
    }

    private void sendMotionEvent(int x, int y, int action) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        if (action == MotionEvent.ACTION_HOVER_MOVE) {
            motionEvent.setSource(InputDevice.SOURCE_CLASS_POINTER);
            Log.d("GenericMotionEvent", x + "-" + y);
            dispatchGenericMotionEvent(motionEvent);
        } else {
            Log.d("TouchEvent", x + "-" + y);
            if (mTargetView != null) {
                mTargetView.dispatchTouchEvent(motionEvent);
            }
        }
    }
}