package com.jcodecraeer.xrecyclerview;

/**
 * Created by jianghejie on 15/11/22.
 */
interface BaseRefreshHeader {

	int STATE_NORMAL = 0;
	int STATE_RELEASE_TO_REFRESH = 1;
	int STATE_REFRESHING = 2;
	int STATE_DONE = 3;

	/**
	 * true代表消费事件
	 * @param delta
	 * @return
	 */
	boolean onMove(float delta);

	boolean releaseAction();

	void refreshComplete();

    /**
     * 是否处理fling事件
     * @param velocityY
     * @return
     */
	boolean fling(int velocityY) ;

}