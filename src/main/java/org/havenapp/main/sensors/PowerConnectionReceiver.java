package org.havenapp.main.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import org.havenapp.main.R;
import org.havenapp.main.model.EventTrigger;
import org.havenapp.main.service.MonitorService;

/**
 * Created by n8fr8 on 10/31/17.
 */

public class PowerConnectionReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        // Can't use intent.getIntExtra(BatteryManager.EXTRA_STATUS), as the extra is not provided.
        // The code example at
        // https://developer.android.com/training/monitoring-device-state/battery-monitoring.html
        // is wrong
        // see https://stackoverflow.com/questions/10211609/problems-with-action-power-connected

        // explicitly check the intent action
        // avoids lint issue UnsafeProtectedBroadcastReceiver
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if(intent.getAction() == null) return;
        switch(intent.getAction()){
            case Intent.ACTION_POWER_CONNECTED:
                isCharging = true;
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                isCharging = false;
                break;
            default:
                return;
        }

        if (MonitorService.getInstance() != null
                && MonitorService.getInstance().isRunning()) {
            MonitorService.getInstance().alert(EventTrigger.POWER, context.getString(R.string.status_charging) + isCharging );
        }
    }

    //Ref: https://developer.android.com/training/monitoring-device-state/battery-monitoring.html

    private void getBatteryStatus(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
    }
}
