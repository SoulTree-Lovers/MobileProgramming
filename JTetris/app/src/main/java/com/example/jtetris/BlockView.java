package com.example.jtetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;



/**
 * TODO: document your custom view class.
 */
public class BlockView extends View {
    public BlockView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }
    public BlockView(Context context, AttributeSet attrs) { super(context, attrs); }
    public BlockView(Context context) { super(context); }
    private int cy, cx; // current drawing position
    private int by, bx; // unit block size
    private int blkWidth; // width of the largest block
    private Matrix block = null;
    private Paint paint = new Paint();

    public void init(int w) { blkWidth = w; }
    public void accept(Matrix m) { block = m; }
    public void onDraw(Canvas canvas) {
        if (block == null) return;
        int[][] array = block.get_array();
        super.onDraw(canvas);
        by = (getHeight() - 20 - 5*(blkWidth-1)) / blkWidth;   // 20: Margin(up-10, down-10), block gap(5) * (blkWidth - 1)
        bx = (getWidth() - 20 - 5*(blkWidth-1)) / blkWidth;    // 20: Margin(up-10, down-10), block gap(5) * (blkWidth - 1)
        paint.setStyle(Paint.Style.FILL);

        cy = 10 + (blkWidth - array.length)*(by+5)/2;
        cx = 10 + (blkWidth - array.length)*(bx+5)/2;
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array[0].length; x++) {
                switch(array[y][x]) {
                    case 0: paint.setColor(Color.BLACK); break;
                    case 10: paint.setColor(Color.RED); break;
                    case 20: paint.setColor(Color.GREEN); break;
                    case 30: paint.setColor(Color.BLUE); break;
//                    case 40: paint.setColor(Color.BLUE); break;
//                    case 50: paint.setColor(Color.YELLOW); break;
//                    case 60: paint.setColor(Color.RED); break;
//                    case 70: paint.setColor(Color.MAGENTA); break;
                    default : paint.setColor(Color.WHITE); break;
                }
                if (array[y][x] == 10) drawRedCircle(canvas, cx, cy);
                else if (array[y][x] == 20) drawGreenRectangle(canvas, cx, cy);
                else if (array[y][x] == 30) drawBlueDiamond(canvas, cx, cy);
                else if (array[y][x] != 0)
                    canvas.drawRect(cx, cy, cx + bx, cy + by, paint);
                cx += (bx + 5);
            }
            cx = 10 + (blkWidth - array.length)*(bx+5)/2;
            cy += (by + 5);
        }

    }

    private void drawRedCircle(Canvas canvas, float cx, float cy) {
        float radius = Math.min(bx, by) / 2;
        paint.setColor(Color.RED);
        canvas.drawCircle(cx + bx / 2, cy + by / 2, radius, paint);
    }

    private void drawGreenRectangle(Canvas canvas, float cx, float cy) {
        paint.setColor(Color.GREEN);
        canvas.drawRect(cx, cy, cx + bx, cy + by, paint);
    }

    private void drawBlueDiamond(Canvas canvas, float cx, float cy) {
        paint.setColor(Color.BLUE);
        Path path = new Path();
        path.moveTo(cx + bx / 2, cy);
        path.lineTo(cx + bx, cy + by / 2);
        path.lineTo(cx + bx / 2, cy + by);
        path.lineTo(cx, cy + by / 2);
        path.lineTo(cx + bx / 2, cy);
        path.close();
        canvas.drawPath(path, paint);
    }
}
