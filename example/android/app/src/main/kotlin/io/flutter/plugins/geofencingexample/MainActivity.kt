package io.flutter.plugins.geofencingexample

import android.Manifest
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log

import io.flutter.app.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant
import android.content.Intent
import android.os.Build
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


class MainActivity() : FlutterActivity(), MethodChannel.MethodCallHandler {

    private val CHANNEL = "plugins.flutter.io/geolocation_plugin"
    private val TAG = "geolocation_plugin"

    // A reference to the service used to get location updates.
    private var mService: LocationUpdatesService? = null

    // Tracks the bound state of the service.
    private var mBound = false

    // Monitors the state of the connection to the service.
    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected $name")
            val binder = service as LocationUpdatesService.LocalBinder
            mService = binder.service

            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected $name")
            mService = null
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this)
        MethodChannel(flutterView, CHANNEL).setMethodCallHandler(this)

        Log.d(TAG, "onCreate")
    }

    override fun onStart() {
        super.onStart();
        Log.d(TAG, "onStart")
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(Intent(this, LocationUpdatesService::class.java), mServiceConnection,
                Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }

        super.onStop()
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        val args = call.arguments() as? ArrayList<*>
        when (call.method) {
            "LocationUpdatesService.initializeService" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 12312)
                }
                LocationUpdatesService.initializeService(this, args)
                result.success(true)
            }
            "LocationUpdatesService.registerGeoLocation" -> {
                val callbackHandle = args!![0] as Long
                mService!!.startGeoLocationService(this)
                mService!!.requestLocationUpdates(callbackHandle)
                result.success(true)
            }
            "LocationUpdatesService.removeGeoLocation" -> {
                mService!!.removeLocationUpdates();
                result.success(true)
            }
            else -> result.notImplemented()
        }
    }
}
