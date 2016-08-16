package rorbin.q.radarview;


import java.util.ArrayList;
import java.util.List;

/**
 * @author changhai qiu
 *         Email:qstumn@163.com
 */
public class RadarData {
    private String mLabel;
    private List<Float> mValue;
    private int mColor;
    private List<String> mValueText;
    private int mVauleTextColor;
    private int mValueTextSize;
    private boolean mValueTextEnable;

    public RadarData(List<Float> value) {
        this("data", value, new RandomColor().randomColor());
    }

    public RadarData(List<Float> value, String label) {
        this(label, value, new RandomColor().randomColor());
    }

    public RadarData(String label, List<Float> value, int color) {
        this.mLabel = label;
        this.mValue = value;
        this.mColor = color;
        initValueText();
        mVauleTextColor = 0xFF9E9E9E;
        mValueTextSize = 10;
        mValueTextEnable = false;
    }

    public List<Float> getValue() {
        return mValue;
    }

    public void setValue(List<Float> value) {
        this.mValue = value;
        initValueText();
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String mLabel) {
        this.mLabel = mLabel;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public int getVauleTextColor() {
        return mVauleTextColor;
    }

    public void setVauleTextColor(int mVauleTextColor) {
        this.mVauleTextColor = mVauleTextColor;
    }

    public int getValueTextSize() {
        return mValueTextSize;
    }

    public void setValueTextSize(int mValueTextSize) {
        this.mValueTextSize = mValueTextSize;
    }

    public boolean isValueTextEnable() {
        return mValueTextEnable;
    }

    public void setValueTextEnable(boolean mValueTextEnable) {
        this.mValueTextEnable = mValueTextEnable;
    }

    public List<String> getValueText() {
        return mValueText;
    }

    public void setValueText(List<String> mValueText) {
        this.mValueText = mValueText;
    }

    private void initValueText() {
        mValueText = new ArrayList<>();
        for (int i = 0; i < mValue.size(); i++) {
            mValueText.add(mValue.get(i).toString());
        }
    }
}
