package com.neeravtanay.neuratask.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.models.NotificationModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private final Context context;
    private List<NotificationModel> notifications;
    private final OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onClick(NotificationModel notification);
    }

    public NotificationsAdapter(Context context, List<NotificationModel> notifications, OnNotificationClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel n = notifications.get(position);
        holder.title.setText(n.title);
        holder.message.setText(n.message);

        // Timestamp formatting
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
        holder.time.setText(sdf.format(new Date(n.timestamp)));

        // Style unread notifications differently
        holder.container.setBackgroundColor(n.read ? Color.TRANSPARENT : Color.parseColor("#E3F2FD"));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(n);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    // Method to update adapter data dynamically
    public void setNotifications(List<NotificationModel> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;
        LinearLayout container;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            message = itemView.findViewById(R.id.tvMessage);
            time = itemView.findViewById(R.id.tvTime);
            container = itemView.findViewById(R.id.notificationContainer);
        }
    }
}
