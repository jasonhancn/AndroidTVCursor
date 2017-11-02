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
    // 两次加速之间的时间间隔
    private static final int defTimes = 400;
    // 移动的最高速度系数
    private static final int defMaxSpeed = 5;
    // 速度系数，每次的移动距离为移动步长*速度系数
    private int speedCoefficient = 1;
    // 鼠标指针大小的偏移量
    private int mOffsetX;
    private int mOffsetY;
    // 鼠标的当前位置
    private int mCursorX = MOUSE_START_X;
    private int mCursorY = MOUSE_START_Y;
    // 鼠标的前一位置
    private int mLastCursorX = mCursorX;
    private int mLastCursorY = mCursorY;
    // 是否已经显示了鼠标指针
    private boolean isShowCursor = false;
    // 上一次移动的执行时间
    private long mLastMoveTime;

    public TvCursorView(@NonNull Context context) {
        super(context);
    }

    // 设置用于传递点击事件的目标View（即传给鼠标View的Parent ViewGroup）
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

    // 用于转换素材到Bitmap（并统一尺寸）
    private Bitmap drawableToBitmap(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return Bitmap.createScaledBitmap(bitmap, 50, 50, true);
    }

    // 是否显示了鼠标指针
    boolean isShowCursor() {
        return isShowCursor;
    }

    // 设置鼠标指针的显示与否
    void setShowCursor(boolean is) {
        isShowCursor = is;
    }

    // 确保指针的大小正确
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCursorView != null && mCursorBitmap != null) {
            mCursorView.measure(MeasureSpec.makeMeasureSpec(mCursorBitmap.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mCursorBitmap.getHeight(), MeasureSpec.EXACTLY));
        }
    }

    // 放置鼠标指针的view
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mCursorView != null) {
            mCursorView.layout(mCursorX, mCursorY, mCursorX + mCursorView.getMeasuredWidth(), mCursorY + mCursorView.getMeasuredHeight());
        }
    }

    boolean onDpadClicked(KeyEvent event) {
        if (!isShowCursor) {
            return false;
        }
        if (event.getKeyCode() == KEYCODE_CENTER) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                sendMotionEvent(mCursorX + mOffsetX / 3, mCursorY + mOffsetY / 5, MotionEvent.ACTION_DOWN);
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                sendMotionEvent(mCursorX + mOffsetX / 3, mCursorY + mOffsetY / 5, MotionEvent.ACTION_UP);
            }
        } else {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                // 如果两次点击之间的事件间隔小于预先设定的事件间隔，进行线性加速
                if (event.getDownTime() - mLastMoveTime < defTimes) {
                    if (speedCoefficient < defMaxSpeed) {
                        speedCoefficient++;
                    }
                } else {
                    // 超过了时间间隔的话，就还原初始速度
                    speedCoefficient = 1;
                }
                mLastMoveTime = event.getDownTime();
                moveCursor(event, speedCoefficient);
            }
        }
        return true;
    }

    // 移动鼠标指针
    // 因为指针的素材并不是完全充满的，所以有一些位置上的调整，以保证指针尖端为点击的位置
    private void moveCursor(KeyEvent event, int times) {
        int mMoveDis = times * MOUSE_MOVE_STEP;
        switch (event.getKeyCode()) {
            case KEYCODE_UP:
                if (mCursorY - mMoveDis >= -mOffsetY / 5) {
                    mCursorY = mCursorY - mMoveDis;
                } else {
                    mCursorY = -mOffsetY / 5;
                }
                break;
            case KEYCODE_LEFT:
                if (mCursorX - mMoveDis > -mOffsetX / 3) {
                    mCursorX = mCursorX - mMoveDis;
                } else {
                    mCursorX = -mOffsetX / 3;
                }
                break;
            case KEYCODE_DOWN:
                if (mCursorY + mMoveDis < getMeasuredHeight() - mMoveDis - mOffsetY / 5) {
                    mCursorY = mCursorY + mMoveDis;
                } else {
                    mCursorY = getMeasuredHeight() - mOffsetY / 5;
                }
                break;
            case KEYCODE_RIGHT:
                if (mCursorX + mMoveDis < getMeasuredWidth() - mOffsetX / 3) {
                    mCursorX = mCursorX + mMoveDis;
                } else {
                    mCursorX = getMeasuredWidth() - mOffsetX / 3;
                }
                break;
        }
        if (mLastCursorX == mCursorX && mLastCursorY == mCursorY) {
            return;
        }
        mLastCursorX = mCursorX;
        mLastCursorY = mCursorY;
        requestLayout();
//        sendMotionEvent(mCursorX, mCursorY, MotionEvent.ACTION_HOVER_MOVE);
        // 把用于对齐指针所加的偏移量消除
        sendMotionEvent(mCursorX + mOffsetX / 3, mCursorY + mOffsetY / 5, MotionEvent.ACTION_MOVE);
    }

    // 发送鼠标的相关事件
    private void sendMotionEvent(int x, int y, int action) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        motionEvent.setSource(InputDevice.SOURCE_CLASS_POINTER);
        Log.d("Cursor Event", x + "-" + y + "-" + action);
        mTargetView.dispatchGenericMotionEvent(motionEvent);
//        // 鼠标的移动事件
//        if (action == MotionEvent.ACTION_MOVE) {
//            motionEvent.setSource(InputDevice.SOURCE_CLASS_POINTER);
//            Log.d("Cursor Move", x + "-" + y);
//            dispatchGenericMotionEvent(motionEvent);
//        } else {
//            // 鼠标的点击事件
//            Log.d("Mouse Click", x + "-" + y);
//            if (mTargetView != null) {
//                mTargetView.dispatchGenericMotionEvent(motionEvent);
//            }
//        }
    }
}