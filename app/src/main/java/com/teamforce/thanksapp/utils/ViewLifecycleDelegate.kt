package com.teamforce.thanksapp.utils

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewLifecycleDelegate<T>(
    private val initializer: () -> T,
): ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {

    private var value: T? = null
    private var isObserverAdded = false

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if(!isObserverAdded) {
            thisRef.viewLifecycleOwner.lifecycle.addObserver(this)
            isObserverAdded = true
        }

        if(value == null) {
            value = initializer.invoke()
        }

        return value!!
    }

    override fun onDestroy(owner: LifecycleOwner) {
        isObserverAdded = false
        value = null
        super.onDestroy(owner)
    }

}