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

public class GroupNavigationAdapter extends BaseAdapter {

    private final Context context;
    private List<Group> groupList;

    public GroupNavigationAdapter(Context context) {
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
        return groupList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0)
            return Group.ALL_TASK_GROUP;
        else
            return groupList.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        if (position == 0)
            return Group.ALL_TASK_GROUP_ID;
        else
            return groupList.get(position - 1).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_nav_group, parent, false);
            view.setTag(view.findViewById(R.id.text_view));
        }

        TextView textView = (TextView) view.getTag();
        textView.setText(((Group) getItem(position)).getGroupName());

        return view;
    }

    public void addGroup(Group group) {
        groupList.add(group);
        notifyDataSetChanged();
    }

    public boolean hasGroupName(String groupName) {
        groupName = groupName.toLowerCase();

        for (Group group : groupList)
            if (group.getGroupName().toLowerCase().equals(groupName))
                return true;

        return false;
    }

    public void removeGroupAt(int position) {
        if (position > 0) {
            groupList.remove(position - 1);
            notifyDataSetChanged();
        }
    }
}
