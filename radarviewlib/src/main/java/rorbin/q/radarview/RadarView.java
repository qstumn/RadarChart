package rorbin.q.radarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rorbin.q.radarview.util.AnimeUtil;
import rorbin.q.radarview.util.RotateUtil;

import static android.R.attr.maxWidth;

/**
 * @author changhai qiu
 *         Email:qstumn@163.com
 */
public class RadarView extends View {
    private Context mContext;

    private int mWebMode;
    public static final int WEB_MODE_POLYGON = 1;
    public static final int WEB_MODE_CIRCLE = 2;

    public static final int VERTEX_ICON_POSITION_LEFT = 1;
    public static final int VERTEX_ICON_POSITION_RIGHT = 2;
    public static final int VERTEX_ICON_POSITION_TOP = 3;
    public static final int VERTEX_ICON_POSITION_BOTTOM = 4;
    public static final int VERTEX_ICON_POSITION_CENTER = 5;
    private double mPerimeter;

    private float mRadius;
    private PointF mPointCenter;
    private int mRadarLineColor;
    private float mRadarLineWidth;
    private boolean mRadarLineEnable;
    private int mLayer;
    private List<Integer> mLayerColor;
    private float mMaxValue;
    private List<Float> mMaxValues;

    private List<String> mVertexText;
    private List<Bitmap> mVertexIcon;
    private int mVertexIconPosition;
    private float mVertexIconSize;
    private float mVertexIconMargin;

    private int mVertexTextColor;
    private float mVertexTextSize;
    private float mVertexTextOffset;
    private int mMaxVertex;
    private float mCenterTextSize;
    private int mCenterTextColor;

    private double mAngle;
    private double mRotateAngle;

    private List<RadarData> mRadarData;

    private RectF mVertexIconRect;

    private Paint mRadarLinePaint;
    private Paint mLayerPaint;
    private TextPaint mVertexTextPaint;
    private Paint mValuePaint;
    private TextPaint mValueTextPaint;
    private Path mRadarPath;
    private TextPaint mCenterTextPaint;

    private GestureDetector mDetector;
    private Scroller mScroller;
    private float mFlingPoint;
    private double mRotateOrientation;
    private boolean mRotationEnable;

    private String mEmptyHint = "no data";
    private String mMaxLengthVertexText;
    private String mCenterText;

