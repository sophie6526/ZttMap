package cute.ztt.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import cute.ztt.R;
import cute.ztt.widget.EmptyView;

/**
 * 包含RecycleView的基类
 * <p>
 * Created by 晖仔(Milo) on 2017/2/28.
 * email:303767416@qq.com
 */

public abstract class RecycleActivity<T> extends AppActivity implements BaseRecyclerAdapter.OnDataLoadFinish {

    @BindView(R.id.lay_swipe)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private EmptyView emptyView;
    private BaseRecyclerAdapter<T> adapter;

    protected abstract BaseRecyclerAdapter getAdapter();

    protected abstract BaseRecyclerAdapter.OnItemClickListener getItemListener();

    protected abstract RecyclerView.ItemDecoration getItemDecoration();

    protected View.OnClickListener getEmptyViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.loadDataFirstTime();
            }
        };
    }

    protected void init() {
        emptyView = (EmptyView) findViewById(R.id.emptyview);
        emptyView.setOnClickListener(getEmptyViewOnClickListener());
        adapter = getAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.loadDataFirstTime();
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setOnDataLoadFinish(this);
        adapter.loadDataFirstTime();
        if (getItemListener() != null) {
            adapter.setOnItemClickListener(getItemListener());
        }
        if (getItemDecoration() != null) {
            recyclerView.addItemDecoration(getItemDecoration());
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_recycle;
    }

    @Override
    public void loadDataFinished(int datasLength) {
        swipeLayout.setRefreshing(false);
        if (datasLength == 0) {
            emptyView.setVisibility(View.VISIBLE);
            swipeLayout.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            swipeLayout.setVisibility(View.VISIBLE);
        }
    }

}
