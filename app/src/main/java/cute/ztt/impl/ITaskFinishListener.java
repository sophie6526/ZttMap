package cute.ztt.impl;

/**
 * 列表数据加载完成回调接口
 */
public interface ITaskFinishListener {
    void onSuccess(String taskName, Object object);

    void onFail(String taskName, String error);
}
