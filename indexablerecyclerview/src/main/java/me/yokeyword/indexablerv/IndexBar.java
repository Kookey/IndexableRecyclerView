package me.yokeyword.indexablerv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.yokeyword.indexablerecyclerview.R;

/**
 * Created by YoKey on 16/10/6.
 */
class IndexBar extends View {
    private static int MARGIN;
    private static int mTotalHeight;

    private List<String> mIndexList = new ArrayList<>();
    // 首字母 到 mIndexList 的映射
    private HashMap<String, Integer> mMapping = new HashMap<>();
    private ArrayList<EntityWrapper> mDatas;

    private int mSelectionPosition;
    private float mIndexHeight;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public IndexBar(Context context) {
        super(context);
        init();
    }

    private void init() {
        MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
    }

    void init(int barTextColor, int barFocusTextColor, float barTextSize) {
        mPaint.setColor(barTextColor);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(barTextSize);

        mFocusPaint.setTextAlign(Paint.Align.CENTER);
        mFocusPaint.setTextSize(barTextSize + 1);
        mFocusPaint.setColor(barFocusTextColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);

        mTotalHeight = (int) ((mIndexList.size() - 1) * mPaint.getTextSize()
                + mFocusPaint.getTextSize())
                + (mIndexList.size() + 1) * MARGIN;

        if (mode == MeasureSpec.AT_MOST) {
            int maxWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
            super.onMeasure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mTotalHeight, MeasureSpec.EXACTLY));
            return;
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mTotalHeight, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIndexList.size() == 0) return;

        mIndexHeight = ((float) getHeight()) / mIndexList.size();

        int currentHeight = (getHeight() - mTotalHeight) / 2;
        for (int i = 0; i < mIndexList.size(); i++) {
            if (mSelectionPosition == i) {
                currentHeight += (mFocusPaint.getTextSize() + MARGIN);
                canvas.drawText(mIndexList.get(i), getWidth() / 2, currentHeight, mFocusPaint);
            } else {
                currentHeight += (mPaint.getTextSize() + MARGIN);
                canvas.drawText(mIndexList.get(i), getWidth() / 2, currentHeight, mPaint);
            }
        }
    }

    /**
     * 根据Y坐标判断 位置
     */
    int getPositionForPoint(float y) {
        if (mIndexList.size() <= 0) return -1;

        int position = (int) (y / mIndexHeight);

        if (position < 0) {
            position = 0;
        } else if (position > mIndexList.size() - 1) {
            position = mIndexList.size() - 1;
        }

        return position;
    }


    int getSelectionPosition() {
        return mSelectionPosition;
    }

    void setSelectionPosition(int position) {
        this.mSelectionPosition = position;
        invalidate();
    }

    int getRecyPosition() {
        String index = mIndexList.get(mSelectionPosition);
        if (mMapping.containsKey(index)) {
            return mMapping.get(index);
        }
        return -1;
    }

    List<String> getIndexList() {
        return mIndexList;
    }

    /**
     * 绑定Index数据
     */
    void setDatas(boolean showAllLetter, ArrayList<EntityWrapper> datas) {
        this.mDatas = datas;
        if (showAllLetter) {
            mIndexList = Arrays.asList(getResources().getStringArray(R.array.indexable_letter));

        }
        for (int i = 0; i < datas.size(); i++) {
            EntityWrapper wrapper = datas.get(i);
            if (wrapper.getItemType() == EntityWrapper.TYPE_INDEX) {
                if (!showAllLetter) {
                    mIndexList.add(wrapper.getIndex());
                }
                mMapping.put(wrapper.getIndex(), i);
            }
        }
        requestLayout();
    }

    void setSelection(int firstVisibleItemPosition) {
        EntityWrapper wrapper = mDatas.get(firstVisibleItemPosition);
        int position = mIndexList.indexOf(wrapper.getIndex());

        if (mSelectionPosition != position) {
            mSelectionPosition = position;
            invalidate();
        }
    }
}
