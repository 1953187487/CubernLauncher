package com.movtery.zalithlauncher.terracotta;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.movtery.zalithlauncher.R;

import net.burningtnt.terracotta.TerracottaAndroidAPI;

import java.io.IOException;

/**
 * 陶瓦联机 VPN 隧道服务
 *
 * 移植自 Zalith Launcher 2，参考 FCL 实现。
 */
@SuppressLint("VpnServicePolicy")
public class TerracottaVPNService extends VpnService {

    private static final String TAG = "TerracottaVPNService";

    private static final int VPN_NOTIFICATION_ID = 0x7A11;
    private static final int VPN_NOTIFICATION_REQUEST_CODE = 0x7A12;

    private static final String CHANNEL_ID = "terracotta_vpn";

    public static final String ACTION_START = "net.burningtnt.terracotta.action.START";
    public static final String ACTION_STOP = "net.burningtnt.terracotta.action.STOP";
    public static final String ACTION_REPOST = "net.burningtnt.terracotta.action.REPOST";
    public static final String ACTION_UPDATE_STATE = "net.burningtnt.terracotta.action.UPDATE_STATE";

    private static final String EXTRA_FROM_DELETE = "from_delete";
    public static final String EXTRA_STATE_TEXT = "terracotta_state_text";

    private NotificationManager notificationManager;
    private int currentStateStringRes = -1;
    private volatile boolean isStopping = false;

    private ParcelFileDescriptor vpnInterface;

    private static boolean running = false;

    public static boolean isRunning() {
        return running;
    }

    /**
     * 构建陶瓦联机专用通知渠道
     */
    public static void buildNotificationChannel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.terracotta_notification_title),
                NotificationManager.IMPORTANCE_LOW
        );
        manager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = true;

        String action = intent != null ? intent.getAction() : null;
        Log.d(TAG, "onStartCommand, action = " + action);

        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            buildNotificationChannel(this);
        }

        if (ACTION_STOP.equals(action)) {
            isStopping = true;
            cleanup();
            stopForeground(true);
            stopSelf();
            return Service.START_NOT_STICKY;
        }

        if (ACTION_UPDATE_STATE.equals(action) || ACTION_REPOST.equals(action)) {
            currentStateStringRes = getStateTextRes(intent);
        }

        if (!isStopping) {
            try {
                startForeground0(buildVpnNotification());
            } catch (Exception e) {
                Log.w(TAG, "Cannot start VPN foreground service.", e);
                try {
                    TerracottaAndroidAPI.getPendingVpnServiceRequest().reject();
                } catch (IllegalStateException ignored) {
                }
                stopSelf();
                return Service.START_NOT_STICKY;
            }
        }

        if (intent == null) {
            stopSelf();
            return Service.START_NOT_STICKY;
        }

        if (ACTION_UPDATE_STATE.equals(action) || ACTION_REPOST.equals(action)) {
            return Service.START_STICKY;
        }

        isStopping = false;

        Builder vpnBuilder = new Builder().setSession("Terracotta Connection");

        try {
            vpnBuilder.addDisallowedApplication(getPackageName());
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        TerracottaAndroidAPI.VpnServiceRequest request;
        try {
            request = TerracottaAndroidAPI.getPendingVpnServiceRequest();
        } catch (IllegalStateException e) {
            stopSelf();
            return Service.START_NOT_STICKY;
        }
        vpnInterface = request.startVpnService(vpnBuilder);

        return Service.START_STICKY;
    }

    @Override
    public void onRevoke() {
        Log.w(TAG, "onRevoke(): preempted by another VPN or revoked by user; tearing down.");
        isStopping = true;
        Terracotta.INSTANCE.setWaiting(false);
        cleanup();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy(): vpn service finished");
        isStopping = true;
        Terracotta.INSTANCE.setWaiting(false);
        cleanup();
        super.onDestroy();
    }

    private int getStateTextRes(Intent intent) {
        if (intent.hasExtra(EXTRA_STATE_TEXT)) {
            return intent.getIntExtra(EXTRA_STATE_TEXT, -1);
        }
        return -1;
    }

    private Notification buildVpnNotification() {
        String title = getString(R.string.terracotta_notification_title);

        if (currentStateStringRes == -1) {
            TerracottaState.Ready state = Terracotta.INSTANCE.getState().getValue();
            if (state != null && !(state instanceof TerracottaState.Waiting)) {
                currentStateStringRes = state.localStringRes();
            }
        }

        String contentText;
        Terracotta.Mode mode = Terracotta.INSTANCE.getMode();
        if (mode == null) {
            contentText = getString(R.string.terracotta_notification_state_preparing);
        } else {
            String modeText = mode == Terracotta.Mode.Host
                    ? getString(R.string.terracotta_player_kind_host)
                    : getString(R.string.terracotta_player_kind_guest);
            String stateString = currentStateStringRes == -1
                    ? getString(R.string.terracotta_waiting)
                    : getString(currentStateStringRes);
            contentText = String.format(getString(R.string.terracotta_notification_desc), modeText, stateString);
        }

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID);

        Intent deleteIntent = new Intent(this, TerracottaVPNService.class)
                .setAction(ACTION_REPOST)
                .putExtra(EXTRA_FROM_DELETE, true)
                .putExtra(EXTRA_STATE_TEXT, currentStateStringRes);
        PendingIntent deletePendingIntent = PendingIntent.getService(
                this,
                VPN_NOTIFICATION_REQUEST_CODE,
                deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(contentText)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setDeleteIntent(deletePendingIntent);

        return builder.build();
    }

    private void startForeground0(Notification notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(VPN_NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(VPN_NOTIFICATION_ID, notification);
        }
    }

    private void cleanup() {
        Log.d(TAG, "cleanup(): close tun & cancel notification");

        if (notificationManager != null) {
            notificationManager.cancel(VPN_NOTIFICATION_ID);
        }

        if (vpnInterface != null) {
            try {
                vpnInterface.close();
            } catch (IOException ignored) {
            }
            vpnInterface = null;
        }

        running = false;
    }
}
