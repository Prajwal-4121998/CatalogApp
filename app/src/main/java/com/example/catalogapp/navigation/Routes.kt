package com.example.catalogapp.navigation

import kotlinx.serialization.Serializable

// ─── Type-safe route definitions ──────────────────────────────────────────────
// Each route is a @Serializable class/object — Navigation generates
// the route string automatically, no manual string formatting needed.
// Compile-time safety: wrong argument types fail at build time, not runtime.

@Serializable
object CatalogRoute

@Serializable
data class DetailRoute(val productId: Int)
