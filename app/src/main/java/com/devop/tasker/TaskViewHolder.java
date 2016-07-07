package com.devop.tasker;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Kong My on 7/7/2016.
 */
public class TaskViewHolder extends AbstractViewHolder
        implements View.OnClickListener {

    private final TextView textboxTaskName;
    private final ImageButton buttonDone;
    private final ImageButton buttonDelete;

    private Task bindedTask;

    public TaskViewHolder(View itemView) {
        super(itemView);

        textboxTaskName = (TextView) itemView.findViewById(R.id.textbox_task_name);
        buttonDone = (ImageButton) itemView.findViewById(R.id.button_done);
        buttonDelete = (ImageButton) itemView.findViewById(R.id.button_delete);

        buttonDone.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
    }

    @Override
    public void bindTask(Task task) {
        bindedTask = task;
        textboxTaskName.setText(bindedTask.getTaskName());
        buttonDone.setVisibility(bindedTask.isCompleted() ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        // Ignore if no task is binded
        if (bindedTask == null) return;

        switch (v.getId()) {
            case R.id.button_done:
                bindedTask.setCompleted(true);
                buttonDone.setVisibility(bindedTask.isCompleted() ? View.INVISIBLE : View.VISIBLE);
                break;

            case R.id.button_delete:
                listener.onTaskDeleted(bindedTask);
                break;
        }
    }

}
