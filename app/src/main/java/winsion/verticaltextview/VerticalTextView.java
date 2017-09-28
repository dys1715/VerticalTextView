package winsion.verticaltextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class VerticalTextView extends View {

    public static final int LAYOUT_CHANGED = 1;
    private Paint paint;
    private int mTextPosx = 0;// x坐标
    private int mTextPosy = 0;// y坐标
    private int mTextWidth = 0;// 绘制宽度
    private int mTextHeight = 0;// 绘制高度
    private int mFontHeight = 0;// 绘制字体高度
    private int mFontSize = 24;// 字体大小
    private int mRealLine = 0;// 字符串真实的行数
    private int mLineWidth = 0;//列宽度
    private int TextLength = 0;//字符串长度
    private int oldWidth = 0;//存储旧的width
    private String text = "";//待显示的文字
    private String[] defaultRegular = new String[]{"(", ")", "（", "）", "[", "]", "【", "】", "{", "}"};
    private String[] regulars;
    private Handler mHandler = null;
    private Align textStartAlign = Align.RIGHT;//draw start left or right.//default right

    public VerticalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();//新建画笔
        paint.setTextAlign(Align.CENTER);//文字居中
        paint.setAntiAlias(true);//平滑处理
        paint.setColor(Color.BLACK);//默认文字颜色

        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.vertical_text);
        this.mFontSize = attributes.getDimensionPixelSize(R.styleable.vertical_text_textSize, this.mFontSize);//获取字体大小属性
//        this.text = attributes.getString(R.styleable.vertical_text_text);
        attributes.recycle();

    }

    //设置需要旋转的符号
    public void setRegulars(String[] regulars) {
        this.regulars = regulars;
    }

    public String[] getDefaultRegular() {
        return defaultRegular;
    }

    //设置文字
    public final void setText(String text) {
        this.text = text;
        this.TextLength = text.length();
        if (mTextHeight > 0) GetTextInfo();
    }

    //设置字体大小
    public final void setTextSize(int size) {
        if (size != paint.getTextSize()) {
            mFontSize = size;
            if (mTextHeight > 0) GetTextInfo();
        }
    }

    //设置字体颜色
    public final void setTextColor(int color) {
        paint.setColor(color);
    }

    //设置字体颜色
    public final void setTextARGB(int a, int r, int g, int b) {
        paint.setARGB(a, r, g, b);
    }

    //设置字体
    public void setTypeface(Typeface tf) {
        if (this.paint.getTypeface() != tf) {
            this.paint.setTypeface(tf);
        }
    }

    //设置行宽
    public void setLineWidth(int LineWidth) {
        mLineWidth = LineWidth;
    }

    //获取实际宽度
    public int getTextWidth() {
        return mTextWidth;
    }

    //设置Handler，用以发送事件
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画字
        draw(canvas, this.text);
    }

    private void draw(Canvas canvas, String thetext) {
        char ch;
        mTextPosy = 0;//初始化y坐标
        mTextPosx = textStartAlign == Align.LEFT ? mLineWidth : mTextWidth - mLineWidth;//初始化x坐标
        for (int i = 0; i < this.TextLength; i++) {
            ch = thetext.charAt(i);
            if (ch == '\n') {
                if (textStartAlign == Align.LEFT) {
                    mTextPosx += mLineWidth;// 换列
                } else {
                    mTextPosx -= mLineWidth;// 换列
                }
                mTextPosy = 0;
            } else {
                mTextPosy += mFontHeight;
                if (mTextPosy > this.mTextHeight) {
                    if (textStartAlign == Align.LEFT) {
                        mTextPosx += mLineWidth;// 换列
                    } else {
                        mTextPosx -= mLineWidth;// 换列
                    }
                    i--;
                    mTextPosy = 0;
                } else {
                    String str = String.valueOf(ch);
                    boolean isBreak;
                    if (regulars != null && regulars.length > 0) {
                        isBreak = rotateCanvas(canvas, str, regulars);
                    } else {
                        isBreak = rotateCanvas(canvas, str, defaultRegular);
                    }
                    if (!isBreak) {
                        canvas.drawText(str, mTextPosx, mTextPosy, paint);
                    }
                }
            }
        }
        //调用接口方法
        //activity.getHandler().sendEmptyMessage(TestFontActivity.UPDATE);
    }

    //旋转定义的内容
    private boolean rotateCanvas(Canvas canvas, String str, String[] regulars) {
        boolean isBreak = false;
        for (String regular : regulars) {
            if (regular.equals(str)) {
                paint.setColor(Color.RED);
                canvas.drawCircle(mTextPosx, mTextPosy - mFontHeight / 4, 4, paint);
                paint.setColor(Color.BLACK);
                canvas.save();
                canvas.rotate(90, mTextPosx, mTextPosy - mFontHeight / 4);
                canvas.drawText(str, mTextPosx, mTextPosy, paint);
                canvas.restore();
                isBreak = true;
                break;
            } else {
                isBreak = false;
            }
        }
        return isBreak;
    }

    //计算文字行数和总宽
    private void GetTextInfo() {
        char ch;
        int h = 0;
        paint.setTextSize(mFontSize);
        //获得字宽
        if (mLineWidth == 0) {
            float[] widths = new float[1];
            paint.getTextWidths("正", widths);//获取单个汉字的宽度
//            mLineWidth = (int) Math.ceil(widths[0]);
            mLineWidth = (int) Math.ceil(widths[0] * 1.1 + 2);
        }

        FontMetrics fm = paint.getFontMetrics();
        mFontHeight = (int) (Math.ceil(fm.descent - fm.top) * 0.9);// 获得字体高度

        //计算文字行数
        mRealLine = 0;
        for (int i = 0; i < this.TextLength; i++) {
            ch = this.text.charAt(i);
            if (ch == '\n') {
                mRealLine++;// 真实的行数加一
                h = 0;
            } else {
                h += mFontHeight;
                if (h > this.mTextHeight) {
                    mRealLine++;// 真实的行数加一
                    i--;
                    h = 0;
                } else {
                    if (i == this.TextLength - 1) {
                        mRealLine++;// 真实的行数加一
                    }
                }
            }
        }
        mRealLine++;//额外增加一行
        mTextWidth = mLineWidth * mRealLine;//计算文字总宽度
        measure(mTextWidth, getHeight());//重新调整大小
        layout(getLeft(), getTop(), getLeft() + mTextWidth, getBottom());//重新绘制容器
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredHeight = measureHeight(heightMeasureSpec);
        //int measuredWidth = measureWidth(widthMeasureSpec);
        if (mTextWidth == 0) GetTextInfo();
        setMeasuredDimension(mTextWidth, measuredHeight);
        if (oldWidth != getWidth()) {//
            oldWidth = getWidth();
            if (mHandler != null) mHandler.sendEmptyMessage(LAYOUT_CHANGED);
        }
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 500;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        mTextHeight = result;//设置文本高度

        return result;
    }
    /*
    private int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);  
		int specSize = MeasureSpec.getSize(measureSpec);  
		int result = 500;  
		if (specMode == MeasureSpec.AT_MOST){  
			result = specSize;  
		}else if (specMode == MeasureSpec.EXACTLY){  
			result = specSize;  
		}  
		return result;  
	}  */
}