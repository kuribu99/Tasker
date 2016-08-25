package com.devop.tasker.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.devop.tasker.R;
import com.devop.tasker.db.DatabaseHelper;
import com.devop.tasker.models.Group;

import java.util.List;

public class GroupDropdownAdapter extends BaseAdapter {

    private final Context context;
    private List<Group> groupList;

    public GroupDropdownAdapter(Context context) {
        this.context = context;
        refresh();
    }

    public void refresh() {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        groupList = Group.findAll(databaseHelper);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Object getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return groupList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_task_group, parent, false);
            view.setTag(view.findViewById(R.id.text_view));
        }

        TextView textView = (TextView) view.getTag();
        textView.setText(groupList.get(position).getGroupName());

        return view;
    }

    public int getPositionFromID(int id) {
        for (int i = 0; i < groupList.size(); i++)
            if (groupList.get(i).getId() == id)
                return i;
        return 0;
    }
}
