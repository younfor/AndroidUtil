package com.rotateviewtest.main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;


public class RotateView extends FrameLayout implements OnGestureListener {

	private Context ct;
	private GestureDetector gd;
	private Drawable[] ds = new Drawable[4];

	private static int OVAL_A , OVAL_B , OVAL_CENTER_X, OVAL_CENTER_Y;

	private int LEFT_BORDER, RIGHT_BORDER, TOP_BORDER, BOTTOM_BORDER;
	private int OFFSET_WIDTH, OFFSET_HEIGHT;

	private boolean[] up = { true, true, true, false };

	private List<LayoutParams> params = new ArrayList<FrameLayout.LayoutParams>();
	private List<View> list = new ArrayList<View>();
	private List<View> sortList = new ArrayList<View>();

	int index = 0;

	private OnItemClickListener onItemClickListener;

	public interface OnItemClickListener {
		public void onItemClick(int position, View v);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		// TODO Auto-generated method stub
		return list.indexOf(sortList.get(i));
	}

	public RotateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//得到当前实例
		ct=context;
		setChildrenDrawingOrderEnabled(true);
		//调用屏幕宽度
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)ct).getWindowManager().getDefaultDisplay().getMetrics(dm);
		//调用图片宽度
		Drawable d = context.getResources().getDrawable(R.drawable.btn_bg01);
		OFFSET_WIDTH = d.getIntrinsicWidth() / 2;
		OFFSET_HEIGHT = d.getIntrinsicHeight() / 2;
		//设置显示中心
		OVAL_CENTER_X = dm.widthPixels/2;
		OVAL_CENTER_Y = d.getIntrinsicHeight();
		//椭圆半长宽
		OVAL_A=2*d.getIntrinsicWidth();
		OVAL_B=d.getIntrinsicHeight()/2;
		// 上下左右四个边界
		LEFT_BORDER = OVAL_CENTER_X - OVAL_A;
		RIGHT_BORDER = OVAL_CENTER_X + OVAL_A;
		TOP_BORDER = OVAL_CENTER_Y - OVAL_B;
		BOTTOM_BORDER = OVAL_CENTER_Y + OVAL_B;
		System.out.println("w:"+OVAL_CENTER_X);
		
		gd = new GestureDetector(ct,this);
		

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		gd.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		scroll(distanceX);
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	private int getOvalYByX(int x, boolean up) {
		int a = x - OVAL_CENTER_X;
		int aa = OVAL_B * OVAL_B * (OVAL_A * OVAL_A - a * a);
		double aaa = divide(aa, OVAL_A * OVAL_A).doubleValue();
		int y = (int) Math.sqrt(aaa);
		if (up) {
			y = -y;
		}
		return y + OVAL_CENTER_Y;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setAdapter(BaseAdapter adapter) {
		if (adapter.getCount() != 4) {
			Toast.makeText(getContext(), "Child count of adapter must be 4", 0)
					.show();
		}
		
		LayoutParams params01 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params01.leftMargin=LEFT_BORDER
				- OFFSET_WIDTH;
		params01.topMargin=OVAL_CENTER_Y - OFFSET_HEIGHT;
		LayoutParams params02 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params02.leftMargin=OVAL_CENTER_X
				- OFFSET_WIDTH;
		params02.topMargin=TOP_BORDER - OFFSET_HEIGHT;
		LayoutParams params03 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params03.leftMargin=RIGHT_BORDER
				- OFFSET_WIDTH;
		params03.topMargin= OVAL_CENTER_Y - OFFSET_HEIGHT;
		LayoutParams params04 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
		params04.leftMargin=OVAL_CENTER_X
				- OFFSET_WIDTH;
		params04.topMargin=BOTTOM_BORDER - OFFSET_HEIGHT;
		params.add(params01);
		params.add(params02);
		params.add(params03);
		params.add(params04);

		for (int i = 0; i < adapter.getCount(); i++) {
			View v = adapter.getView(i, new View(getContext()), this);
			addView(v, params.get(i));
			if (up[i]) {
				setAlphaByY(v, params.get(i).topMargin + OFFSET_HEIGHT - OVAL_CENTER_Y);
			}
			list.add(v);
			sortList.add(v);
		}

		for (int i = 0; i < adapter.getCount(); i++) {
			ds[i] = getContext().getResources().getDrawable((Integer)adapter.getItem(i));
		}

		for (View v : list) {
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Button btn = (Button) v;
					if (onItemClickListener != null) {
						onItemClickListener.onItemClick(list.indexOf(btn), btn);
					}
				}
			});
		}
	}

	private void setAlphaByY(View v, int y) {
		int alpha =255;
		if(y<0)
			alpha=100;
		v.getBackground().setAlpha(alpha);
	}

	private void scroll(float distanceX) {
		boolean scrolling2left = distanceX >= 0;
		int offset = (int) Math.abs(distanceX);
		List<int[]> loc = new ArrayList<int[]>();
		sortList.removeAll(sortList);
		for (View v : list) {
			LayoutParams params = (LayoutParams) v.getLayoutParams();
			int x = params.leftMargin + OFFSET_WIDTH;
			int y = params.topMargin + OFFSET_HEIGHT;
			int i = list.indexOf(v);
			if (scrolling2left) {
				// 左滑动
				if (up[i]) {
					// 中心上方按钮向右
					if (x + offset > RIGHT_BORDER) {
						// 滑动后会超出右边界,上变下，y取负
						x += offset;
						x -= (2 * (x - RIGHT_BORDER));
						up[i] = false;
						y = getOvalYByX(x, up[i]);
					} else {
						// 正常滑动，y取正
						x += offset;
						y = getOvalYByX(x, up[i]);
					}

				} else {
					// 中心下方按钮向左
					if (x - offset < LEFT_BORDER) {
						// 滑动后会超出左边界，下变上，y取正
						x -= offset;
						x += (2 * (LEFT_BORDER - x));
						up[i] = true;
						y = getOvalYByX(x, up[i]);
					} else {
						// 正常滑动，y取负
						x -= offset;
						y = getOvalYByX(x, up[i]);
					}

				}
			} else {
				// 右滑动
				if (up[i]) {
					// 中心上方按钮向左
					if (x - offset < LEFT_BORDER) {
						// 滑动后会超出左边界,上变下，y取负
						x -= offset;
						x += (2 * (LEFT_BORDER - x));
						up[i] = false;
						y = getOvalYByX(x, up[i]);
					} else {
						// 正常滑动，y取正
						x -= offset;
						y = getOvalYByX(x, up[i]);
					}

				} else {
					// 中心下方按钮向右
					if (x + offset > RIGHT_BORDER) {
						// 滑动后会超出右边界，下变上，y取正
						x += offset;
						x -= (2 * (x - RIGHT_BORDER));
						up[i] = true;
						y = getOvalYByX(x, up[i]);
					} else {
						// 正常滑动，y取负
						x += offset;
						y = getOvalYByX(x, up[i]);
					}
				}
			}
			loc.add(new int[] { x - OFFSET_WIDTH, y - OFFSET_HEIGHT });
			list.set(i, v);
		}
		sortList.addAll(list);
		Collections.sort(sortList, new Comparator<View>() {

			@Override
			public int compare(View lhs, View rhs) {
				// TODO Auto-generated method stub
				int index = list.indexOf(lhs);
				if (up[index])
					return -1;
				else {
					return 1;
				}
			}

		});
		for (View v : list) {
			int i = list.indexOf(v);
			int[] location = loc.get(i);
			LayoutParams params = (LayoutParams) v.getLayoutParams();
			params.leftMargin = location[0];
			params.topMargin = location[1];
			v.setLayoutParams(params);
			if (up[i]) {
				setAlphaByY(v, location[1] + OFFSET_HEIGHT - OVAL_CENTER_Y);
			} else {
				v.getBackground().setAlpha(255);
			}
		}
		invalidate();
	}



	private BigDecimal divide(float a, float b) {
		return new BigDecimal(a).divide(new BigDecimal(b), 8,
				BigDecimal.ROUND_HALF_UP);
	}
}
