package com.tikotapps.pathfinder.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tikotapps.pathfinder.Database.Task;
import com.tikotapps.pathfinder.R;
import com.tikotapps.pathfinder.ViewHolders.DisplayTaskViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by neel on 04/11/15 at 4:28 PM.
 */
public class DisplayTaskAdapter extends RecyclerView.Adapter<DisplayTaskViewHolder> {

    private ArrayList<Task> taskList;
    private LayoutInflater inflater;

    public DisplayTaskAdapter(Context context, ArrayList<Task> taskList) {
        this.taskList = taskList;
        inflater = LayoutInflater.from(context);
    }

    public void updateAdapter(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public DisplayTaskViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.display_tasks, viewGroup, false);

        return new DisplayTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DisplayTaskViewHolder displayTaskViewHolder, int i) {
        Task task = taskList.get(i);

        displayTaskViewHolder.displayTask.setText(task.task);
        displayTaskViewHolder.displayTaskLocation.setText(task.name);

        if (task.time_required > 3600) {
            if (task.time_required % 3600 == 0) {
                if (task.time_required / 3600 == 1) {
                    displayTaskViewHolder.displayTaskTime.setText((task.time_required / 3600 + " hour"));
                } else {
                    displayTaskViewHolder.displayTaskTime.setText((task.time_required / 3600 + " hours"));
                }
            } else {
                if (task.time_required / 3600 == 1) {
                    if ((task.time_required % 3600) / 60 == 1) {
                        displayTaskViewHolder.displayTaskTime.setText((task.time_required / 3600 + " hour " + (task.time_required % 3600) / 60 + " minute"));
                    } else {
                        displayTaskViewHolder.displayTaskTime.setText((task.time_required / 3600 + " hour " + (task.time_required % 3600) / 60 + " minutes"));
                    }
                } else {
                    if ((task.time_required % 3600) / 60 == 1) {
                        displayTaskViewHolder.displayTaskTime.setText((task.time_required / 3600 + " hours " + (task.time_required % 3600) / 60 + " minute"));
                    } else {
                        displayTaskViewHolder.displayTaskTime.setText((task.time_required / 3600 + " hours " + (task.time_required % 3600) / 60 + " minutes"));
                    }
                }
            }
        } else if (task.time_required > 60) {
            if (task.time_required / 60 == 1) {
                displayTaskViewHolder.displayTaskTime.setText(task.time_required / 60 + " minute");
            } else {
                displayTaskViewHolder.displayTaskTime.setText(task.time_required / 60 + " minutes");
            }
        } else {
            if (task.time_required == 1) {
                displayTaskViewHolder.displayTaskTime.setText(task.time_required + " second");
            } else {
                displayTaskViewHolder.displayTaskTime.setText(task.time_required + " seconds");
            }
        }

        Date deadline = new Date(task.deadline * 1000);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String date = format.format(deadline);

        int month = Integer.parseInt(date.substring(3, 5));
        switch (month) {
            case 0:
                date = "January " + date.substring(0, 2) + ", " + date.substring(6);
                break;
            case 1:
                date = "February " + date.substring(0, 2) + ", " + date.substring(6);
                break;
            case 2:
                date = "March " + date.substring(0, 2) + ", " + date.substring(6);
                break;
            case 3:
                date = "April " + date.substring(0, 2) + ", " + date.substring(6);
                break;
            case 4:
                date = "May " + date.substring(0, 2) + ", " + date.substring(6);
                break;
            case 5:
                date = "June " + date.substring(0, 2) + ", " + date.substring(6);
                break;
            case 6:
                date = "July " + date.substring(0, 2) + ", " + date.substring(6);
                break;
            case 7:
                date = "August " + date.substring(0, 2) + ", " + date.substring(6);
                break;
            case 8:
                date = "September " + date.substring(0, 2) + ", " + date.substring(6);
                break;
            case 9:
                date = "October " + date.substring(0, 2) + ", " + date.substring(6);
                break;
            case 10:
                date = "November " + date.substring(0, 2) + ", " + date.substring(6);
                break;
            case 11:
                date = "December " + date.substring(0, 2) + ", " + date.substring(6);
                break;
        }

        displayTaskViewHolder.displayTaskDeadline.setText(date);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
