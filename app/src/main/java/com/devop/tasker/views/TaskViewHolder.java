package com.devop.tasker.views;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devop.tasker.R;
import com.devop.tasker.models.Task;

public class TaskViewHolder extends AbstractViewHolder
        implements View.OnClickListener {

    private final LinearLayout taskViewLayout;
    private final TextView textboxTaskName;
    private final ImageButton buttonDone;
    private final ImageButton buttonDelete;

    private Task bindedTask;

    public TaskViewHolder(Context context, View itemView) {
        super(itemView);

        taskViewLayout = (LinearLayout) itemView.findViewById(R.id.task_view_layout);
        textboxTaskName = (TextView) itemView.findViewById(R.id.textbox_task_name);
        buttonDone = (ImageButton) itemView.findViewById(R.id.button_done);
        buttonDelete = (ImageButton) itemView.findViewById(R.id.button_delete);

        taskViewLayout.setOnClickListener(this);
        buttonDone.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
    }

    @Override
    public void bindTask(Task task) {
        bindedTask = task;
        textboxTaskName.setText(bindedTask.getTitle());
        buttonDone.setVisibility(bindedTask.getStatus() == Task.Status.COMPLETED ? View.INVISIBLE : View.VISIBLE);
        updateBackgroundColor();
    }

    private void updateBackgroundColor() {
        taskViewLayout.setBackgroundResource(getTaskBackgroundColor(bindedTask.getStatus(), bindedTask.getImportance()));
    }

    private int getTaskBackgroundColor(int status, int importance) {
        int color = R.drawable.background_task_completed;

        // Show color based on status and completion status
        switch (status) {
            case Task.Status.PENDING:
                switch (importance) {

                    case Task.Importance.LOW:
                        color = R.drawable.background_task_pending_low;
                        break;

                    case Task.Importance.NORMAL:
                        color = R.drawable.background_task_pending_normal;
                        break;

                    case Task.Importance.IMPORTANT:
                        color = R.drawable.background_task_pending_important;
                        break;

                }
                break;

            case Task.Status.OVERDUE:
                switch (importance) {

                    case Task.Importance.LOW:
                        color = R.drawable.background_task_overdue_low;
                        break;

                    case Task.Importance.NORMAL:
                        color = R.drawable.background_task_overdue_normal;
                        break;

                    case Task.Importance.IMPORTANT:
                        color = R.drawable.background_task_overdue_important;
                        break;

                }
                break;

        }
        return color;
    }

    @Override
    public void onClick(View v) {
        // Ignore if no task is binded
        if (bindedTask == null) return;

        switch (v.getId()) {
            case R.id.task_view_layout:
                listener.onTaskActionPerformed(OnTaskActionPerformedListener.ACTION_CLICK, bindedTask);
                break;

            case R.id.button_done:
                listener.onTaskActionPerformed(OnTaskActionPerformedListener.ACTION_COMPLETE, bindedTask);

                buttonDone.setVisibility(View.INVISIBLE);
                updateBackgroundColor();
                break;

            case R.id.button_delete:
                listener.onTaskActionPerformed(OnTaskActionPerformedListener.ACTION_DELETE, bindedTask);
                break;
        }
    }

}
