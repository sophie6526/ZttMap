package cute.ztt.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cute.ztt.R;
import cute.ztt.impl.ITaskFinishListener;
import cute.ztt.utils.NetworkUtils;


public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> implements View.OnClickListener, RecyclerViewHolder.OnItemViewClickListener {

    private static final String TAG = "BaseRecyclerAdapter";

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOTER = 2;
    public static final int TYPE_HEADER = 3;

    protected OnItemClickListener onItemClickListener;
    protected OnViewClickListener onViewClickListener;

    private LayoutInflater inflater;

    protected View headerView;
    protected View footerView;

    protected List<T> datas;
    protected Context context;

    protected int headerViewCount;
    protected int footerViewCount;
    protected int itemRes;
    protected Bundle bundle;

    // 分页加载
    private int currentPage = 1;//当前显示第几页，默认为1
    protected int NUMS = 10;//每页显示数据条数
    private boolean noMore = false;//没有更多数据了
    private boolean loadFail = false;//数据加载失败
    private boolean isLoadingData = false;//正在加载数据，不要在请求了

    private MyTaskFinishListener myTaskFinishListener;

    public BaseRecyclerAdapter(Context context, List<T> datas, int itemLayoutResourse, @Nullable Bundle bundle) {
        this.datas = datas;
        this.context = context;
        this.itemRes = itemLayoutResourse;
        this.bundle = bundle;
        inflater = LayoutInflater.from(context);
        myTaskFinishListener = new MyTaskFinishListener();
        footerView = inflater.inflate(R.layout.layout_footview, null);//这里默认就让每个recyclerview有footer
        setFooterView(footerView);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_NORMAL:
                view = inflater.inflate(itemRes, parent, false);
                break;
            case TYPE_FOOTER:
                view = footerView;
                break;
            case TYPE_HEADER:
                view = headerView;
                break;
            default:
                view = inflater.inflate(itemRes, parent, false);
                break;
        }
        view.setOnClickListener(this);
        return new RecyclerViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        if (isFooter(position)) {
            bindFooter(holder);
        } else if (isHeader(position)) {
            bindHeader(holder);
        } else {
            final int index = position - headerViewCount;
            if (index < datas.size()) {
                bindViewHolder(index, holder);
                View itemView = holder.getItemView();
                final T data = datas.get(index);
                itemView.setTag(data);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size() + headerViewCount + footerViewCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooter(position)) {
            return TYPE_FOOTER;
        } else if (isHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL;
        }
    }

    public T getItem(int position) {
        return datas.get(position);
    }

    public int getPosition(T data) {
        return datas.indexOf(data) + headerViewCount;
    }

    protected abstract void bindViewHolder(int position, RecyclerViewHolder holder);

    protected abstract void loadData(int page, ITaskFinishListener iTaskFinishListener);

    protected abstract void setPageSize(int pageSize);

    protected void bindHeader(RecyclerViewHolder holder) {
        holder.getItemView().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    protected void bindFooter(final RecyclerViewHolder holder) {
        holder.getItemView().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final TextView textView = holder.getView(R.id.tv_content, false);
        final ProgressBar progressBar = holder.getView(R.id.progress_bar, false);
        if (!NetworkUtils.isNetworkAvailable(context)) {
            textView.setText(R.string.network_unbale);
            progressBar.setVisibility(View.GONE);
            return;
        } else if (loadFail) {
            loadFail = false;
            textView.setText(R.string.load_fail);
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!noMore && !isLoadingData && currentPage != 1) {
            isLoadingData = true;
            textView.setText(R.string.text_loading);
            progressBar.setVisibility(View.VISIBLE);
            loadData(currentPage, myTaskFinishListener);
        }
        if (noMore || datas.size() == 0) {
            holder.getItemView().setVisibility(View.GONE);//TODO 请将加载中改为没有更多数据了哦
        } else {
            holder.getItemView().setVisibility(View.VISIBLE);
        }
    }

    private class MyTaskFinishListener implements ITaskFinishListener {

        @Override
        public void onSuccess(String taskName, Object object) {
            loadFail = false;
            if (object != null && object instanceof List) {
                List<T> list = (List<T>) object;
                if (list != null) {
                    if (list.size() < NUMS) {
                        noMore = true;
                    }

                    if (list.size() == 0 && currentPage == 1) {//TODO 第一页,没有数据时,清空Datas并刷新adapter
                        datas.clear();
                        notifyDataSetChanged();
                    } else if (list.size() > 0) {
                        currentPage++;

                        if (currentPage == 2) {
                            datas.clear();
                        }
                        datas.addAll(list);
                        notifyDataSetChanged();
                    } else if (list.size() == 0) {
                        footerViewCount = 0;
                        notifyDataSetChanged();
                    }
                }
            }
            isLoadingData = false;
            loadDataFinish(true);
        }

        @Override
        public void onFail(String taskName, String error) {
            loadFail = true;
            isLoadingData = false;
            loadDataFinish(false);

            if (isFooter(getItemCount() - 1)) {
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 首次加载数据
     */
    public void loadDataFirstTime() {
        if (!isLoadingData) {
            noMore = false;
            currentPage = 1;
            loadData(currentPage, myTaskFinishListener);
            isLoadingData = true;
        }
    }

    protected boolean isFooter(int position) {
        if (footerView == null) {
            return false;
        }
        return position == getItemCount() - 1;
    }

    protected boolean isHeader(int position) {
        if (headerView == null) {
            return false;
        }
        return position == 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public void setHeaderView(View headerView) {
        if (this.headerView == null) {
            if (headerView != null) {
                this.headerView = headerView;
                this.headerViewCount++;
                notifyItemInserted(0);
            }
        }
    }

    public void setFooterView(View footerView) {
        if (footerView != null) {
            this.footerView = footerView;
            this.footerViewCount = 1;
        }
    }

    public void removeHeaderView() {
        if (headerViewCount > 0) {
            this.headerView = null;
            this.headerViewCount--;
            this.notifyItemRemoved(0);
        }
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, v.getTag());
        }
    }

    @Override
    public void onViewClick(View view) {
        if (onViewClickListener != null) {
            onViewClickListener.onViewClick(view, view.getTag());
        }
    }

    /**
     * 整个Item点击事件
     *
     * @param <T>
     */
    public interface OnItemClickListener<T> {
        void onItemClick(View view, T data);
    }

    /**
     * 整个Item中的子view设置点击事件
     * 注意：必须在Adapter中setTag，否则这里data没有数据，目前还没有更好的解决办法，如有请告知
     *
     * @param <T>
     */
    public interface OnViewClickListener<T> {
        void onViewClick(View view, T data);
    }

    private OnDataLoadFinish onDataLoadFinish;

    public void setOnDataLoadFinish(OnDataLoadFinish onDataLoadFinish) {
        this.onDataLoadFinish = onDataLoadFinish;
    }

    public OnDataLoadFinish getOnDataLoadFinish() {
        return onDataLoadFinish;
    }

    public interface OnDataLoadFinish {
        void loadDataFinished(int datasLength);
    }

    private void loadDataFinish(boolean success) {
        if (onDataLoadFinish != null) {
            onDataLoadFinish.loadDataFinished(datas.size());
        }
    }
}
