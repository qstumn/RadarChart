# RadarView
一个可以自由定制、旋转交互的Android雷达图Lib

![](https://github.com/qstumn/RadarView/blob/master/logo.png?raw=true)

###一些特性
* 支持手势旋转（可关闭）

* 支持动画的方式的方式添加展现数据

* 支持对各层雷达网添加背景

* 支持自定义雷达网层数

* 支持使用圆形作为雷达网

* 扇区（顶点）个数无限制

* 支持添加多组数据

* 数据区支持设置值的描述文字

* 所有的文字、雷达网、线、数据区颜色和大小均可定制

![](https://github.com/qstumn/RadarView/blob/master/demo.gif?raw=true)


## how to use:
###1. gradle
`compile 'q.rorbin:RadarView:1.0.0'`

###2. xml
     <rorbin.q.radarview.RadarView
        android:id="@+id/radarView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
    

###3. 属性说明

xml | code | 说明
---|---|---
app:radar_layer | setLayer | 雷达网层数
app:rotation_enable | setRotationEnable | 是否打开手势旋转
app:web_mode | setWebMode | 雷达网模式(多边形、圆形...)
app:max_value | setMaxValue | 最外圈的值
app:radar_line_color | setRadarLineColor | 雷达网线条颜色
app:radar_line_width | setRadarLineWidth | 雷达网线条大小
app:radar_line_enable | setRadarLineEnable | 是否显示雷达网线条
app:vertex_text_color | setVertexTextColor | 顶点文字颜色
app:vertex_text_size | setVertexTextSize | 顶点文字大小
app:vertex_text_offset | setVertexTextOffset | 顶点文字距最外层网偏移量
注：各属性均有默认值

###4. 方法说明
  code | 说明
  --- | ---
setLayerColor | 设置雷达网各层颜色
setVertexText | 设置顶点文字内容
 animeValue | 展现一次数据添加动画
 addData | 添加数据
 
###5. 添加数据
 1. 创建并数据
 
	   `List<Float> values = new ArrayList<>();
        Collections.addAll(values, 3.2f, 6.7f, 2f, 7f, 5.6f);
        RadarData data = new RadarData(values);
         mRadarView.addData(data);`
        
 2. 方法说明
 
  code | 说明
  --- | ---
  setLabel | 数据的标注
  setValue | 设置（更改）数据值
  setValueText | 数据值的描述
  setValueTextEnable | 是否显示描述
  setValueTextSize | 描述的文字大小
  setVauleTextColor | 描述的文字颜色
       
