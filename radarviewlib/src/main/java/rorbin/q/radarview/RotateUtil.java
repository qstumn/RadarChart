package rorbin.q.radarview;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Scroller;

/**
 * @author changhai qiu
 *         Email:qstumn@163.com
 */
public class RotateUtil {
    public static final double CIRCLE_ANGLE = 2 * Math.PI;

    protected static double getRotateAngle(PointF p1, PointF p2, PointF mPointCenter) {
        int q1 = getQuadrant(p1, mPointCenter);
        int q2 = getQuadrant(p2, mPointCenter);
        double angle1 = getAngle(p1, mPointCenter);
        double angle2 = getAngle(p2, mPointCenter);
        if (q1 == q2) {
            return angle1 - angle2;
        } else {
            return 0;
        }
    }

    public static double getAngle(PointF p, PointF mPointCenter) {
        float x = p.x - mPointCenter.x;
        float y = mPointCenter.y - p.y;
        double angle = Math.atan(y / x);
        return getNormalizedAngle(angle);
    }

    public static int getQuadrant(PointF p, PointF mPointCenter) {
        float x = p.x;
        float y = p.y;
        if (x > mPointCenter.x) {
            if (y > mPointCenter.y) {
                return 4;
            } else if (y < mPointCenter.y) {
                return 1;
            }
        } else if (x < mPointCenter.x) {
            if (y > mPointCenter.y) {
                return 3;
            } else if (y < mPointCenter.y) {
                return 2;
            }
        }
        return -1;
    }

    public static double getNormalizedAngle(double angle) {
        while (angle < 0)
            angle += CIRCLE_ANGLE;
        return angle % CIRCLE_ANGLE;
    }
}
