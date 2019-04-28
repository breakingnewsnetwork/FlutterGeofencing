// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.geofencingexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import io.flutter.plugins.geofencingexample.LocationUpdatesService

class GeoLocationRebootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("GEOLOCATION REBOOT", "Reregistering geolocation!")
        if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e("GEOLOCATION REBOOT", "Reregistering geolocation!")
            // LocationUpdatesService.reRegisterAfterReboot(context)
        }
    }
}