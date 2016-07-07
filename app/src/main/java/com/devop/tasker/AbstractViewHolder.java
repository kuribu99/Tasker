package com.devop.tasker;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Kong My on 7/7/2016.
 */
public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    protected OnTaskDeletedListener listener;

    public AbstractViewHolder(View itemView) {
        super(itemView);
    }

    public void bindTask(Task task) {

    }

    public void setOnTaskDeletedListener(OnTaskDeletedListener listener) {
        this.listener = listener;
    }

    public interface OnTaskDeletedListener {
        public void onTaskDeleted(Task task);
    }

}
