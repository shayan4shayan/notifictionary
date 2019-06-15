package ir.shahinsoft.notifictionary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import ir.shahinsoft.notifictionary.R;

public class AppButton extends AppCompatButton {
    static final int FONT_LIGHT = 0;
    static final int FONT_MEDIUM = 1;
    static final int FONT_REGULAR = 2;
    static final int FONT_BOLD = 3;

    int font = FONT_REGULAR;


    public AppButton(Context context) {
        super(context);
        init(null);
    }

    public AppButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AppButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            updateValues(attrs);
        }
        setFont();
    }

    private void updateValues(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.AppTextView);
        font = array.getIndex(R.styleable.AppTextView_font_name);
        array.recycle();
    }

    private void setFont() {
        Typeface tf = createTypeface(font);
        setTypeface(tf);
    }

    private Typeface createTypeface(int font) {
        Typeface tf;
        if (font == FONT_LIGHT) {
            tf = ResourcesCompat.getFont(getContext(), R.font.prompt_light);
        } else if (font == FONT_MEDIUM) {
            tf = ResourcesCompat.getFont(getContext(), R.font.prompt_medium);
        } else if (font == FONT_REGULAR) {
            tf = ResourcesCompat.getFont(getContext(), R.font.prompt_regular);
        } else {
            tf = ResourcesCompat.getFont(getContext(), R.font.prompt_semi_bold);
        }
        return tf;
    }
}
