package com.example.catalogapp.data.product.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.catalogapp.data.product.local.dao.ProductDao
import com.example.catalogapp.data.product.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = true
)
abstract class CatalogDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
