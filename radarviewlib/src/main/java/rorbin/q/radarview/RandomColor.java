package rorbin.q.radarview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * @author changhai qiu
 *         Email:qstumn@163.com
 */
public class RandomColor {
    private HashMap<Integer, Integer> BrightColor;
    private HashMap<Integer, Integer> LightColor;

    {
        BrightColor = new HashMap<>();
        LightColor = new HashMap<>();
        LightColor.put(0xFFE84E40, 0);
//        LightColor.put(0xffec407a, 0);
        LightColor.put(0xffab47bc, 0);
        LightColor.put(0xff7e57c2, 0);
//        LightColor.put(0xff5c6bc0, 0);
        LightColor.put(0xff738ffe, 0);
        LightColor.put(0xff29b6f6, 0);
        LightColor.put(0xff26c6da, 0);
//        LightColor.put(0xff26a69a, 0);
//        LightColor.put(0xff2baf2b, 0);
        LightColor.put(0xff9ccc65, 0);
        LightColor.put(0xffd4e157, 0);
        LightColor.put(0xffffca28, 0);
//        LightColor.put(0xffffa726, 0);
        LightColor.put(0xffff7043, 0);
        LightColor.put(0xff8d6e63, 0);
    }

    public int randomColor() {
        ArrayList<Integer> color = new ArrayList<>(LightColor.keySet());
        int randomColor;
        int i = 0;
        int count = 0;
        do {
            randomColor = color.get(new Random().nextInt(color.size()));
            count++;
            if (count > color.size()) {
                i++;
            }
        } while (LightColor.get(randomColor) != i);
        LightColor.put(randomColor, LightColor.get(randomColor) + 1);
        return randomColor;
    }
}
