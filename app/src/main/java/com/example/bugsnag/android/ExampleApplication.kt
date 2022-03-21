package com.example.bugsnag.android

import android.app.Application
import android.util.Log
import com.bugsnag.android.Bugsnag
import com.bugsnag.android.Configuration
import com.bugsnag.android.ErrorType
import java.io.File

class ExampleApplication : Application() {

    companion object {
        init {
            System.loadLibrary("entrypoint")
        }

        const val TEST_TAB = "TEST_TAB"
    }

    private external fun performNativeBugsnagSetup()

    override fun onCreate() {
        super.onCreate()

        val config = Configuration.load(this)
        config.setUser("123456", "joebloggs@example.com", "Joe Bloggs")
        config.addMetadata("user", "age", 31)

        config.addOnSend { event ->
            val metadataToSend = event.getMetadata(TEST_TAB)

            // FIXME: The issue is here
            // FIXME: If this is native crash - 'metadataToSend' will not contain 'list' and 'map' values.
            Log.i("TEST_LOG", "Number of sent metadata items: ${metadataToSend?.size?.toString().orEmpty()}")

            event.addMetadata(TEST_TAB, "anotherLongValue", "123456789_123456789_123456789_123456789_123456789_123456789_123456789_")

            true
        }

        Bugsnag.start(this, config)

        // Initialise native callbacks
        performNativeBugsnagSetup()

        // FIXME, Issue: Metadata setup is here!
        setTestMetadata()
        setTooLongMetadata()
    }

    private fun setTestMetadata() {
        Bugsnag.addMetadata(TEST_TAB, "int", 42)
        Bugsnag.addMetadata(TEST_TAB, "string", "value")
        Bugsnag.addMetadata(TEST_TAB, "list", listOf("1", "2").toString())
        Bugsnag.addMetadata(TEST_TAB, "map", mapOf("key1" to 1, "key2" to 2).toString())

        Bugsnag.addFeatureFlag("TEST", "true")
    }

    private fun setTooLongMetadata() {
        Bugsnag.addMetadata(TEST_TAB, "longValue", "123456789_123456789_123456789_123456789_123456789_123456789_123456789_")
    }
}
