package com.example.headerscale;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

public class ParallaxListView extends ListView {
    
	
	private ImageView iv;
	/**
	 * ImageView原始高度
	 */
	private int originalHeight;
	/**
	 * header可移动的最大距离
	 */
	private int maxHeight;
	public ParallaxListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ParallaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void setParallaxImageView(final ImageView iv){
		this.iv = iv;
		 iv.post(new Runnable(){

		

			@Override
			public void run() {
				originalHeight = iv.getHeight();
				int drawableHeight = iv.getDrawable().getIntrinsicHeight();//获取图片高度
				/*
				 * 当ImageView设置的高度小于图片高度，则可移动的最大高度为图片高度
				 * 否则设定为ImgeView高度的两倍
				 */
				maxHeight = originalHeight>drawableHeight ?originalHeight*2 :drawableHeight;
				
			}});
	}
	/*listview滑动到头是否可以继续移动
	 * deltaX 水平方向移动的距离 
	 * deltaY竖直方向移动距离  为正则是底部滑到头继续滑动的距离，为负则是顶部
	 * isTouchEvent true是触摸滑动，false是惯性滑动
	 */
	@SuppressLint("NewApi") @Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		System.out.println("...........deltaX : "+deltaX+"  deltaY"+deltaY);
		//System.out.println("...........TOUCH : "+isTouchEvent+"  scroll  "+scrollY);
		
		
		
		if(deltaY<0 && isTouchEvent && iv!=null){//当触摸滑动顶部
			/*
			 * 让ImageView的高度随着用户向下滑动的距离的二分之一，再加原来高度
			 * 达到一种吃力拉动效果，如果大于最大高度，则设置成最大高度，否则则是新的高度
			 */
			int newHeight = iv.getHeight()-deltaY/2 >maxHeight ? maxHeight :iv.getHeight()-deltaY/2;
			iv.getLayoutParams().height = newHeight;
			iv.requestLayout();//重新布局
		}
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		/*
		 * 让ImageVie在用户抬起时，恢复原来高度
		 */
		if(ev.getAction()==MotionEvent.ACTION_UP && iv!=null){
			ValueAnimator animator = ValueAnimator.ofInt(iv.getHeight(),originalHeight);
			animator.addUpdateListener(new AnimatorUpdateListener() {
				
				@Override
				public void onAnimationUpdate(ValueAnimator arg0) {
					int newHeight = (Integer) arg0.getAnimatedValue();
					iv.getLayoutParams().height = newHeight;
					iv.requestLayout();//重绘
					
				}
			});
			animator.setDuration(500);
			animator.setInterpolator(new OvershootInterpolator());
			animator.start();
		}
		return super.onTouchEvent(ev);
	}
}
