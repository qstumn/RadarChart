package rorbin.q.radarview;

import android.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chqiu on 2016/9/5.
 */
public class AnimeUtil {

    protected static void animeValue(RadarView view, AnimeType type, int duration) {
        switch (type) {
            case ZOOM:
                startZoomAnime(view, duration);
                break;
        }
    }

    private static void startZoomAnime(final RadarView view, int duration) {
        List<RadarData> datas = view.getRadarData();
        for (int i = 0; i < datas.size(); i++) {
            RadarData data = datas.get(i);
            ValueAnimator anime = ValueAnimator.ofFloat(0, 1f);
            final List<Float> values = data.getValue();
            final List<Float> values2 = new ArrayList<>(values);
            anime.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float percent = Float.parseFloat(animation.getAnimatedValue().toString());
                    for (int i = 0; i < values.size(); i++) {
                        values.set(i, values2.get(i) * percent);
                    }
                    view.invalidate();
                }
            });
            anime.setDuration(duration).start();
        }
    }

    public enum AnimeType {
        ZOOM, ROTATE
    }
}
