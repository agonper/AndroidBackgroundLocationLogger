package com.example.alarmtest

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.alarmtest.alarms.AlarmWatchdog
import com.example.alarmtest.alarms.OneMinuteAlarm
import com.example.alarmtest.coordinates.CoordsCollectorNotification
import com.example.alarmtest.managers.PermissionsManager
import com.example.alarmtest.managers.PowerSavingsManager

class MainActivity : AppCompatActivity() {

    private val tag = javaClass.canonicalName

    private lateinit var countText: TextView

    private lateinit var model: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        countText = findViewById(R.id.countText)

        PermissionsManager(this).askLocationAccess()
        PowerSavingsManager(this).requestDeactivation()
        CoordsCollectorNotification(this).setupChannel()

        val oneMinuteAlarm = OneMinuteAlarm(this)
        Log.d(tag, "Alarm scheduled (pre): ${oneMinuteAlarm.isUp}")
        oneMinuteAlarm.schedule()
        Log.d(tag, "Alarm scheduled (post): ${oneMinuteAlarm.isUp}")
        AlarmWatchdog(this).schedule()

        model = ViewModelProviders.of(this).get(MainViewModel::class.java)

        setupCountObserver()
    }

    fun wipeCoords(view: View) {
        model.wipeCoords()
    }

    private fun setupCountObserver() {
        val coordsCountObserver = Observer<Long> { coordsCount->
            countText.text = "Coords stored: $coordsCount"
        }
        model.coordsCount.observe(this, coordsCountObserver)
    }
}
