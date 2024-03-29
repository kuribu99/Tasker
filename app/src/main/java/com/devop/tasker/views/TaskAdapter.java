package com.devop.tasker.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.devop.tasker.R;
import com.devop.tasker.db.DatabaseHelper;
import com.devop.tasker.models.Group;
import com.devop.tasker.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<AbstractViewHolder>
        implements AbstractViewHolder.OnTaskActionPerformedListener {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_TASK = 1;
    private final Context context;
    private final AbstractViewHolder.OnTaskActionPerformedListener listener;

    private List<Task> taskList;

    public TaskAdapter(Context context, AbstractViewHolder.OnTaskActionPerformedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void refresh(int groupID) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        if (groupID == Group.ALL_TASK_GROUP_ID)
            taskList = Task.findAll(databaseHelper);
        else
            taskList = Task.findByGroup(databaseHelper, groupID);

        databaseHelper.close();
        notifyDataSetChanged();
    }

    @Override
    public AbstractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AbstractViewHolder viewHolder = null;

        switch (viewType) {
            case VIEW_TYPE_EMPTY:
                viewHolder = new EmptyViewHolder(inflater.inflate(R.layout.view_holder_empty, parent, false));
                break;

            case VIEW_TYPE_TASK:
                viewHolder = new TaskViewHolder(context, inflater.inflate(R.layout.view_holder_task, parent, false));
                break;
        }
        viewHolder.setOnTaskActionPerformedListener(this);

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
    public void onTaskActionPerformed(int actionCode, Task task) {
        // Escalate to parent listener
        listener.onTaskActionPerformed(actionCode, task);
    }

    public void deleteTask(Task task) {
        int index = taskList.indexOf(task);
        if (index >= 0) {
            taskList.remove(task);
            notifyItemRemoved(index);
        }
    }
}
