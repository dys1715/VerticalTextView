package winsion.verticaltextview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.HorizontalScrollView;

public class MainActivity extends Activity {
    private HorizontalScrollView sv;
    private VerticalTextView tv, tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (VerticalTextView) findViewById(R.id.tv);
        tv2 = (VerticalTextView) findViewById(R.id.tv2);
        sv = (HorizontalScrollView) findViewById(R.id.sv);

        //设置接口事件接收
        Handler handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case VerticalTextView.LAYOUT_CHANGED:
                        sv.scrollBy(tv.getTextWidth(), 0);//滚动到最右边
                        break;
                }
            }
        };
        tv.setHandler(handler);//将Handler绑定到TextViewVertical

        //创建并设置字体（这里只是为了效果好看一些，但为了让网友们更容易下载，字体库并没有一同打包
        //如果需要体验下效果的朋友可以自行在网络上搜索stxingkai.ttf并放入assets/fonts/中）
        //Typeface face=Typeface.createFromAsset(getAssets(),"fonts/stxingkai.ttf");
        //tv.setTypeface(face);

        //设置文字内容
        tv.setText("测试\n这是一段(测试)文字，主要是（为了测试）[竖直排版】TextView的{显示效果}。" +
                "\n竖直排版的(TextView)需要配合HorizontalScrollView使用才能有更佳的效果。当然，(如果你有时间)的话，也可以给这个类" +
                "加上滚动的功能。" + "\n " + "测试\n这是一段测试文字，主要是为了测试竖直排版TextView的显示效果。" +
                "\n竖直排版的TextView需要配合HorizontalScrollView使用才能有更佳的效果。当然，如果你有时间的话，也可以给这个类" +
                "加上滚动的功能。");
        tv2.setText("hello(world）!！\n\n\t竖直,排版的TextView，");
        //不设置regulars的话有默认的defaultRegular(旋转括号)
        tv2.setRegulars(new String[]{"(",",", "!", "！"});
    }
}
