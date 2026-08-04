package com.example.catalogapp.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController

/**
 * Prevents duplicate navigation calls from rapid double-taps or multiple
 * simultaneous triggers (e.g. two different buttons both calling navigate()
 * before the first transition settles). Only allows navigation when the
 * current back stack entry is RESUMED — i.e. no transition is in flight.
 */
fun NavController.navigateSafely(route: Any) {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navigate(route)
    }
}

fun NavController.popBackStackSafely(): Boolean {
    return if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    } else {
        false
    }
}
