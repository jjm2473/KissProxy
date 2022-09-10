package me.dawson.proxyserver.ui;

import me.dawson.proxyserver.R;
import me.dawson.proxyserver.core.ProxyServer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;

public class ProxyService extends Service {
	public static final String TAG = "ProxyService";
	private static int NOTIFICATION_ID = 20140701;

	@Override
	public IBinder onBind(Intent binder) {
		return new IProxyControl.Stub() {
			@Override
			public boolean start() throws RemoteException {
				return doStart();
			}

			@Override
			public boolean stop() throws RemoteException {
				return doStop();
			}

			@Override
			public boolean isRunning() throws RemoteException {
				return ProxyServer.getInstance().isRunning();
			}

			@Override
			public int getPort() throws RemoteException {
				return ProxyServer.getInstance().getPort();
			}

		};
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private boolean doStart() {
		ProxyServer proxyServer = ProxyServer.getInstance();
		if (proxyServer.isRunning()) {
			return false;
		}

		boolean ret = proxyServer.start();

		if (ret) {

			Notification notification = new Notification();
			notification.icon = R.drawable.icon_launcher;
			notification.tickerText = getResources().getString(R.string.proxy_on);
			notification.when = System.currentTimeMillis();

			CharSequence contentTitle = getResources().getString(R.string.app_name);

			CharSequence contentText = getResources().getString(
					R.string.service_text);
			Intent intent = new Intent(this, ProxySettings.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					intent, 0);

			notification.setLatestEventInfo(this, contentTitle, contentText,
					pendingIntent);
			notification.flags |= Notification.FLAG_ONGOING_EVENT;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
				startForeground(NOTIFICATION_ID, notification);
			} else {
				NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				manager.notify(NOTIFICATION_ID, notification);
			}
		}
		return ret;
	}

	private boolean doStop() {
		ProxyServer proxyServer = ProxyServer.getInstance();
		if (!proxyServer.isRunning()) {
			return false;
		}

		boolean ret = proxyServer.stop();
		if (ret) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
				stopForeground(true);
			} else {
				NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				manager.cancel(NOTIFICATION_ID);
			}
		}
		return ret;
	}

}
