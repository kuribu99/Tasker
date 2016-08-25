package com.devop.tasker.views;

import android.view.View;
import android.widget.LinearLayout;

import com.devop.tasker.R;

public class EmptyViewHolder extends AbstractViewHolder implements View.OnClickListener {

    private LinearLayout addTaskLayout;

    public EmptyViewHolder(View itemView) {
        super(itemView);

        addTaskLayout = (LinearLayout) itemView.findViewById(R.id.add_task_layout);
        addTaskLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.onTaskActionPerformed(OnTaskActionPerformedListener.ACTION_CLICK, null);
    }
}
