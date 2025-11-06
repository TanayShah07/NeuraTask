package com.neeravtanay.neuratask.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.models.NotificationModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    public interface OnNotificationClickListener {
        void onClick(NotificationModel notification);
    }

    private final Context context;
    private final OnNotificationClickListener listener;
    private List<NotificationModel> notifications = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable updater;

    public NotificationsAdapter(Context context, List<NotificationModel> notifications, OnNotificationClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;

        // 🔁 Auto-update every minute
        updater = new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                handler.postDelayed(this, 30 * 1000); // every 30 seconds
            }
        };
        handler.post(updater);
    }

    public void setNotifications(List<NotificationModel> newList) {
        this.notifications = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel n = notifications.get(position);

        holder.title.setText(n.title);
        holder.message.setText(n.message);

        // ---- Format timestamp ----
        long now = System.currentTimeMillis();
        long diff = n.timestamp - now;

        String dueIn;
        if (diff <= 0) {
            dueIn = "⏰ Due now or past";
        } else if (diff < TimeUnit.HOURS.toMillis(1)) {
            dueIn = "⏰ Due in " + (diff / TimeUnit.MINUTES.toMillis(1)) + " min";
        } else if (diff < TimeUnit.DAYS.toMillis(1)) {
            dueIn = "⏰ Due in " + (diff / TimeUnit.HOURS.toMillis(1)) + " hr";
        } else {
            dueIn = "⏰ Due in " + (diff / TimeUnit.DAYS.toMillis(1)) + " days";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy, hh:mm a", Locale.getDefault());
        String dateText = "🗓️ Due date: " + sdf.format(new Date(n.timestamp));

        holder.timestamp.setText(dueIn + "\n" + dateText);
        holder.itemView.setAlpha(n.read ? 0.6f : 1.0f);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(n);
        });
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    // ✅ Stop auto-updating when adapter detached to save battery
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        handler.removeCallbacks(updater);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, timestamp;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            message = itemView.findViewById(R.id.tvMessage);
            timestamp = itemView.findViewById(R.id.tvTime);
        }
    }
}