package de.kolbasa.buttons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Buttons extends CordovaPlugin {

    private CallbackContext cbc;
    private BroadcastReceiver broadcastReceiver;

    private void registerPowerButtonListener() {
        unregisterGeneralButtonListener();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String[] action = intent.getAction().split("\\.");
                onKey(null, action[action.length - 1]);
            }
        };
        this.webView.getContext().registerReceiver(this.broadcastReceiver, intentFilter);
    }

    private void unregisterPowerButtonListener() {
        if (this.broadcastReceiver != null) {
            this.webView.getContext().unregisterReceiver(this.broadcastReceiver);
            this.broadcastReceiver = null;
        }
    }

    private void registerGeneralButtonListener() {
        EventBus.getDefault().register(this);
    }

    private void unregisterGeneralButtonListener() {
        EventBus.getDefault().unregister(this);
    }

    private long lastDownTime;

    private void onKey(KeyEvent keyEvent, String action) {
        JSONObject json = new JSONObject();

        try {
            if (keyEvent != null) {
                if (this.lastDownTime == keyEvent.getDownTime() &&
                        keyEvent.getAction() == 0 && !keyEvent.isCanceled()) {
                    return;
                }
                json.put("keyCode", keyEvent.getKeyCode());
                json.put("eventTime", keyEvent.getEventTime());
                json.put("downTime", keyEvent.getDownTime());
                json.put("cancelled", keyEvent.isCanceled());
                json.put("finished", keyEvent.getAction() == KeyEvent.ACTION_UP);
                this.lastDownTime = keyEvent.getDownTime();
            }

            if (action != null) {
                json.put("action", action);
            }
        } catch (JSONException e) {
            //
        }

        PluginResult response = new PluginResult(PluginResult.Status.OK, json);
        response.setKeepCallback(true);
        this.cbc.sendPluginResult(response);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(final KeyEvent event) {
        onKey(event, null);
    }

    private void unregisterCallback() {
        if (this.cbc != null) {
            this.cbc.success("Unsubscribed");
            this.cbc = null;
        }
    }

    private void registerCallback(CallbackContext callbackContext) {
        unregisterCallback();
        this.cbc = callbackContext;
        PluginResult response = new PluginResult(PluginResult.Status.OK);
        response.setKeepCallback(true);
        callbackContext.sendPluginResult(response);
    }

    private void subscribe(CallbackContext callbackContext) {
        try {
            registerPowerButtonListener();
            registerGeneralButtonListener();
            registerCallback(callbackContext);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void unsubscribe(CallbackContext callbackContext) {
        try {
            onDestroy();
            callbackContext.success();
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        unregisterPowerButtonListener();
        unregisterGeneralButtonListener();
        super.onDestroy();
    }

    @Override
    public boolean execute(final String action, final JSONArray data, final CallbackContext callbackContext) {
        switch (action) {
            case "subscribe":
                cordova.getThreadPool().execute(() -> subscribe(callbackContext));
                break;
            case "unsubscribe":
                cordova.getThreadPool().execute(() -> unsubscribe(callbackContext));
                break;
            default:
                return false;
        }
        return true;
    }

}
