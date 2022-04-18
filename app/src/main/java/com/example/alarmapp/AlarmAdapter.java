package com.example.alarmapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


public class AlarmAdapter extends ListAdapter<Alarm, AlarmAdapter.ViewHolder> {
    // private ArrayList<Alarm> alarmArrayList;
    Context context;
    DeleteEditAlarmInterface deleteEditAlarmInterface;

    public interface DeleteEditAlarmInterface {
        void deleteAlarm(Alarm alarm);

        void editAlarm(Alarm alarm);
    }

    public AlarmAdapter(Context context, DeleteEditAlarmInterface deleteEditAlarmInterface) {
        super(DiffCallback);
        // this.alarmArrayList = alarmArrayList;
        this.context = context;
        this.deleteEditAlarmInterface = deleteEditAlarmInterface;
    }

    private static final DiffUtil.ItemCallback<Alarm> DiffCallback = new DiffUtil.ItemCallback<Alarm>() {
        @Override
        public boolean areItemsTheSame(@NonNull Alarm oldItem, @NonNull Alarm newItem) {
            return oldItem.getId() == newItem.getId();
            // return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Alarm oldItem, @NonNull Alarm newItem) {
            if (oldItem.getPendingIntentRequestCode() == newItem.getPendingIntentRequestCode() &&
                    oldItem.getTime() == newItem.getTime() &&
                    oldItem.getSnoozeCount() == newItem.getSnoozeCount() &&
                    oldItem.getSnoozeTime() == newItem.getSnoozeTime() &&
                    oldItem.getName().equals(newItem.getName()) &&
                    oldItem.isSnoozeOn() == newItem.isSnoozeOn())
                return true;
            return false;
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.alarm_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Alarm alarm = getItem(position);
        holder.timeTextView.setText(alarm.getTimeInFormat());
        holder.alarmDateTextView.setText(alarm.getTimeInFormat("EEE, MMM dd"));
        if (alarm.getName() != null && !alarm.getName().isEmpty()){
            holder.alarmNameTextView.setVisibility(View.VISIBLE);
            holder.alarmNameTextView.setText(alarm.getName());
        }
        if (alarm.isSnoozeOn()) {
            holder.snoozeCountTextView.setVisibility(View.VISIBLE);
            holder.snoozeCountTextView.setText(context.getString(R.string.timerSnoozingText) + " " + alarm.getTimeInFormtAfterSnoozing());
        }else{
            holder.snoozeCountTextView.setVisibility(View.GONE);
        }


        holder.deleteAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEditAlarmInterface.deleteAlarm(alarm);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEditAlarmInterface.editAlarm(alarm);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView timeTextView;
        Button deleteAlarmButton;
        TextView snoozeCountTextView;
        TextView alarmDateTextView;
        TextView alarmNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.timeTextView = itemView.findViewById(R.id.alarmTimeTextView);
            this.deleteAlarmButton = itemView.findViewById(R.id.deleteAlarmButton);
            this.snoozeCountTextView = itemView.findViewById(R.id.snoozingTextView);
            this.alarmDateTextView = itemView.findViewById(R.id.alarmDateTextView);
            this.alarmNameTextView = itemView.findViewById(R.id.alarmNameTextView);
        }

    }

}
