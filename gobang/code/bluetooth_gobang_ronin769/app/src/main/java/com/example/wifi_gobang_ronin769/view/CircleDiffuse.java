package com.example.wifi_gobang_ronin769.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;


public class CircleDiffuse extends AppCompatImageView {

    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    int width=wm.getDefaultDisplay().getWidth();
    int height=wm.getDefaultDisplay().getHeight();


    public CircleDiffuse(Context context)
    {
        super(context);
    }

    public CircleDiffuse(Context context, AttributeSet attrs,int defStyleAttr)
    {
        super(context,attrs,defStyleAttr);
    }

    public CircleDiffuse(Context context, AttributeSet attrs ) {
        super(context, attrs);
    }

    int banjing=1;
    int banjing1=banjing;
    int banjing2=banjing1+100;
    int banjing3=banjing2+200;
    int banjing4=banjing3+300;



    @Override
    public void onDraw(Canvas canvas)
    {
    super.onDraw(canvas);
        Paint paint=new Paint();
        paint.setStrokeWidth(1);
        paint.setARGB(255,25,255,255);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        //圆

        if(banjing1==800)
        {
            banjing1=banjing;

        }
        if(banjing2==800)
        {
            banjing2=banjing;

        }
        if(banjing3==800)
        {
            banjing3=banjing;

        }
        if(banjing4==800)
        {
            banjing4=banjing;

        }
        canvas.drawCircle(width/2,height/2-72, banjing1, paint);
        canvas.drawCircle(width/2,height/2-72, banjing2, paint);
        canvas.drawCircle(width/2,height/2-72, banjing3, paint);
        canvas.drawCircle(width/2,height/2-72, banjing4, paint);

        banjing1++;
        banjing2++;
        banjing3++;
        banjing4++;
        canvas.save();
        canvas.restore();
        postInvalidateDelayed(13);





    }


}
