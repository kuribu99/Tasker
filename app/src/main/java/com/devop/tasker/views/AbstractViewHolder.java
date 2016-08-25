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

        int ACTION_CLICK = 0;
        int ACTION_COMPLETE = 1;
        int ACTION_DELETE = 2;

        void onTaskActionPerformed(int actionCode, Task task);

    }

}