    private AnimeUtil mAnimeUtil;


    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.RadarView);
        mLayer = typedArray.getInt(R.styleable.RadarView_radar_layer, 5);
        mRotationEnable = typedArray.getBoolean(R.styleable.RadarView_rotation_enable, true);
        mWebMode = typedArray.getInt(R.styleable.RadarView_web_mode, WEB_MODE_POLYGON);
        mMaxValue = typedArray.getFloat(R.styleable.RadarView_max_value, 0);
        mRadarLineColor = typedArray.getColor(R.styleable.RadarView_radar_line_color, 0xFF9E9E9E);
        mRadarLineEnable = typedArray.getBoolean(R.styleable.RadarView_radar_line_enable, true);
        mRadarLineWidth = typedArray.getDimension(R.styleable.RadarView_radar_line_width, dp2px(1));
        mVertexTextColor = typedArray.getColor(R.styleable.RadarView_vertex_text_color, mRadarLineColor);
        mVertexTextSize = typedArray.getDimension(R.styleable.RadarView_vertex_text_size, dp2px(12));
        mVertexTextOffset = typedArray.getDimension(R.styleable.RadarView_vertex_text_offset, 0);
        mCenterTextColor = typedArray.getColor(R.styleable.RadarView_center_text_color, mRadarLineColor);
        mCenterTextSize = typedArray.getDimension(R.styleable.RadarView_center_text_size, dp2px(18));
        mCenterText = typedArray.getString(R.styleable.RadarView_center_text);
        mVertexIconSize = typedArray.getDimension(R.styleable.RadarView_vertex_icon_size, dp2px(20));
        mVertexIconPosition = typedArray.getInt(R.styleable.RadarView_vertex_icon_position, VERTEX_ICON_POSITION_TOP);
        mVertexIconMargin = typedArray.getDimension(R.styleable.RadarView_vertex_icon_margin, 0);
        int vertexTextResid = typedArray.getResourceId(R.styleable.RadarView_vertex_text, 0);
        typedArray.recycle();
        initVertexText(vertexTextResid);
    }

    private void initVertexText(int vertexTextResid) {
        try {
            String[] stringArray = mContext.getResources().getStringArray(vertexTextResid);
            if (stringArray.length > 0) {
                mVertexText = new ArrayList<>();
                Collections.addAll(mVertexText, stringArray);
            }
        } catch (Exception e) {
        }
    }

    private void init() {
        mRadarPath = new Path();
        mAnimeUtil = new AnimeUtil(this);
        mScroller = new Scroller(mContext);
        mDetector = new GestureDetector(mContext, new GestureListener());
        mDetector.setIsLongpressEnabled(false);

        mRadarData = new ArrayList<>();
        mLayerColor = new ArrayList<>();
        initLayerColor();

        mRadarLinePaint = new Paint();
        mLayerPaint = new Paint();
        mValuePaint = new Paint();
        mVertexTextPaint = new TextPaint();
        mValueTextPaint = new TextPaint();
        mCenterTextPaint = new TextPaint();
        mRadarLinePaint.setAntiAlias(true);
        mVertexTextPaint.setAntiAlias(true);
        mCenterTextPaint.setAntiAlias(true);
        mValueTextPaint.setFakeBoldText(true);

        mVertexIconRect = new RectF();
    }

    private void initLayerColor() {
        if (mLayerColor == null) {
            mLayerColor = new ArrayList<>();
        }
        if (mLayerColor.size() < mLayer) {
            int size = mLayer - mLayerColor.size();
            for (int i = 0; i < size; i++) {
                mLayerColor.add(Color.TRANSPARENT);
            }
        }
    }

    public int getWebMode() {
        return mWebMode;
    }

    /**
     * @param webMode only support {@link RadarView#WEB_MODE_CIRCLE} or {@link RadarView#WEB_MODE_POLYGON}
     */
    public void setWebMode(int webMode) {
        if (webMode != WEB_MODE_POLYGON && webMode != WEB_MODE_CIRCLE) {
            throw new IllegalStateException("only support WEB_MODE_POLYGON or WEB_MODE_CIRCLE");
        }
        this.mWebMode = webMode;
        invalidate();
    }

    public int getRadarLineColor() {
        return mRadarLineColor;
    }

    public void setRadarLineColor(int radarLineColor) {
        this.mRadarLineColor = radarLineColor;
        invalidate();
    }

    public float getRadarLineWidth() {
        return mRadarLineWidth;
    }

    public void setRadarLineWidth(float radarLineWidth) {
        this.mRadarLineWidth = radarLineWidth;
        invalidate();
    }

    public boolean isRadarLineEnable() {
        return mRadarLineEnable;
    }

    public void setRadarLineEnable(boolean radarLineEnable) {
        this.mRadarLineEnable = radarLineEnable;
        invalidate();
    }

    public int getLayer() {
        return mLayer;
    }

    public void setLayer(int layer) {
        this.mLayer = layer;
        initLayerColor();
        invalidate();
    }

    public List<Integer> getLayerColor() {
        return mLayerColor;
    }

    public void setLayerColor(List<Integer> layerColor) {
        this.mLayerColor = layerColor;
        initLayerColor();
        invalidate();
    }

    public List<Bitmap> getVertexIcon() {
        return mVertexIcon;
    }

    public void setVertexIconBitmap(List<Bitmap> vertexIconBitmap) {
        this.mVertexIcon = vertexIconBitmap;
        invalidate();
    }

    public void setVertexIconResid(List<Integer> vertexIconResid) {
        this.mVertexIcon = new ArrayList<>();
        for (Integer integer : vertexIconResid) {
            mVertexIcon.add(BitmapFactory.decodeResource(mContext.getResources(), integer));
        }
        invalidate();
    }

    public int getVertexIconPosition() {
        return mVertexIconPosition;
    }

    /**
     * @param vertexIconPosition only support {@link RadarView#VERTEX_ICON_POSITION_BOTTOM}
     *                           {@link RadarView#VERTEX_ICON_POSITION_CENTER}
     *                           {@link RadarView#VERTEX_ICON_POSITION_LEFT}
     *                           {@link RadarView#VERTEX_ICON_POSITION_RIGHT}
     *                           {@link RadarView#VERTEX_ICON_POSITION_TOP}
     */
    public void setVertexIconPosition(int vertexIconPosition) {
        if (vertexIconPosition != VERTEX_ICON_POSITION_BOTTOM
                && vertexIconPosition != VERTEX_ICON_POSITION_CENTER
                && vertexIconPosition != VERTEX_ICON_POSITION_LEFT
                && vertexIconPosition != VERTEX_ICON_POSITION_RIGHT
                && vertexIconPosition != VERTEX_ICON_POSITION_TOP) {
            throw new IllegalStateException("only support VERTEX_ICON_POSITION_BOTTOM" +
                    "  VERTEX_ICON_POSITION_CENTER  VERTEX_ICON_POSITION_LEFT " +
                    " VERTEX_ICON_POSITION_RIGHT  VERTEX_ICON_POSITION_TOP");
        }
        this.mVertexIconPosition = vertexIconPosition;
        invalidate();
    }

    public float getVertexIconSize() {
        return mVertexIconSize;
    }

    public void setVertexIconSize(float vertexIconSize) {
        this.mVertexIconSize = vertexIconSize;
        invalidate();
    }

    public float getVertexIconMargin() {
        return mVertexIconMargin;
    }

    public void setVertexIconMargin(float margin) {
        this.mVertexIconMargin = margin;
        invalidate();
    }

    public float getVertexTextOffset() {
        return mVertexTextOffset;
    }

    public void setVertexTextOffset(float vertexOffset) {
        this.mVertexTextOffset = vertexOffset;
        invalidate();
    }

    public float getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(float maxValue) {
        this.mMaxValue = maxValue;
        this.mMaxValues = null;
        invalidate();
    }

    public List<Float> getMaxValues() {
        return mMaxValues;
    }

    public void setMaxValues(List<Float> maxValues) {
        this.mMaxValues = maxValues;
        initMaxValues();
        invalidate();
    }

    private void initMaxValues() {
        if (mMaxValues != null && mMaxValues.size() < mMaxVertex) {
            int size = mMaxVertex - mMaxValues.size();
            for (int i = 0; i < size; i++) {
                mMaxValues.add(0f);
            }
        }
    }

    public List<String> getVertexText() {
        return mVertexText;
    }

    public void setVertexText(List<String> vertexText) {
        this.mVertexText = vertexText;
        initVertexText();
        invalidate();
    }

    public int getVertexTextColor() {
        return mVertexTextColor;
    }

    public void setVertexTextColor(int vertexTextColor) {
        this.mVertexTextColor = vertexTextColor;
        invalidate();
    }

    public float getVertexTextSize() {
        return mVertexTextSize;
    }

    public void setVertexTextSize(float vertexTextSize) {
        this.mVertexTextSize = vertexTextSize;
        invalidate();
    }

    public void setCenterText(String text) {
        mCenterText = text;
        invalidate();
    }

    public String getCenterText() {
        return mCenterText;
    }

    public float getCenterTextSize() {
        return mCenterTextSize;
    }

    public void setCenterTextSize(float centerTextSize) {
        this.mCenterTextSize = centerTextSize;
        invalidate();
    }

    public int getCenterTextColor() {
        return mCenterTextColor;
    }

    public void setCenterTextColor(int centerTextColor) {
        this.mCenterTextColor = centerTextColor;
        invalidate();
    }

    public boolean isRotationEnable() {
        return mRotationEnable;
    }

    public void setRotationEnable(boolean enable) {
        this.mRotationEnable = enable;
    }

    public void animeValue(int duration) {
        for (RadarData radarData : mRadarData) {
            animeValue(duration, radarData);
        }
    }

    public void animeValue(int duration, RadarData data) {
        if (!mAnimeUtil.isPlaying(data)) {
            mAnimeUtil.animeValue(AnimeUtil.AnimeType.ZOOM, duration, data);
        }
    }

    public void addData(RadarData data) {
        mRadarData.add(data);
        initData(data);
        animeValue(2000, data);
    }

    public void setEmptyHint(String hint) {
        mEmptyHint = hint;
        invalidate();
    }

    public String getEmptyHint() {
        return mEmptyHint;
    }

    public void removeRadarData(RadarData data) {
        mRadarData.remove(data);
        invalidate();
    }

    public void clearRadarData() {
        mRadarData.clear();
        invalidate();
    }

    private void initData(RadarData data) {
        List<Float> value = data.getValue();
        float max = Collections.max(value);
        if (mMaxValue == 0 || mMaxValue < max) {
            mMaxValue = max;
        }
        int valueSize = value.size();
        if (mMaxVertex < valueSize) {
            mMaxVertex = valueSize;
        }
        mAngle = 2 * Math.PI / mMaxVertex;
        initVertexText();
        initMaxValues();
    }

    private void initVertexText() {
        if (mVertexText == null || mVertexText.size() == 0) {
            mVertexText = new ArrayList<>();
            for (int i = 0; i < mMaxVertex; i++) {
                char text = (char) ('A' + i);
                mVertexText.add(String.valueOf(text));
            }
        } else if (mVertexText.size() < mMaxVertex) {
            int size = mMaxVertex - mVertexText.size();
            for (int i = 0; i < size; i++) {
                mVertexText.add("");
            }
        }
        if (mVertexText.size() == 0) {
            return;
        }
        mMaxLengthVertexText = Collections.max(mVertexText, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.length() - rhs.length();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRadarData.size() == 0) {
            mValueTextPaint.setTextSize(dp2px(16));
            float hintWidth = mValueTextPaint.measureText(mEmptyHint);
            canvas.drawText(mEmptyHint, mPointCenter.x - hintWidth / 2, mPointCenter.y, mValueTextPaint);
        } else {
            initPaint();
            calcRadius();
            drawRadar(canvas);
            drawData(canvas);
            drawCenterText(canvas);
        }
    }

    private void initPaint() {
        mRadarLinePaint.setStrokeWidth(mRadarLineWidth);
        mRadarLinePaint.setColor(mRadarLineColor);
        mRadarLinePaint.setStyle(Paint.Style.STROKE);
        mVertexTextPaint.setColor(mVertexTextColor);
        mVertexTextPaint.setTextSize(mVertexTextSize);
        mValuePaint.setStrokeWidth(dp2px(1));
        mLayerPaint.setStyle(Paint.Style.FILL);
        mCenterTextPaint.setTextSize(mCenterTextSize);
        mCenterTextPaint.setColor(mCenterTextColor);
    }

    private void drawRadar(Canvas canvas) {
        if (mWebMode == WEB_MODE_POLYGON) {
            drawWeb(canvas);
        } else if (mWebMode == WEB_MODE_CIRCLE) {
            drawCircle(canvas);
        }
        drawRadarLine(canvas);
    }

    private void drawWeb(Canvas canvas) {
        for (int i = mLayer; i >= 1; i--) {
            float radius = mRadius / mLayer * i;
            int layerColor = mLayerColor.get(i - 1);
            mRadarPath.reset();
            for (int j = 1; j <= mMaxVertex; j++) {
                double angleSin = Math.sin(mAngle * j - mRotateAngle);
                double angleCos = Math.cos(mAngle * j - mRotateAngle);
                float x = (float) (mPointCenter.x + angleSin * radius);
                float y = (float) (mPointCenter.y - angleCos * radius);
                if (j == 1) {
                    mRadarPath.moveTo(x, y);
                } else {
                    mRadarPath.lineTo(x, y);
                }
            }
            mRadarPath.close();
            if (layerColor != Color.TRANSPARENT) {
                mLayerPaint.setColor(layerColor);
                canvas.drawPath(mRadarPath, mLayerPaint);
            }
            if (mRadarLineEnable) {
                canvas.drawPath(mRadarPath, mRadarLinePaint);
            }
        }
    }

    private void drawCircle(Canvas canvas) {
        for (int i = mLayer; i >= 1; i--) {
            float radius = mRadius / mLayer * i;
            int layerColor = mLayerColor.get(i - 1);
            if (layerColor != Color.TRANSPARENT) {
                mLayerPaint.setColor(layerColor);
                canvas.drawCircle(mPointCenter.x, mPointCenter.y, radius, mLayerPaint);
            }
            if (mRadarLineEnable) {
                canvas.drawCircle(mPointCenter.x, mPointCenter.y, radius, mRadarLinePaint);
            }
        }
    }

    private void drawRadarLine(Canvas canvas) {
        for (int i = 1; i <= mMaxVertex; i++) {
            double angleSin = Math.sin(mAngle * i - mRotateAngle);
            double angleCos = Math.cos(mAngle * i - mRotateAngle);
            drawVertex(canvas, i, angleSin, angleCos);
            drawVertexLine(canvas, angleSin, angleCos);
        }
    }

    private void drawVertex(Canvas canvas, int index, double angleSin, double angleCos) {
        float x = (float) (mPointCenter.x + angleSin * (mRadius + mVertexTextOffset));
        float y = (float) (mPointCenter.y - angleCos * (mRadius + mVertexTextOffset));
        String text = mVertexText.get(index - 1);
        float textWidth = mVertexTextPaint.measureText(text);
        Paint.FontMetrics fontMetrics = mVertexTextPaint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        if (mVertexIcon != null && mVertexIcon.size() >= index) {
            Bitmap icon = mVertexIcon.get(index - 1);
            int[] iconSize = RotateUtil.geometricScaling(icon.getWidth(), icon.getHeight(), mVertexIconSize);
            float textY = y + textHeight / 4;
            float textX = x - textWidth / 2;
            switch (mVertexIconPosition) {
                case VERTEX_ICON_POSITION_LEFT:
                    mVertexIconRect.left = x - (iconSize[0] + mVertexIconMargin + textWidth) / 2f;
                    mVertexIconRect.right = mVertexIconRect.left + iconSize[0];
                    mVertexIconRect.top = y - iconSize[1] / 2f;
                    mVertexIconRect.bottom = mVertexIconRect.top + iconSize[1];
                    textX = mVertexIconRect.right + mVertexIconMargin;
                    break;
                case VERTEX_ICON_POSITION_RIGHT:
                    mVertexIconRect.right = x + (iconSize[0] + mVertexIconMargin + textWidth) / 2f;
                    mVertexIconRect.left = mVertexIconRect.right - iconSize[0];
                    mVertexIconRect.top = y - iconSize[1] / 2f;
                    mVertexIconRect.bottom = mVertexIconRect.top + iconSize[1];
                    textX = mVertexIconRect.left - mVertexIconMargin - textWidth;
                    break;
                case VERTEX_ICON_POSITION_TOP:
                    mVertexIconRect.left = x - iconSize[0] / 2f;
                    mVertexIconRect.right = mVertexIconRect.left + iconSize[0];
                    mVertexIconRect.top = y - (iconSize[1] + mVertexIconMargin + textHeight) / 2f;
                    mVertexIconRect.bottom = mVertexIconRect.top + iconSize[1];
                    textY = mVertexIconRect.bottom + mVertexIconMargin + textHeight / 2f + textHeight / 4f;
                    break;
                case VERTEX_ICON_POSITION_BOTTOM:
                    mVertexIconRect.left = x - iconSize[0] / 2f;
                    mVertexIconRect.right = mVertexIconRect.left + iconSize[0];
                    mVertexIconRect.bottom = y + (iconSize[1] + mVertexIconMargin + textHeight) / 2f;
                    mVertexIconRect.top = mVertexIconRect.bottom - iconSize[1];
                    textY = mVertexIconRect.top - mVertexIconMargin - textHeight / 2f + textHeight / 4f;
                    break;
                case VERTEX_ICON_POSITION_CENTER:
                    mVertexIconRect.left = x - iconSize[0] / 2f;
                    mVertexIconRect.right = mVertexIconRect.left + iconSize[0];
                    mVertexIconRect.top = y - iconSize[1] / 2f;
                    mVertexIconRect.bottom = mVertexIconRect.top + iconSize[1];
                    break;
            }
            drawVertexImpl(canvas, text, icon, mVertexTextPaint, textY, textX);
        } else {
            drawVertexImpl(canvas, text, null, mVertexTextPaint, x - textWidth / 2, y + textHeight / 4);
        }
    }

    protected void drawVertexImpl(Canvas canvas, String text, Bitmap icon, Paint paint,
                                  float textY, float textX) {
        if (icon != null) {
            canvas.drawBitmap(icon, null, mVertexIconRect, paint);
        }
        if (!TextUtils.isEmpty(text)) {
            canvas.drawText(text, textX, textY, paint);
        }
    }

    private void drawVertexLine(Canvas canvas, double angleSin, double angleCos) {
        if (!mRadarLineEnable) {
            return;
        }
        float x = (float) (mPointCenter.x + angleSin * mRadius);
        float y = (float) (mPointCenter.y - angleCos * mRadius);
        canvas.drawLine(mPointCenter.x, mPointCenter.y, x, y, mRadarLinePaint);
    }

    private void drawData(Canvas canvas) {
        for (int i = 0; i < mRadarData.size(); i++) {
            RadarData radarData = mRadarData.get(i);
            mValuePaint.setColor(radarData.getColor());
            mValueTextPaint.setTextSize(dp2px(radarData.getValueTextSize()));
            mValueTextPaint.setColor(radarData.getVauleTextColor());
            List<Float> values = radarData.getValue();
            mRadarPath.reset();
            PointF[] textPoint = new PointF[mMaxVertex];
            for (int j = 1; j <= mMaxVertex; j++) {
                float value = 0;
                if (values.size() >= j) {
                    value = values.get(j - 1);
                }
                Float percent;
                if (mMaxValues != null) {
                    percent = value / mMaxValues.get(j - 1);
                } else {
                    percent = value / mMaxValue;
                }
                if (percent.isInfinite()) {
                    percent = 1f;
                } else if (percent.isNaN()) {
                    percent = 0f;
                }
                if (percent > 1f) {
                    percent = 1f;
                }
                float x = (float) (mPointCenter.x + Math.sin(mAngle * j - mRotateAngle) * mRadius * percent);
                float y = (float) (mPointCenter.y - Math.cos(mAngle * j - mRotateAngle) * mRadius * percent);
                if (j == 1) {
                    mRadarPath.moveTo(x, y);
                } else {
                    mRadarPath.lineTo(x, y);
                }
                textPoint[j - 1] = new PointF(x, y);
            }
            mRadarPath.close();
            mValuePaint.setAlpha(255);
            mValuePaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mRadarPath, mValuePaint);
            mValuePaint.setStyle(Paint.Style.FILL);
            mValuePaint.setAlpha(150);
            canvas.drawPath(mRadarPath, mValuePaint);
            if (radarData.isValueTextEnable()) {
                List<String> valueText = radarData.getValueText();
                for (int k = 0; k < textPoint.length; k++) {
                    String text = "";
                    if (valueText.size() > k) {
                        text = valueText.get(k);
                    }
                    float textWidth = mValueTextPaint.measureText(text);
                    Paint.FontMetrics fontMetrics = mValueTextPaint.getFontMetrics();
                    float textHeight = fontMetrics.descent - fontMetrics.ascent;
                    canvas.drawText(text, textPoint[k].x - textWidth / 2, textPoint[k].y + textHeight / 3, mValueTextPaint);
                }
            }
        }
    }

    private void drawCenterText(Canvas canvas) {
        if (!TextUtils.isEmpty(mCenterText)) {
            float textWidth = mCenterTextPaint.measureText(mCenterText);
            Paint.FontMetrics fontMetrics = mCenterTextPaint.getFontMetrics();
            float textHeight = fontMetrics.descent - fontMetrics.ascent;
            canvas.drawText(mCenterText, mPointCenter.x - textWidth / 2, mPointCenter.y + textHeight / 3, mCenterTextPaint);
        }
    }

    private void calcRadius() {
        if (mVertexText == null || mVertexText.size() == 0) {
            mRadius = Math.min(mPointCenter.x, mPointCenter.y) - mVertexTextOffset;
        } else {
            float maxWidth;
            if (mVertexIconPosition == VERTEX_ICON_POSITION_LEFT || mVertexIconPosition == VERTEX_ICON_POSITION_RIGHT) {
                maxWidth = (mVertexTextPaint.measureText(mMaxLengthVertexText) + mVertexIconMargin + mVertexIconSize) / 2;
            } else {
                maxWidth = Math.max(mVertexTextPaint.measureText(mMaxLengthVertexText), mVertexIconSize) / 2;
            }
            mRadius = Math.min(mPointCenter.x, mPointCenter.y) - (maxWidth + mVertexTextOffset);
            mPerimeter = 2 * Math.PI * mRadius;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mRotationEnable) return super.onTouchEvent(event);
        return mDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                mFlingPoint = e2.getX();
                mScroller.fling((int) e2.getX(), 0, (int) velocityX, 0, (int) (-mPerimeter + e2.getX()),
                        (int) (mPerimeter + e2.getX()), 0, 0);
            } else if (Math.abs(velocityY) > Math.abs(velocityX)) {
                mFlingPoint = e2.getY();
                mScroller.fling(0, (int) e2.getY(), 0, (int) velocityY, 0, 0, (int) (-mPerimeter + e2.getY()),
                        (int) (mPerimeter + e2.getY()));
            }
            invalidate();
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            double rotate = mRotateAngle;
            double dis = RotateUtil.getRotateAngle(new PointF(e2.getX() - distanceX, e2.getY() - distanceY)
                    , new PointF(e2.getX(), e2.getY()), mPointCenter);
            rotate += dis;
            handleRotate(rotate);
            mRotateOrientation = dis;
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            int max = Math.max(Math.abs(x), Math.abs(y));
            double rotateDis = RotateUtil.CIRCLE_ANGLE * (Math.abs(max - mFlingPoint) / mPerimeter);
            double rotate = mRotateAngle;
            if (mRotateOrientation > 0) {
                rotate += rotateDis;
            } else if (mRotateOrientation < 0) {
                rotate -= rotateDis;
            }
            handleRotate(rotate);
            mFlingPoint = max;
            invalidate();
        }
    }

    private void handleRotate(double rotate) {
        rotate = RotateUtil.getNormalizedAngle(rotate);
        mRotateAngle = rotate;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPointCenter = new PointF(w / 2, h / 2);
    }

    private float dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }
}
