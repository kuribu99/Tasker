package com.devop.tasker.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.devop.tasker.models.Task;

/**
 * Created by Kong My on 7/7/2016.
 */
public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    protected OnTaskActionPerformedListener listener;

    public AbstractViewHolder(View itemView) {
        super(itemView);
    }

    public void bindTask(Task task) {

    }

    public void setOnTaskActionPerformedListener(OnTaskActionPerformedListener listener) {
        this.listener = listener;
    }

    public interface OnTaskActionPerformedListener {

        void onTaskCompleted(Task task);

        void onTaskDeleted(Task task);

    }

}
