package com.cunteng008.daygram.widget;

import com.cunteng008.daygram.R;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyListView extends ListView implements OnScrollListener {

	private static final String TAG = "MyListView";
	private OnrefreshListener refreshListener;//监听刷新接口
	private boolean isOnrefreshListener;//是否设置了刷新监听
	private TextView tv_title;
	private int headerviewHeigth;
	private float    startY;//记录开始接触屏幕时 Y的坐标
	private boolean isRecoder=false;//startY时候记录过了
	private boolean isFirstItem=false;//是否是listView的第一个条目
	private float tempY;
	private int state=DONE;//当前状态
	public final static int PULL=0;//下拉状态
	public final static int RELEASE=1;//释放状态
	public final static int REFERESHING=2;//正在刷新
	public final static int DONE=4;//刷新完成状态
	private View view;
	private float ratio=3;

	private String time;
	private String DEFAULT_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		handler.post(updateThread);
		init(context);
	}
	public MyListView(Context context) {
		super(context);
		handler.post(updateThread);
		init(context);
	}
	private void init(Context context) {
		LayoutInflater inflater=LayoutInflater.from(context);
		view = inflater.inflate(R.layout.list_refresh_headerview, null);
		tv_title = (TextView) view.findViewById(R.id.tv_title);

		measureHeaderview(view);
		headerviewHeigth=view.getMeasuredHeight();
		view.setPadding(0,-1*headerviewHeigth, 0,0);//讲headview这样设置 它会隐藏在界面最上面看不见
		view.invalidate(); //让headview重新绘制

		this.addHeaderView(view);

		this.setOnScrollListener(this);
		this.setCacheColorHint(0x00000000);

	}

	//测量HeadView的高度
	private void measureHeaderview(View view) {
		ViewGroup.LayoutParams lp=view.getLayoutParams();
		if(null==lp){
			lp=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
		int childMeasureHeight;
		if(lp.height > 0){
			childMeasureHeight = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);//适合、匹配
		} else {
			childMeasureHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);//未指定
		}
		Log.i(TAG,"width:"+childMeasureWidth+"  height:"+childMeasureHeight);
		view.measure(childMeasureWidth,childMeasureHeight);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(isFirstItem&&!isRecoder){
					startY = event.getY();
					isRecoder=true;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if(isFirstItem&&!isRecoder){
					startY = event.getY();
					isRecoder=true;
				}
				tempY = event.getY();
				if(state!=REFERESHING&&isOnrefreshListener){//如果当前状态没有在刷新状态
					view.setPadding(0,(int) ((tempY-startY)/ratio-headerviewHeigth), 0,0);
					if(state==PULL){
						if((tempY-startY)/ratio<headerviewHeigth&&(tempY-startY)>0){
							//不用处理
						}if((tempY-startY)/ratio>headerviewHeigth){
							state=RELEASE;
							dealState(state);
						}if(tempY-startY<0){
							state=DONE;
							dealState(state);
						}
					}if(state==RELEASE){
						if((tempY-startY)/ratio<headerviewHeigth&&(tempY-startY)>0){
							//不用处理
							state=PULL;
							dealState(state);
						}if((tempY-startY)/ratio>headerviewHeigth){
						}if(tempY-startY<0){
							state=DONE;
							dealState(state);
						}
					}if(state==DONE){
						if((tempY-startY)/ratio<headerviewHeigth&&(tempY-startY)>0){
							//不用处理
							state=PULL;
							dealState(state);
						}if((tempY-startY)/ratio>headerviewHeigth){
							state=RELEASE;
							dealState(state);
						}if(tempY-startY<0){
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state==RELEASE) {
					state=REFERESHING;
					dealState(state);
					break;
				}if(state==PULL){
				state=DONE;
				dealState(state);
				break;
			}
				break;
		}
		return super.onTouchEvent(event);
	}
	/**
	 * 根据状态 做相关的处理
	 * @param state
	 */
	public void dealState(int state){
		switch (state) {
			case PULL:
				break;
			case RELEASE:
				break;
			case REFERESHING:
				tv_title.setVisibility(View.VISIBLE);
				view.setPadding(0,0,0,0);
				refresh_();//获取数据
				break;
			case DONE:
				view.setPadding(0,-1*headerviewHeigth, 0,0);
				break;
		}
	}

	@Override
	public void onScroll(AbsListView ablistview,int firstVisibleItem,int visibleItemCount,int totalItemCount){
		Log.i(TAG,"firstVisibleItem:"+firstVisibleItem+"visibleItemCount:"+visibleItemCount+"totalItemCount:"+totalItemCount);

			isFirstItem=true;//如果listview的第一个条目 才给设置成true,才有下来刷新

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}
	/**
	 * 对listview刷新监听
	 * @author janecer
	 */
	public interface OnrefreshListener{
		void refresh();
	}

	public void refresh_(){
		refreshListener.refresh();
		isRecoder=false;
	}
	public void setOnrefreshListener(OnrefreshListener refreshlisttener){
		refreshListener = refreshlisttener;
		isOnrefreshListener = true;
	}
	/**
	 * 刷新完成
	 */
	public void onRefreshComplete(){
		state=DONE;
		view.setPadding(0,-1*headerviewHeigth,0,0);
		dealState(state);
	}
	Handler handler = new Handler();//创建Handler
	Runnable updateThread = new Runnable() {
		public void run() {
			handler.postDelayed(updateThread, 1000);
			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					DEFAULT_TIME_FORMAT);
			time = dateFormatter.format(Calendar.getInstance().getTime());//获取当前时间
			tv_title.setText(time);
		}
	};
}
