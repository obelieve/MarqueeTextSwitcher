package com.zxy.marqueetextswitcher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zxy.marqueetextswitcher.library.BuildConfig;
import com.zxy.marqueetextswitcher.library.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * MarqueeTextSwitcher
 * 跑马灯滚动 + 翻页切换 文本效果
 */
public class MarqueeTextSwitcher extends TextSwitcher {

    private static final String TAG = MarqueeTextSwitcher.class.getSimpleName();
    private static final int WHAT_SHOW_NEXT = 0;
    private static final int WHAT_SCROLL_CUR = 1;

    private static final int DEF_TEXT_DURATION = 3000;
    private static final int DEF_ANIM_DURATION = 1000;
    private static final int DEF_DELAYED_MARQUEE_DURATION = 3000;
    private static final int DEF_TEXT_SIZE = 14;
    private static final int DEF_TEXT_COLOR = Color.parseColor("#FF333333");
    private static final float DEF_STEP_SPEED = 1.5f;

    private List<String> mTextList;
    private int mTextSize;
    private int mTextColor;
    private int mTextDuration;
    private int mAnimDuration;
    private int mDelayedMarqueeDuration;
    private float mStepSpeed;
    private int mIndex = 0;

    private boolean mShowLog = BuildConfig.DEBUG;
    private boolean mInit = true;
    private boolean mStartRun;
    private Timer mTimer;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_SHOW_NEXT:
                    showNext();
                    String text = msg.obj.toString();
                    HorizontalScrollTextView tv = (HorizontalScrollTextView) getCurrentView();
                    tv.setTextContent(text);
                    tv.setText(text);
                    boolean scrollText = tv.needScrollText();
                    if (scrollText) {//多于一行
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                message.what = WHAT_SCROLL_CUR;
                                mHandler.sendMessage(message);
                            }
                        }, mDelayedMarqueeDuration);
                    } else {//少于一行
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                processShowNext();
                            }
                        }, mTextDuration);
                    }
                    break;
                case WHAT_SCROLL_CUR:
                    //滚动当前View
                    HorizontalScrollTextView tv2 = (HorizontalScrollTextView) getCurrentView();
                    tv2.startScroll();
                    break;
            }
        }
    };

    public MarqueeTextSwitcher(Context context) {
        this(context, null);
    }

    public MarqueeTextSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.MarqueeTextSwitcher);
        mTextDuration = typedArray.getInteger(R.styleable.MarqueeTextSwitcher_textDuration, DEF_TEXT_DURATION);
        mAnimDuration = typedArray.getInteger(R.styleable.MarqueeTextSwitcher_animDuration, DEF_ANIM_DURATION);
        mDelayedMarqueeDuration = typedArray.getInteger(R.styleable.MarqueeTextSwitcher_marqueeDelayedDuration, DEF_DELAYED_MARQUEE_DURATION);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.MarqueeTextSwitcher_textSize, DEF_TEXT_SIZE);
        mTextColor = typedArray.getColor(R.styleable.MarqueeTextSwitcher_textColor, DEF_TEXT_COLOR);
        mStepSpeed = typedArray.getFloat(R.styleable.MarqueeTextSwitcher_stepSpeed, DEF_STEP_SPEED);
        mStepSpeed = mStepSpeed > 0 ? mStepSpeed : DEF_STEP_SPEED;
        typedArray.recycle();
    }

    @Override
    public void setOutAnimation(Animation outAnimation) {
        if (outAnimation == null) {
            outAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, -1);
            outAnimation.setDuration(mAnimDuration);
            super.setOutAnimation(outAnimation);
        }
        super.setOutAnimation(outAnimation);
    }

    public void setShowLog(boolean debug) {
        mShowLog = debug;
    }

    public void setTextList(List<String> textList) {
        if (textList != null) {
            this.mTextList = textList;
        }
    }

    public void startRun() {
        mStartRun = true;
        if (mInit) {
            mInit = false;
            setFactory(null);
            setInAnimation(null);
            setOutAnimation(null);
        }
        start();
    }

    public void cancelRun() {
        mStartRun = false;
        release();
    }

    private void start() {
        mHandler.removeCallbacksAndMessages(null);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        processShowNext();
    }

    private void processShowNext() {
        if (mShowLog) {
            Log.i(TAG, "滚动下一行 执行processShowNext()");
        }
        TextView textView = (TextView) getCurrentView();
        if (textView == null) {
        } else {
            Message message = Message.obtain();
            message.what = WHAT_SHOW_NEXT;
            message.obj = mTextList.get(mIndex % mTextList.size());
            mHandler.sendMessage(message);
        }
        mIndex++;
    }

    private void release() {
        mHandler.removeCallbacksAndMessages(null);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mShowLog) {
            Log.i(TAG, "可见性 View：" + changedView + " visibility =" + (visibility == 0 ? "VISIBLE" : visibility == 8 ? "GONE" : "INVISIBLE"));
        }
        if (mStartRun) {
            if (visibility == View.VISIBLE) {
                release();
                start();
            } else {
                release();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelRun();
    }


    @Override
    public void setInAnimation(Animation inAnimation) {
        if (inAnimation == null) {
            inAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_PARENT, 0);
            inAnimation.setDuration(mAnimDuration);
            inAnimation.setFillAfter(true);
            super.setInAnimation(inAnimation);
        }
        super.setInAnimation(inAnimation);
    }

    @Override
    public void setFactory(ViewFactory factory) {
        if (factory == null) {
            factory = new ViewFactory() {
                @Override
                public View makeView() {
                    final HorizontalScrollTextView textView = new HorizontalScrollTextView(getContext());
                    textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    textView.setStepSpeed(mStepSpeed);
                    textView.setTextColor(mTextColor);
                    textView.setTextSize(mTextSize);
                    textView.setTextScrollListener(1, new HorizontalScrollTextView.onTextScrollListener() {
                        @Override
                        public void onReset(float x) {

                        }

                        @Override
                        public void onFinish() {
                            //滚动完 切换下一个
                            textView.stopScroll();
                            mTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    processShowNext();
                                }
                            }, mDelayedMarqueeDuration);
                        }
                    });
                    return textView;
                }
            };
            super.setFactory(factory);
        }
    }

    public void setStepSpeed(float stepSpeed) {
        mStepSpeed = stepSpeed > 0 ? stepSpeed : DEF_STEP_SPEED;
    }

    public void setContentTextColor(int color) {
        this.mTextColor = color;
    }

    public void setContentTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public void setTextDuration(int duration) {
        this.mTextDuration = duration;
    }

    public void setAnimDuration(int duration) {
        this.mAnimDuration = duration;
    }

}
