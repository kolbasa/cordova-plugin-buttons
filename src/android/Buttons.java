package de.kolbasa.buttons;

import android.view.KeyEvent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Buttons extends CordovaPlugin {

    private CallbackContext cbc;
    private HashMap<Integer, Long> lastDownTime;

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(final KeyEvent keyEvent) {
        JSONObject json = new JSONObject();

        if (this.lastDownTime == null) {
            this.lastDownTime = new HashMap<>();
        }

        try {
            if (keyEvent != null) {
                int keyCode = keyEvent.getKeyCode();
                Long lastUsed = lastDownTime.get(keyCode);

                if (lastUsed != null && lastUsed == keyEvent.getDownTime() &&
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN && !keyEvent.isCanceled()) {
                    return;
                }

                json.put("keyCode", keyEvent.getKeyCode());
                json.put("eventTime", keyEvent.getEventTime());
                json.put("downTime", keyEvent.getDownTime());
                json.put("cancelled", keyEvent.isCanceled());
                json.put("finished", keyEvent.getAction() == KeyEvent.ACTION_UP);

                this.lastDownTime.put(keyCode, keyEvent.getDownTime());
            }
        } catch (JSONException e) {
            //
        }

        PluginResult response = new PluginResult(PluginResult.Status.OK, json);
        response.setKeepCallback(true);
        this.cbc.sendPluginResult(response);
    }

    private void registerButtonListener() {
        EventBus.getDefault().register(this);
    }

    private void unregisterButtonListener() {
        EventBus.getDefault().unregister(this);
    }

    private void registerCallback(CallbackContext callbackContext) {
        this.cbc = callbackContext;
        PluginResult response = new PluginResult(PluginResult.Status.OK);
        response.setKeepCallback(true);
        callbackContext.sendPluginResult(response);
    }

    private void unregisterCallback() {
        if (this.cbc != null) {
            this.cbc.success("Callback closed");
            this.cbc = null;
        }
        this.lastDownTime = null;
    }

    private void subscribe(CallbackContext callbackContext) {
        try {
            cleanup();
            registerButtonListener();
            registerCallback(callbackContext);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void unsubscribe(CallbackContext callbackContext) {
        try {
            cleanup();
            callbackContext.success();
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void cleanup() {
        unregisterButtonListener();
        unregisterCallback();
    }

    @Override
    public boolean execute(final String action, final JSONArray data,
                           final CallbackContext callbackContext) {
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