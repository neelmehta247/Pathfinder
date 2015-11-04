package com.tikotapps.pathfinder.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tikotapps.pathfinder.R;

/**
 * Created by neel on 04/11/15 at 4:23 PM.
 */
public class DisplayTaskViewHolder extends RecyclerView.ViewHolder {

    public TextView displayTask, displayTaskLocation, displayTaskTime, displayTaskDeadline;

    public DisplayTaskViewHolder(View itemView) {
        super(itemView);
        displayTask = (TextView) itemView.findViewById(R.id.displayTaskText);
        displayTaskLocation = (TextView) itemView.findViewById(R.id.displayTaskLocationText);
        displayTaskDeadline = (TextView) itemView.findViewById(R.id.displayTaskDeadlineText);
        displayTaskTime = (TextView) itemView.findViewById(R.id.displayTaskTimeText);
    }
}
