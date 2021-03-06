// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';
import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:geofencing_example/location.dart';
import 'package:geofencing_example/callback_dispatcher.dart';

class GeoLocationManager {
  static const MethodChannel _channel = MethodChannel('plugins.flutter.io/geolocation_plugin');
  static const MethodChannel _background = MethodChannel('plugins.flutter.io/geolocation_plugin_background');

  /// Initialize the plugin and request relevant permissions from the user.
  static Future<void> initialize() async {
    final CallbackHandle callback = PluginUtilities.getCallbackHandle(callbackDispatcher);
    await _channel.invokeMethod('LocationUpdatesService.initializeService', <dynamic>[callback.toRawHandle()]);
  }

  /// Promote the geofencing service to a foreground service.
  ///
  /// Will throw an exception if called anywhere except for a geofencing
  /// callback.
  static Future<void> promoteToForeground() async => await _background.invokeMethod('LocationUpdatesService.promoteToForeground');

  /// Demote the geofencing service from a foreground service to a background
  /// service.
  ///
  /// Will throw an exception if called anywhere except for a geofencing
  /// callback.
  static Future<void> demoteToBackground() async => await _background.invokeMethod('LocationUpdatesService.demoteToBackground');

  /// Register for geofence events for a [GeofenceRegion].
  ///
  /// `region` is the geofence region to register with the system.
  /// `callback` is the method to be called when a geofence event associated
  /// with `region` occurs.
  ///
  /// Note: `GeofenceEvent.dwell` is not supported on iOS. If the
  /// `GeofenceRegion` provided only requests notifications for a
  /// `GeofenceEvent.dwell` trigger on iOS, `UnsupportedError` is thrown.
  static Future<void> registerGeoLocation(void Function(Location location) callback) async {
    final List<dynamic> args = <dynamic>[PluginUtilities.getCallbackHandle(callback).toRawHandle()];
    await _channel.invokeMethod('LocationUpdatesService.registerGeoLocation', args);
  }

  /// Stop receiving geofence events for a given [GeofenceRegion].
  static Future<bool> removeGeoLocation() async => await _channel.invokeMethod('LocationUpdatesService.removeGeoLocation');
}
