package com.xcc.mylibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义滚动选择器
 */
public class PickerScrollView extends View {
    public static final String TAG = "PickerView";
    /**
     * text之间间距和minTextSize之比
     */
    public static final float MARGIN_ALPHA = 3.0f;
    /**
     * 自动回滚到中间的速度
     */
    public static final float SPEED = 2;

    private List<Pickers> mDataList;
    private int listSize;
    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private int mCurrentSelected;
    private Paint mPaint;
    private Paint p;
    private float mMaxTextSize = 16;
    private float mMinTextSize = 12;
    private float maxTextScale = 13f;

    private float mMaxTextAlpha = 255;
    private float mMinTextAlpha = 120;

    private int mColorText = 0x333333;

    private int mViewHeight;
    private int mViewWidth;

    private float mLastDownY;
    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;
    private boolean isInit = false;
    private onSelectListener mSelectListener;
    private Timer timer;
    private MyTimerTask mTask;

    Handler updateHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (Math.abs(mMoveLen) < SPEED) {
                mMoveLen = 0;
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                    performSelect();
                }
            } else
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
            invalidate();
        }

    };

    public PickerScrollView(Context context) {
        super(context);
        init();
    }

    public PickerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnSelectListener(onSelectListener listener) {
        mSelectListener = listener;
    }

    private void performSelect() {
        if (mSelectListener != null) {
            if (mDataList == null || listSize == 0)
                mSelectListener.onSelect(this, null);
            else
                mSelectListener.onSelect(this, mDataList.get(mCurrentSelected));
        }
    }

    public void setData(List<Pickers> datas) {
        mDataList = datas;
        listSize = datas.size();
        mCurrentSelected = 0;
        invalidate();
        performSelect();
    }

    public void setSelectIdx(int selected) {
        setSelect(selected);
    }

    /**
     * 选择选中的item的index
     *
     * @param selected
     */
    public void setSelect(int selected) {
        mCurrentSelected = selected;
//        int distance = listSize / 2 - mCurrentSelected;
//        if (distance < 0)
//            for (int i = 0; i < -distance; i++) {
//                moveHeadToTail();
//                mCurrentSelected--;
//            }
//        else if (distance > 0)
//            for (int i = 0; i < distance; i++) {
//                moveTailToHead();
//                mCurrentSelected++;
//            }
        invalidate();
        performSelect();
    }

    /**
     * 选择选中的内容
     */
    public void setSelected(String mSelectItem) {
        for (int i = 0; i < listSize; i++)
            if (mDataList.get(i).equals(mSelectItem)) {
                setSelect(i);
                break;
            }
    }

    /**
     * 更改字体大小
     *
     * @param maxTextScale 8f-13f-16f
     */
    public void setMaxTextScale(float maxTextScale) {
        this.maxTextScale = maxTextScale;
    }

    // 向上滑
    private void moveHeadToTail() {
        if (listSize == 0)
            return;
        mCurrentSelected++;
        if (mCurrentSelected >= listSize)
            mCurrentSelected = 0;
    }

    // 向下滑
    private void moveTailToHead() {
        if (listSize == 0)
            return;
        mCurrentSelected--;
        if (mCurrentSelected < 0)
            mCurrentSelected = listSize - 1;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        // 按照View的高度计算字体大小
        mMaxTextSize = mViewHeight / maxTextScale;// 8f
        mMinTextSize = mViewHeight / 16f;
        isInit = true;
        invalidate();
    }

    private void init() {
        timer = new Timer();
        mDataList = new ArrayList<Pickers>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setColor(mColorText);
        p = new Paint();
        p.setStrokeWidth(2);
        p.setColor(0xffDCDCDC);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        if (mDataList != null)
            listSize = mDataList.size();
        if (isInit)
            drawData(canvas);
    }

    private void drawData(Canvas canvas) {
        // 先绘制选中的text再往上往下绘制其余的text
        float scale = parabola(mViewHeight / 4.0f, mMoveLen);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        float x = (float) (mViewWidth / 2.0);
        float y = (float) (mViewHeight / 2.0 + mMoveLen);
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        int indexs = mCurrentSelected;
        canvas.drawLine(0, mViewHeight * 0.4f, mViewWidth, mViewHeight * 0.4f,
                p);
        canvas.drawLine(0, mViewHeight * 0.6f, mViewWidth, mViewHeight * 0.6f,
                p);
        if (listSize == 0)
            return;
        String textData = mDataList.get(indexs).getShowConetnt();
        canvas.drawText(textData, x, baseline, mPaint);
        // 绘制上方data
        int max = listSize > 10 ? 5 : (listSize + 1) / 2;
        for (int i = 1; i < max; i++) {
            drawOtherText(canvas, i, -1);
        }
        if (listSize % 2 == 0)
            max++;
        // 绘制下方data
        for (int i = 1; i < max; i++) {
            drawOtherText(canvas, i, 1);
        }
    }

    /**
     * @param canvas
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        float d = (float) (MARGIN_ALPHA * mMinTextSize * position + type
                * mMoveLen);
        float scale = parabola(mViewHeight / 4.0f, d);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        float y = (float) (mViewHeight / 2.0 + type * d);
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        int indexs = mCurrentSelected + type * position;
        if (indexs < 0)
            indexs = indexs + listSize;
        else if (indexs >= listSize)
            indexs = indexs - listSize;
        String textData = mDataList.get(indexs).getShowConetnt();
        canvas.drawText(textData, (float) (mViewWidth / 2.0), baseline, mPaint);
    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     * @return scale
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                doMove(event);
                break;
            case MotionEvent.ACTION_UP:
                doUp(event);
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();
    }

    private void doMove(MotionEvent event) {
        mMoveLen += (event.getY() - mLastDownY);
        if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
            // 往下滑超过离开距离
            moveTailToHead();
            mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
        } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
            // 往上滑超过离开距离
            moveHeadToTail();
            mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
        }
        mLastDownY = event.getY();
        invalidate();
    }

    private void doUp(MotionEvent event) {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 0.0001) {
            mMoveLen = 0;
            return;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 10);
    }

    class MyTimerTask extends TimerTask {
        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }

    }

    public interface onSelectListener {
        void onSelect(PickerScrollView v, Pickers pickers);
    }
}
