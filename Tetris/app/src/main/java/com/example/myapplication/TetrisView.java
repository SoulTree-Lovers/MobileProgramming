package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;

public class TetrisView extends View {
    private int dy, dx;   // view size in unit blocks
    private int cy = 10, cx = 10;   // current position
    private int by = 0, bx = 0;     // unit block size
    private int color = Color.BLACK;
    private Paint paint = new Paint();

    public TetrisView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }
    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TetrisView(Context context) {
        super(context);
    }
    public void init(int y, int x) {
            dy = y; dx = x;
            cx = 10; cy = 10;
            bx = (getWidth() - 20 - ((dx-1)*5))/dx;
            by = (getHeight() - 20 - ((dy-1)*5))/dy;
    }
    public void accept(char key) {
        switch (key) {
            case 'N':
                if (color == Color.WHITE) color = Color.BLACK;
                else color = Color.WHITE;
                break;
            case 'w': cy -= (by + 5); break;
            case 'a': cx -= (bx + 5); break;
            case 'd': cx += (bx + 5); break;
            case 's': cy += (by + 5); break;
        }
    }
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRect(cx, cy, cx + bx, cy + by, paint);
        Log.d("TetrisView", "(cy,cx)=(" + cy + "," + cx + ")");
        Log.d("TetrisView", "(by,bx)=(" + by + "," + bx + ")");
        Log.d("TetrisView", "color=" + color);
    }
}
