package com.bubble;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.Toast;

public class BubbleMove extends View {

	String text="8";
	private int  curRadius;
	private Paint paint; // ����Բ��ͼ��
    private TextPaint textPaint; // ����Բ��ͼ��
    private Paint.FontMetrics textFontMetrics;
    private Point end;
    private Point base;
    private Point touch;
    Path path = new Path();
    private int moveRadius=20;
    private int maxDistance=150,curDistance=0;
    private boolean isMove=false;
    public BubbleMove(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public void setRadius(int r)
    {
    	moveRadius=r;
    	curRadius=r;
    }
    public BubbleMove(Context context) {
        super(context);
        init(context);
    }
    public void setBasePoint(int x,int y)
    {
    	base=new Point(x, y);
    }
	public void init(Context context) {
		// ���û���flag��paint
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);

        // ���û������ֵ�paint
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(18);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textFontMetrics = textPaint.getFontMetrics();
        
        //��ʼ����
        end=new Point((int)moveRadius,(int)moveRadius);
	}
	public void setText(String s)
	{
		this.text=s;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//����͸��
		canvas.drawColor(Color.TRANSPARENT);
		//���ƶ�ԲȦ
		canvas.drawCircle(end.x+base.x, end.y+base.y, moveRadius, paint);
		
		//������������
		if(isMove&&curDistance<maxDistance)
		{
			canvas.drawCircle(base.x+moveRadius, base.y+moveRadius, curRadius, paint);
			path.reset();
            double sin = -1.0*(end.y-touch.y) / curDistance;
            double cos = 1.0*(end.x-touch.x) /curDistance ;
            // tableԲ������
            path.moveTo(
                    (float) (base.x+moveRadius - curRadius * sin),
                    (float) (base.y+moveRadius - curRadius * cos)
            );
            path.lineTo(
                    (float) (base.x+moveRadius + curRadius * sin),
                    (float) (base.y+moveRadius + curRadius * cos)
            );
            // moveԲ������
            path.quadTo(
                    (base.x+moveRadius + base.x+end.x) / 2, (base.y+moveRadius + base.y+end.y) / 2,
                    (float) (base.x+end.x +  moveRadius* sin), (float) (base.y+end.y + moveRadius * cos)
            );
            path.lineTo(
                    (float) (base.x+end.x - moveRadius * sin),
                    (float) (base.y+end.y- moveRadius * cos)
            );
            // �պ�
            path.quadTo(
            		 (base.x+moveRadius + base.x+end.x) / 2, (base.y+moveRadius +base.y+ end.y) / 2,
                    (float) (base.x+moveRadius - curRadius * sin), (float) (base.y+moveRadius - curRadius * cos)
            );
            canvas.drawPath(path, paint);
		}
		//�ƶ�Բ�ϵ�����
		float textH = - textFontMetrics.ascent - textFontMetrics.descent;
        canvas.drawText(text,end.x+base.x, end.y+base.y+ textH / 2, textPaint);

	}
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	touch=new Point((int)event.getX(),(int)event.getY());
            	isMove=true;
                break;
            case MotionEvent.ACTION_MOVE:
                end.x = (int)(event.getX());
                end.y = (int) (event.getY());
                double offx=event.getX()-touch.x;
                double offy=event.getY()-touch.y;
                //��ǰ�������
                curDistance=(int)Math.sqrt(offx*offx+offy*offy);
                //����Բ���ž��������С
                curRadius=(int)(moveRadius*(1.0-1.0*curDistance/maxDistance));
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            	isMove=false;
            	curRadius=moveRadius;
            	Point old=new Point(end);
                end=new Point((int)moveRadius,(int)moveRadius);
                postInvalidate();
                if(curDistance<maxDistance)
                {
                	Toast.makeText(getContext(), "�ɿ�����", Toast.LENGTH_SHORT).show();
                	shakeAnimation(old);
                }
                else
                	Toast.makeText(getContext(), "��������", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }
	
	//CycleTimes�����ظ��Ĵ���
    public void shakeAnimation(Point end) {
    	float x,y;
    	x=0.3f*(end.x-touch.x)*curDistance/maxDistance;
    	y=0.3f*(end.y-touch.y)*curDistance/maxDistance;
    	ObjectAnimator animx = ObjectAnimator .ofFloat(this, "translationX", x);
    	animx.setInterpolator(new CycleInterpolator(1));
    	ObjectAnimator animy = ObjectAnimator .ofFloat(this, "translationY", y);
    	animy.setInterpolator(new CycleInterpolator(1));
    	AnimatorSet set=new AnimatorSet();
    	set.setDuration(200);
    	set.playTogether(animx,animy);
    	set.start();

    }
	public void setMaxDistance(int dis)
	{
		maxDistance=dis;
	}
	
}
