package com.zxy.marqueetextswitcher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.zxy.marqueetextswitcher.library.R;

/**
 * Created by Admin
 * on 2020/7/21
 */
public class HorizontalScrollTextView extends AppCompatTextView {

    private static final float DEF_STEP_SPEED = 1.5f;

    private float textLength = 0f;// 文本长度
    private float step = 0f;// 文字的横坐标

    private float temp_view_plus_text_length = 0.0f;// 用于计算的临时变量
    private float temp_view_plus_two_text_length = 0.0f;// 用于计算的临时变量
    public boolean isStarting = false;// 是否开始滚动
    private Paint paint = null;// 绘图样式
    private String mTextContent = "";// 文本内容
    private onTextScrollListener mListener;
    private int mTimes = 1;//次数
    private int mTimesCount;
    private boolean mFirstInit = true;
    private float mStepSpeed;


    public HorizontalScrollTextView(Context context) {
        this(context, null);
    }

    public HorizontalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.HorizontalScrollTextView);
        mStepSpeed = typedArray.getFloat(R.styleable.HorizontalScrollTextView_stepSpeed, DEF_STEP_SPEED);
        mStepSpeed = mStepSpeed > 0 ? mStepSpeed : DEF_STEP_SPEED;
        typedArray.recycle();
    }


    private void init(String content) {
        int color = getTextColors().getColorForState(getDrawableState(), 0);
        paint = getPaint();
        //设置滚动字体颜色
        paint.setColor(color);
        mTextContent = content;
        textLength = paint.measureText(mTextContent);
        float viewWidth = getWidth();
        if (viewWidth == 0) {
            viewWidth = getResources().getDisplayMetrics().widthPixels;
        }
        step = textLength;
        temp_view_plus_text_length = textLength;//viewWidth + textLength
        temp_view_plus_two_text_length = textLength * 2 - viewWidth;//viewWidth + textLength * 2
    }

    /**
     *
     * @param stepSpeed  大于0，小于0 默认是1.5
     */
    public void setStepSpeed(float stepSpeed) {
        mStepSpeed = stepSpeed > 0 ? stepSpeed : DEF_STEP_SPEED;
    }

    public float getStepSpeed() {
        return mStepSpeed;
    }

    /**
     * 重置
     */
    public void resetStep() {
        step = textLength;
        invalidate();
    }

    /**
     * 是否需要滚动文本
     *
     * @return
     */
    public boolean needScrollText() {
        float textWidth = getPaint().measureText(mTextContent);
        if (textWidth > getMeasuredWidth()) {
            return true;
        }
        return false;
    }

    /**
     * 设置文本内容
     *
     * @param textContent
     */
    public void setTextContent(String textContent) {
        mTextContent = textContent;
        init(mTextContent);
        invalidate();
    }

    /**
     * 从起始位置滚动
     */
    public void reStartScroll() {
        step = textLength;
        isStarting = true;
        invalidate();
    }

    public void startScroll() {
        isStarting = true;
        invalidate();
    }

    public void stopScroll() {
        isStarting = false;
        invalidate();
    }

    public boolean getHasStarting() {
        return isStarting;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.step = step;
        ss.isStarting = isStarting;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        step = ss.step;
        isStarting = ss.isStarting;
    }

    private static class SavedState extends BaseSavedState {
        boolean isStarting = false;
        float step = 0.0f;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBooleanArray(new boolean[]{isStarting});
            out.writeFloat(step);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
        };

        private SavedState(Parcel in) {
            super(in);
            boolean[] b = null;
            in.readBooleanArray(b);
            step = in.readFloat();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mFirstInit) {
            mFirstInit = false;
            init(getText().toString());
        }
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        float textBaseY = getHeight() - (getHeight() - fontHeight) / 2 - fontMetrics.bottom;
        canvas.drawText(mTextContent, temp_view_plus_text_length - step, textBaseY, paint);
        if (!isStarting) {
            return;
        }
        step += mStepSpeed;// 文字滚动速度。
        if (step > temp_view_plus_two_text_length) {
            if (mTimesCount <= mTimes) {
                if (mListener != null)
                    mListener.onFinish();
            }
            mTimes++;
        } else {
            if (mListener != null)
                mListener.onReset(temp_view_plus_text_length - step);
        }
        invalidate();
    }

    public interface onTextScrollListener {
        void onReset(float x);

        void onFinish();
    }

    /**
     * 监听滚动次数并监听滚动结束
     *
     * @param times    滚动次数
     * @param listener 文字滚动监听
     */
    public void setTextScrollListener(int times, onTextScrollListener listener) {
        mTimesCount = times;
        mListener = listener;
    }
}
