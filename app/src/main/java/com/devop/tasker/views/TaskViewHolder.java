package com.devop.tasker.views;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.devop.tasker.R;
import com.devop.tasker.models.Task;

/**
 * Created by Kong My on 7/7/2016.
 */
public class TaskViewHolder extends AbstractViewHolder
        implements View.OnClickListener {

    private final TextView textboxTaskName;
    private final ImageButton buttonDone;
    private final ImageButton buttonDelete;
    private final Context context;

    private Task bindedTask;

    public TaskViewHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        textboxTaskName = (TextView) itemView.findViewById(R.id.textbox_task_name);
        buttonDone = (ImageButton) itemView.findViewById(R.id.button_done);
        buttonDelete = (ImageButton) itemView.findViewById(R.id.button_delete);

        buttonDone.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
    }

    @Override
    public void bindTask(Task task) {
        bindedTask = task;
        textboxTaskName.setText(bindedTask.getTitle());
        buttonDone.setVisibility(bindedTask.getStatus() == Task.Status.COMPLETED ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        // Ignore if no task is binded
        if (bindedTask == null) return;

        switch (v.getId()) {
            case R.id.button_done:
                buttonDone.setVisibility(View.INVISIBLE);
                listener.onTaskCompleted(bindedTask);
                break;

            case R.id.button_delete:
                listener.onTaskDeleted(bindedTask);
                break;
        }
    }

}
