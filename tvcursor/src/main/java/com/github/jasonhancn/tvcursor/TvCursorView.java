package com.github.jasonhancn.tvcursor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    // 鼠标指针尖端的相对于素材左上角的偏移量
    private int mPointerX;
    private int mPointerY;
    // 是否已经显示了鼠标指针
    private boolean isShowCursor = false;
    // 上一次移动的执行时间
    private long mLastMoveTime;
    // 是否执行拖拽操作
    private boolean isDragMode = false;
    // 需要滚动的view
    private View scrollTargetView = null;
    // 鼠标的资源文件
    private int pointerResource = R.mipmap.shubiao;
    // 鼠标的大小
    private int pointerSize = 50;

    public void setScrollTargetView(View scrollTargetView) {
        this.scrollTargetView = scrollTargetView;
    }

    public void setPointerResource(int pointerResource, int mPointerX, int mPointerY, int pointerSize) {
        this.pointerResource = pointerResource;
        this.mPointerX = mPointerX;
        this.mPointerY = mPointerY;
        setPointerSize(pointerSize);
    }

    public void setPointerSize(int pointerSize) {
        this.pointerSize = pointerSize;
        setCursorView();
        requestLayout();
    }

    public TvCursorView(@NonNull Context context) {
        super(context);
    }

    // 设置用于传递点击事件的目标View（即传给鼠标View的Parent ViewGroup）
    TvCursorView(Context context, View targetView) {
        super(context);
        mTargetView = targetView;
        mCursorView = new ImageView(getContext());
        addView(mCursorView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setCursorView();
        mPointerX = mOffsetX / 3;
        mPointerY = mOffsetY / 5;
    }

    // 用于转换素材到Bitmap（并统一尺寸）
    private Bitmap drawableToBitmap() {
        Resources resources;
        if (pointerResource == R.mipmap.shubiao) {
            resources = getResources();
        } else {
            resources = mTargetView.getContext().getResources();
        }
        Bitmap bitmap = BitmapFactory.decodeResource(resources, pointerResource);
        return Bitmap.createScaledBitmap(bitmap, pointerSize, pointerSize, true);
    }

    private void setCursorView() {
        mCursorBitmap = drawableToBitmap();
        mCursorView.setImageBitmap(mCursorBitmap);
        mOffsetX = mCursorBitmap.getWidth();
        mOffsetY = mCursorBitmap.getHeight();
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
            // 添加偏移量，让指针尖端对准实际位置（图片的左上角）
            mCursorView.layout(
                    mCursorX - mPointerX,
                    mCursorY - mPointerY,
                    mCursorX + mOffsetX - mPointerX,
                    mCursorY + mOffsetY - mPointerY
            );
        }
    }

    boolean onDpadClicked(KeyEvent event) {
        if (!isShowCursor) {
            return false;
        }
        if (event.getKeyCode() == KEYCODE_CENTER) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                // 按下确定键进入拖动模式（长按相当于不断点击，会维持拖动模式）
                isDragMode = true;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                // 如果在指定时间内抬起，则认为是一次点击
                if (event.getDownTime() - mLastMoveTime > defTimes / 2) {
                    isDragMode = false;
                    sendMotionEvent(mCursorX, mCursorY, MotionEvent.ACTION_DOWN);
                    sendMotionEvent(mCursorX, mCursorY, MotionEvent.ACTION_UP);
                }
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
                // 如果是拖动的情况，就模拟拖动的动作，反之仅移动指针
                if (isDragMode) {
                    sendMotionEvent(mCursorX, mCursorY, MotionEvent.ACTION_DOWN);
                    moveCursor(event, speedCoefficient);
                    sendMotionEvent(mCursorX, mCursorY, MotionEvent.ACTION_UP);
                    isDragMode = false;
                } else {
                    moveCursor(event, speedCoefficient);
                }
            }
        }
        return true;
    }

    // 移动鼠标指针
    private void moveCursor(KeyEvent event, int times) {
        int mMoveDis = times * MOUSE_MOVE_STEP;
        switch (event.getKeyCode()) {
            case KEYCODE_UP:
                if (mCursorY - mMoveDis >= 0) {
                    mCursorY = mCursorY - mMoveDis;
                } else {
                    mCursorY = 0;
                    if (scrollTargetView != null) {
                        scrollTargetView.scrollBy(0, -mMoveDis);
                    }
                }
                break;
            case KEYCODE_LEFT:
                if (mCursorX - mMoveDis > 0) {
                    mCursorX = mCursorX - mMoveDis;
                } else {
                    mCursorX = 0;
                }
                break;
            case KEYCODE_DOWN:
                if (mCursorY + mMoveDis < getMeasuredHeight()) {
                    mCursorY = mCursorY + mMoveDis;
                } else {
                    mCursorY = getMeasuredHeight();
                    if (scrollTargetView != null) {
                        scrollTargetView.scrollBy(0, mMoveDis);
                    }
                }
                break;
            case KEYCODE_RIGHT:
                if (mCursorX + mMoveDis < getMeasuredWidth()) {
                    mCursorX = mCursorX + mMoveDis;
                } else {
                    mCursorX = getMeasuredWidth();
                }
                break;
        }
        if (mLastCursorX == mCursorX && mLastCursorY == mCursorY) {
            return;
        }
        mLastCursorX = mCursorX;
        mLastCursorY = mCursorY;
        requestLayout();
        sendMotionEvent(mCursorX, mCursorY, MotionEvent.ACTION_MOVE);
    }

    // 发送鼠标的相关事件
    private void sendMotionEvent(int x, int y, int action) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        motionEvent.setSource(InputDevice.SOURCE_MOUSE);
        Log.d("Cursor Event", x + "-" + y);
        mTargetView.dispatchTouchEvent(motionEvent);
    }
}