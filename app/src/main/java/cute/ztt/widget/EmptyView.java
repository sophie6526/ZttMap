package cute.ztt.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cute.ztt.R;


/**
 * Created by 晖仔(Milo) on 2016/12/28.
 * email:303767416@qq.com
 */

public final class EmptyView extends LinearLayout {

    public static final int INVALID_ID = 0;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        setOrientation(HORIZONTAL);
        inflate(getContext(), R.layout.layout_empty, this);
    }

    /**
     * 设置文字描述
     *
     * @param text
     */
    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            ((TextView) findViewById(R.id.tv_nodata)).setText("");
            findViewById(R.id.tv_nodata).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.tv_nodata)).setText(text);
            findViewById(R.id.tv_nodata).setVisibility(View.VISIBLE);
        }
    }

}
