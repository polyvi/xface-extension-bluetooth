/*
 Copyright 2012-2013, Polyvi Inc. (http://www.xface3.com)
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
/**
 * bluetooth 蓝牙设备模块
 * @module bluetooth
 * @main bluetooth
 */

/**
 * bluetooth模块提供了蓝牙设备配对信息的获取和设备的控制(Android) <br/>
 * @class bluetooth
 * @platform Android
 * @since 3.0.0
 */
var argscheck = require("cordova/argscheck");
var exec = require('cordova/exec');

var Bluetooth = function(){};
 /**
 * 打开bluetooth
  @example
        function win(result) {
            alert(result);
        }

        function fail(result) {
             alert(result);
        }

        navigator.bluetooth.enable(win,fail);
 * @method enable
 * @param {Function} [successCallback] 成功回调函数
 * @param {Bool} successCallback.data true表示设备打开，false表示设备打开失败
 * @param {Function} [errorCallback]   失败回调函数
 * @platform Android
 * @since 3.0.0
 */
Bluetooth.prototype.enable = function(successCallback,errorCallback) {
    argscheck.checkArgs('FF','Bluetooth.enable',arguments);
    exec(successCallback, errorCallback, "Bluetooth", "enableBT", []);
};
 /**
 * 关闭bluetooth
  @example
        function win(result) {
            alert(result);
        }

        function fail(result) {
             alert(result);
        }

        navigator.bluetooth.disable(win,fail);
 * @method disable
 * @param {Function} [successCallback] 成功回调函数
 * @param {Bool} successCallback.data true表示设备关闭成功，false表示设备关闭失败
 * @param {Function} [errorCallback]   失败回调函数
 * @platform Android
 * @since 3.0.0
 */
Bluetooth.prototype.disable = function(successCallback,errorCallback) {
    argscheck.checkArgs('FF','Bluetooth.disable',arguments);
    exec(successCallback, errorCallback, "Bluetooth", "disableBT", []);
};

/**
 * 列出找到的蓝牙设备列表
  @example
        function win(result) {
            for(var i =0;i<result.length;i++){
            var jsonobj = result[i];
            for(var x in jsonobj){
                alert(x+"="+jsonobj[x]);}
            }
        }

        function fail(result) {
             alert(result);
        }

        navigator.bluetooth.listDevices(win,fail);
 * @method listDevices
 * @param {Function} successCallback 成功回调函数
 * @param {JasonArray} successCallback.data 返回搜索到的未配对蓝牙设备信息,如：｛name：“testdevice”,macAddress：“12-AC-5D-6C-91-28",<br/>
                                                                                 name: "testdevice1",macAddress:"16-BC-5D-6C-91-28"}
 * @param {Function} [errorCallback]   失败回调函数
 * @platform Android
 * @since 3.0.0
 */
Bluetooth.prototype.listDevices = function(successCallback,errorCallback) {
    argscheck.checkArgs('FF','Bluetooth.listDevices',arguments);
    exec(successCallback, errorCallback, "Bluetooth", "listDevices", []);
};

/**
 * 列出已配对的蓝牙设备列表
  @example
        function win(result) {
            for(var i =0;i<result.length;i++){
            var jsonobj = result[i];
            for(var x in jsonobj){
                alert(x+"="+jsonobj[x]);}
            }
        }

        function fail(result) {
            alert(result);
        }
        navigator.bluetooth.listBoundDevices(win,fail);
 * @method listBoundDevices
 * @param {Function} successCallback 成功回调函数
 * @param {JasonArray} successCallback.data 返回已配对的蓝牙设备信息,如：｛name：“testdevice”,macAddress：“12-AC-5D-6C-91-28",<br/>
                                                                           name: "testdevice1",macAddress: "31-EA-12-11-31-44"}
 * @param {Function} [errorCallback]   失败回调函数
 * @platform Android
 * @since 3.0.0
 */
