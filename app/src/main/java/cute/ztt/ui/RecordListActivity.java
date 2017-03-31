package cute.ztt.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cute.ztt.MyApplication;
import cute.ztt.R;
import cute.ztt.base.BaseRecyclerAdapter;
import cute.ztt.base.RecycleActivity;
import cute.ztt.base.RecyclerViewHolder;
import cute.ztt.db.DBManager;
import cute.ztt.decoration.DividerDecoration;
import cute.ztt.impl.ITaskFinishListener;
import cute.ztt.javabean.RecordInfo;

/**
 * Created by 晖仔(Milo) on 2017-03-31.
 * email:303767416@qq.com
 */

public class RecordListActivity extends RecycleActivity<RecordInfo> {

    private List<RecordInfo> listDatas = new ArrayList<>();

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, RecordListActivity.class);
        return intent;
    }

    @Override
    protected BaseRecyclerAdapter getAdapter() {
        return new MyAdapter(this, listDatas, null);
    }

    @Override
    protected BaseRecyclerAdapter.OnItemClickListener getItemListener() {
        return new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object data) {
                RecordInfo info = (RecordInfo) data;
                startActivity(DrawActivity.createIntent(RecordListActivity.this, DBManager.getInstance(getBaseContext()).getLocationList(info.id)));
            }
        };
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new DividerDecoration(getResources(), R.color.black_dft, R.dimen.divider_dft, LinearLayout.VERTICAL);
    }

    @Override
    protected void init() {
        super.init();
    }

    class MyAdapter extends BaseRecyclerAdapter<RecordInfo> {

        public MyAdapter(Context context, List<RecordInfo> datas, @Nullable Bundle bundle) {
            super(context, datas, R.layout.item_record, bundle);
        }

        @Override
        protected void bindViewHolder(int position, RecyclerViewHolder holder) {
            RecordInfo itemData = getItem(position);

            TextView tvStart = holder.getView(R.id.tv_starttime, false);
            TextView tvEnd = holder.getView(R.id.tv_endtime, false);
            TextView tvCount = holder.getView(R.id.tv_count, false);

            tvStart.setText("开始时间:" + itemData.getStartTime());
            tvEnd.setText("结束时间:" + itemData.getStartTime());
            if (itemData.locationCount != 0) {
                tvCount.setText(String.format(getString(R.string.count_of_location), itemData.locationCount));
            } else {
                tvCount.setText("非正常结束");
            }
        }

        @Override
        protected void loadData(int page, final ITaskFinishListener iTaskFinishListener) {
            final List<RecordInfo> list = DBManager.getInstance(context).getRecordList(page);
            if (list == null || list.isEmpty()) {
                iTaskFinishListener.onFail(TAG, "data is null ... ");
            } else {
                MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iTaskFinishListener.onSuccess(TAG, list);
                    }
                }, 500);
            }
        }

        @Override
        protected void setPageSize(int pageSize) {

        }

    }


}
