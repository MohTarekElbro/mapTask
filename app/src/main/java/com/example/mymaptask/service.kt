package com.example.mymaptask

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.widget.Toast
import com.google.android.gms.maps.model.Marker
import java.lang.Thread.sleep


class service() : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service started", Toast.LENGTH_LONG).show()
        return START_STICKY
    }

    private fun getCurrentLocation() {
        Toast.makeText(this, "Thread is running", Toast.LENGTH_LONG).show()

    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show()
    }
}