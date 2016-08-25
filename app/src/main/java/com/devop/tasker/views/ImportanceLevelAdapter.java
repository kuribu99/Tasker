package com.devop.tasker.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.devop.tasker.R;
import com.devop.tasker.models.Task;

public class ImportanceLevelAdapter extends BaseAdapter {

    private final Context context;
    private final int[] levels;

    public ImportanceLevelAdapter(Context context) {
        this.context = context;

        levels = new int[]{
                Task.Importance.LOW,
                Task.Importance.NORMAL,
                Task.Importance.IMPORTANT,
        };
    }

    @Override
    public int getCount() {
        return levels.length;
    }

    @Override
    public Object getItem(int position) {
        return levels[position];
    }

    @Override
    public long getItemId(int position) {
        return levels[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_importance_level, parent, false);
            view.setTag(view.findViewById(R.id.text_view));
        }

        TextView textView = (TextView) view.getTag();
        textView.setText(Task.Importance.getStringResource((int) getItemId(position)));

        return view;
    }
}
