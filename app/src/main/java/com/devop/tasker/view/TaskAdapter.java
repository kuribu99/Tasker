package com.devop.tasker.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.devop.tasker.R;
import com.devop.tasker.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kong My on 7/7/2016.
 */
public class TaskAdapter extends RecyclerView.Adapter<AbstractViewHolder>
        implements AbstractViewHolder.OnTaskDeletedListener {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_TASK = 1;

    private List<Task> taskList;

    public TaskAdapter() {
        this.taskList = new ArrayList<>();
    }

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        this.notifyDataSetChanged();
    }

    @Override
    public AbstractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AbstractViewHolder viewHolder = null;

        switch (viewType) {
            case VIEW_TYPE_EMPTY:
                viewHolder = new EmptyViewHolder(inflater.inflate(R.layout.empty_task_view_holder, parent, false));
                break;

            case VIEW_TYPE_TASK:
                viewHolder = new TaskViewHolder(inflater.inflate(R.layout.task_view_holder, parent, false));
                viewHolder.setOnTaskDeletedListener(this);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AbstractViewHolder holder, int position) {
        if (taskList.size() == 0)
            holder.bindTask(null);
        else
            holder.bindTask(taskList.get(position));
    }

    @Override
    public int getItemCount() {
        if (taskList.size() == 0)
            return 1;
        else
            return taskList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (taskList.size() == 0)
            return VIEW_TYPE_EMPTY;
        else
            return VIEW_TYPE_TASK;
    }

    @Override
    public void onTaskDeleted(Task task) {
        int index = taskList.indexOf(task);
        if (index >= 0) {
            taskList.remove(task);
            notifyItemRemoved(index);
        }
    }

}