Bluetooth.prototype.listBoundDevices = function(successCallback,errorCallback) {
    argscheck.checkArgs('fF','Bluetooth.listBoundDevices',arguments);
    exec(successCallback, errorCallback, "Bluetooth", "listBoundDevices", []);
};
//--------------------------
 /**
 * 判断蓝牙设备是否开启
  @example
        function win(result) {
            alert(result);
        }

        function fail(result) {
            alert(result);
        }

        navigator.bluetooth.isBTEnabled(win,fail);
 * @method isBTEnabled
 * @param {Function} [successCallback] 成功回调函数
 * @param {Bool} successCallback.data true表示设备打开，false表示设备未打开
 * @param {Function} [errorCallback]   失败回调函数
 * @platform Android
 * @since 3.0.0
 */
Bluetooth.prototype.isBTEnabled = function(successCallback,errorCallback) {
    argscheck.checkArgs('fF','Bluetooth.isBTEnabled',arguments);
    exec(successCallback, errorCallback, "Bluetooth", "isBTEnabled", []);
};

 //--------------------------
 /**
 * 根据指定的地址配对
  @example
        function win(result) {
            alert(result);
        }

        function fail(result) {
            alert(result);
        }

        navigator.bluetooth.pairBT(macAddress,win,fail);
 * @method pairBT
 * @param {String} macAddress 要配对蓝牙设备的mac地址
 * @param {Function} successCallback 成功回调函数
 * @param {Bool} successCallback.data true表示配对成功，false表示设备配对失败
 * @param {Function} [errorCallback]   失败回调函数
 * @platform Android
 * @since 3.0.0
 */
Bluetooth.prototype.pairBT = function(macAddress,successCallback,errorCallback) {
    argscheck.checkArgs('sfF','Bluetooth.pairBT',arguments);
    exec(successCallback, errorCallback, "Bluetooth", "pairBT", [macAddress]);
};
//-----------------
/**
 * 对指定的地址取消配对
  @example
        function win(result) {
            alert(result);
        }

        function fail(result) {
            alert(result);
        }

        navigator.bluetooth.unPairBT(macAddress,win,fail);
 * @method unPairBT
 * @param {String} macAddress 要配对蓝牙设备的mac地址
 * @param {Function} successCallback 成功回调函数
 * @param {Bool} successCallback.data true表示取消配对成功，false表示取消配对失败
 * @param {Function} [errorCallback]   失败回调函数
 * @platform Android
 * @since 3.0.0
 */
Bluetooth.prototype.unPairBT = function(macAddress,successCallback,errorCallback) {
    argscheck.checkArgs('sfF','Bluetooth.unPairBT',arguments);
    exec(successCallback, errorCallback, "Bluetooth", "unPairBT", [macAddress]);
};
//-----------------
/**
 * 停止寻找蓝牙设备
  @example
        function win(result) {
            alert(result);
        }

        function fail(result) {
            alert(result);
        }

        navigator.bluetooth.stopDiscovering(win,fail);
 * @method stopDiscovering
 * @param {Function} [successCallback] 成功回调函数
 * @param {Bool} successCallback.data true表示停止搜索成功，false表示停止搜索失败
 * @param {Function} [errorCallback]   失败回调函数
 * @platform Android
 * @since 3.0.0
 */
Bluetooth.prototype.stopDiscovering = function(successCallback,errorCallback) {
    argscheck.checkArgs('fF','Bluetooth.stopDiscovering',arguments);
    exec(successCallback, errorCallback, "Bluetooth", "stopDiscovering", []);
};

/**
 * 判断指定mac地址的蓝牙设备是否配对
  @example
        function win(result) {
            alert(result);
        }

        function fail(result) {
            alert(result);
        }

        navigator.bluetooth.isBound(macAddress,win,fail);
 * @method isBound
 * @param {Function} successCallback 成功回调函数
 * @param {Bool} successCallback.data true表示指定的地址已经配对，false表示指定的地址未配对
 * @param {Function} [errorCallback]   失败回调函数
 * @platform Android
 * @since 3.0.0
 */
Bluetooth.prototype.isBound = function(macAddress,successCallback,errorCallback) {
    argscheck.checkArgs('sfF','Bluetooth.isBound',arguments);
    exec(successCallback, errorCallback, "Bluetooth", "isBound", [macAddress]);
};
module.exports = new Bluetooth();