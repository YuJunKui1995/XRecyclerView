package com.example.xrecyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import com.jcodecraeer.xrecyclerview.ArrowRefreshHeader;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

/**
 * Author: YuJunKui
 * Time:2018/11/29 18:14
 * Tips:
 */
public class DynamicDetailVideoHeader extends ArrowRefreshHeader {


    private int minHeight, maxHeight;

    private ViewConfiguration vc;
    private int mMinFlingVelocity, mMaxFlingVelocity;

    public DynamicDetailVideoHeader(Context context) {
        super(context);
    }

    public DynamicDetailVideoHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initView() {

        minHeight = (int) dpToPx(getContext(), 200);
        maxHeight = getDefaultHeight(getContext());

        mContainer = (ViewGroup) LayoutInflater.from(getContext()).inflate(
                R.layout.x_dynamic_detail_video, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, minHeight));


        vc = ViewConfiguration.get(getContext());
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
    }

    @Override
    public void setState(int state) {
    }

    @Override
    public boolean releaseAction() {
        return true;
    }

    private static final String TAG = "DynamicDetailVideoHeade";

    @Override
    public boolean onMove(float deltaY) {

        mState = STATE_NORMAL;
        deltaY = deltaY * XRecyclerView.DRAG_RATE * 1.5f;

        //低于最低高度并且还想缩小的
        if (minHeight >= getHeight() && deltaY < 0) {
            Log.i(TAG, "onMove: 低于最低高度并且还想缩小的 ");
            return false;
        }
        //超过最大
        if (maxHeight <= getHeight() && deltaY > 0) {
            Log.i(TAG, "onMove: 超过最大");
            return false;
        }

        ViewGroup.LayoutParams params = mContainer.getLayoutParams();
        params.height = (int) (getHeight() + deltaY);

        Log.i(TAG, "onMove: minHeight=" + minHeight + " params.height=" + params.height);

        //检测是否低于最低高度  和最大
        params.height = Math.max(minHeight, params.height);
        params.height = Math.min(maxHeight, params.height);
        mContainer.setLayoutParams(params);
        return true;

    }

    /**
     * 单位时间的位移像素值
     * 往下是负数 往上是正数
     *
     * @param velocityY
     */
    public boolean fling(int velocityY) {

        //先判断自己是否显示全了
        Rect localRect = new Rect();
        Log.i(TAG, "getLocalVisibleRect start");
        getLocalVisibleRect(localRect);
        Log.i(TAG, "getLocalVisibleRect end");
        int height = localRect.bottom - localRect.top;
        if (height != getHeight()) {
            return false;
        }

        Log.i(TAG, "fling: velocityY=" + velocityY + "mMinFlingVelocity=" + mMinFlingVelocity);

        if (Math.abs(velocityY) < mMinFlingVelocity) {
            return false;
        }

        //计算滑动距离
        velocityY = -velocityY;

        if (velocityY < 0 && getVisibleHeight() < minHeight) {
            //缩小 并且 已经低于最低高度  由列表处理
            return false;
        }

        int moveY = velocityY / 15;
        int destHeight;

        if (velocityY < 0) {
            destHeight = Math.max(getVisibleHeight() + moveY, minHeight);
        } else {
            destHeight = Math.min(getVisibleHeight() + moveY, maxHeight);
        }

        int time = (int) (destHeight * 1f / maxHeight * 100);

        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(time).start();
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();

        return true;
    }


    @Override
    public void setArrowImageView(int resid) {
    }

    @Override
    public void setProgressStyle(int style) {
    }

    @Override
    public void refreshComplete() {

    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static int getDefaultHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.max(outMetrics.widthPixels, outMetrics.heightPixels);
    }


}
