
/*
 Copyright 2012-2013, Polyvi Inc. (http://polyvi.github.io/openxface)
 This program is distributed under the terms of the GNU General Public License.

 This file is part of xFace.

 xFace is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 xFace is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with xFace.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.polyvi.xface.extension.bluetooth;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.polyvi.xface.util.XLog;

/**
 * 蓝牙扩展，提供了开关蓝牙，查找蓝牙设备，根据指定mac地址进行配对等功能 在查找蓝牙设备时，通过BroadcastReceiver实现
 */
public class XBluetoothExt extends CordovaPlugin {
    public static final String CLASS_NAME = XBluetoothExt.class.getSimpleName();
    public static final String DEVICE_NAME = "name";
    public static final String DEVICE_MACADDRESSG = "macAddress";
    public static final String COMMAND_DISCOVER_DEVICES = "listDevices";
    public static final String COMMAND_LIST_BOUND_DEVICES = "listBoundDevices";
    public static final String COMMAND_IS_BT_ENABLED = "isBTEnabled";
    public static final String COMMAND_ENABLE_BT = "enableBT";
    public static final String COMMAND_DISABLE_BT = "disableBT";
    public static final String COMMAND_PAIR_BT = "pairBT";
    public static final String COMMAND_UNPAIR_BT = "unPairBT";
    public static final String COMMAND_STOP_DISCOVERING_BT = "stopDiscovering";
    public static final String COMMAND_IS_BOUND_BT = "isBound";
    private static BluetoothAdapter mBtadapter;
    private ArrayList<BluetoothDevice> mFoundDevices;
    private boolean mIsDiscovering = false;
    private CallbackContext mJsCallback;
    private CordovaInterface mCordova;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        mCordova = cordova;
        try {
            mBtadapter = BluetoothAdapter.getDefaultAdapter();
        } catch(RuntimeException e) {
            XLog.e(CLASS_NAME, "init: RuntimeException");
            return;
        }
        mFoundDevices = new ArrayList<BluetoothDevice>();
        Context context = cordova.getActivity();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiver, filter);
    }

    private void addDevice(BluetoothDevice device) {
        if (!mFoundDevices.contains(device)) {
            mFoundDevices.add(device);
        }
    }

    @Override
    public void onDestroy() {
        mCordova.getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (null == mJsCallback) {
                return;
            }
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    addDevice(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mIsDiscovering = true;
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                mIsDiscovering = false;
                JSONArray devicesFound = new JSONArray();
                for (BluetoothDevice device : mFoundDevices) {
                    if ((device.getName() != null)
                            && (device.getBluetoothClass() != null)) {
                        JSONObject foundDevice = new JSONObject();
                        try {
                            foundDevice.put(DEVICE_NAME, device.getName());
                            foundDevice.put(DEVICE_MACADDRESSG,
                                    device.getAddress());
                            devicesFound.put(foundDevice);
                        } catch (JSONException e) {
                            XLog.e(CLASS_NAME, e.getMessage(), e);
                        }
                    }
                }
                PluginResult result = new PluginResult(
                        PluginResult.Status.OK, devicesFound);
                result.setKeepCallback(true);
                mJsCallback.sendPluginResult(result);
            }
        }
    };

    @Override
    public boolean execute(String action, JSONArray args,
            CallbackContext callbackContext) throws JSONException {
        mJsCallback = callbackContext;
        PluginResult result = null;
        if (COMMAND_DISCOVER_DEVICES.equals(action)) {
            result = discoverDevice();
        } else if (COMMAND_IS_BT_ENABLED.equals(action)) {
            result = isBluetoothEnabled();
        } else if (COMMAND_ENABLE_BT.equals(action)) {
            result = enableBluetooth();
        } else if (COMMAND_DISABLE_BT.equals(action)) {
            result = disableBluetooth();
        } else if (COMMAND_PAIR_BT.equals(action)) {
            result = pairBluetooth(args);
        } else if (COMMAND_UNPAIR_BT.equals(action)) {
            result = unpairBluetooth(args);
        } else if (COMMAND_LIST_BOUND_DEVICES.equals(action)) {
            result = listBoundBluetooth();
        } else if (COMMAND_STOP_DISCOVERING_BT.equals(action)) {
            result = stopDiscovering();
        } else if (COMMAND_IS_BOUND_BT.equals(action)) {
            result = isBoundBluetooth(args);
        } else {
            result = new PluginResult(
                    PluginResult.Status.INVALID_ACTION);
        }
        mJsCallback.sendPluginResult(result);
        return true;
    }

    /**
     * 根据指定mac地址判断是否配对
     *
     * @param args
     *            包含了mac地址的json数组
     * @return PluginResult
     */
    private PluginResult isBoundBluetooth(JSONArray args) {
        String addressDevice = null;
        try {
            addressDevice = args.getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
            return new PluginResult(PluginResult.Status.ERROR,e.getMessage());
        }
        BluetoothDevice device = mBtadapter.getRemoteDevice(addressDevice);
        boolean isBound = false;

        if (device != null
                && device.getBondState() == BluetoothDevice.BOND_BONDED)
            isBound = true;
        else
            isBound = false;
        return new PluginResult(PluginResult.Status.OK, isBound);
    }

    /**
     * 停止搜索蓝牙设备
     *
     * @return PluginResult
     */
    private PluginResult stopDiscovering() {
        boolean stopped = true;
        if (mBtadapter.isDiscovering()) {
            stopped = mBtadapter.cancelDiscovery();
            mIsDiscovering = false;
        }
        return new PluginResult(PluginResult.Status.OK, stopped);
    }

    /**
     * 列出已经配对的蓝牙设备
     *
     * @return PluginResult
     */
    private PluginResult listBoundBluetooth() {
        Set<BluetoothDevice> pairedDevices = mBtadapter.getBondedDevices();
        JSONArray devicesBound = new JSONArray();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if ((device.getName() != null)
                        && (device.getBluetoothClass() != null)) {
                    JSONObject boundDevice = new JSONObject();
                    try {
                        boundDevice.put(DEVICE_NAME, device.getName());
                        boundDevice
                                .put(DEVICE_MACADDRESSG, device.getAddress());
                        devicesBound.put(boundDevice);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return new PluginResult(PluginResult.Status.OK, devicesBound);
    }

    /**
     * 根据指定的mac地址来取消蓝牙配对
     *
     * @param args
     *            包含了要取消配对的mac地址的json数组
     * @return PluginResult
     */
    private PluginResult unpairBluetooth(JSONArray args) {
        String addressDevice = null;
        try {
            addressDevice = args.getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
            return new PluginResult(PluginResult.Status.ERROR,e.getMessage());
        }

        if (mBtadapter.isDiscovering()) {
            mBtadapter.cancelDiscovery();
        }
        BluetoothDevice device = mBtadapter.getRemoteDevice(addressDevice);
        boolean IsUnpaired = false;
        Method m;
        try {
            m = device.getClass().getMethod("removeBond");
            IsUnpaired = (Boolean) m.invoke(device);
        } catch (SecurityException e) {
            e.printStackTrace();
            return new PluginResult(PluginResult.Status.ERROR,e.getMessage());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return new PluginResult(PluginResult.Status.ERROR,e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new PluginResult(PluginResult.Status.ERROR,e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new PluginResult(PluginResult.Status.ERROR,e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return new PluginResult(PluginResult.Status.ERROR,e.getMessage());
        }
        return new PluginResult(PluginResult.Status.OK, IsUnpaired);
    }

    /**
     * 根据指定mac地址进行蓝牙配对
     *
     * @param args
     *            包含要配对的mac地址的json数组
     * @return PluginResult
     */
    private PluginResult pairBluetooth(JSONArray args) {
        String addressDevice = null;
        try {
            addressDevice = args.getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mBtadapter.isDiscovering()) {
            mBtadapter.cancelDiscovery();
        }

        BluetoothDevice device = mBtadapter.getRemoteDevice(addressDevice);
        boolean paired = false;
        Method m;
        try {
            m = device.getClass().getMethod("createBond");
            paired = (Boolean) m.invoke(device);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new PluginResult(PluginResult.Status.OK, paired);
    }

    /**
     * 关闭蓝牙设备
     *
     * @return PluginResult
     */
    private PluginResult disableBluetooth() {
        boolean isDisabled = false;
        if (mBtadapter.isEnabled()) {
            isDisabled = mBtadapter.disable();
        } else {
            isDisabled = true;
        }
        return new PluginResult(PluginResult.Status.OK, isDisabled);
    }

    /**
     * 打开蓝牙设备
     *
     * @return
     */
    private PluginResult enableBluetooth() {
        boolean enabled = false;
        if (mBtadapter.isEnabled()) {
            enabled = true;
        } else {
            enabled = mBtadapter.enable();
        }
        return new PluginResult(PluginResult.Status.OK, enabled);
    }

    /**
     * 判断蓝牙设备是否打开
     *
     * @return PluginResult
     */
    private PluginResult isBluetoothEnabled() {
        PluginResult result;
        boolean isEnabled = mBtadapter.isEnabled();
        result = new PluginResult(PluginResult.Status.OK, isEnabled);
        return result;
    }

    /**
     * 搜索附近的蓝牙设备
     *
     * @return
     */
    private PluginResult discoverDevice() {
        if (mIsDiscovering == true) {
            return new PluginResult(PluginResult.Status.NO_RESULT);
        }
        mFoundDevices.clear();
        mIsDiscovering = true;
        if (mBtadapter.isDiscovering()) {
            mBtadapter.cancelDiscovery();
        }
        mBtadapter.startDiscovery();
        PluginResult result = new PluginResult(
                PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        return result;
    }

}
