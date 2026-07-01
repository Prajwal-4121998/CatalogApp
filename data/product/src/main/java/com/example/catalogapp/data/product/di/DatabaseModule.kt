package com.example.catalogapp.data.product.di

import android.content.Context
import androidx.room.Room
import com.example.catalogapp.data.product.local.CatalogDatabase
import com.example.catalogapp.data.product.local.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCatalogDatabase(
        @ApplicationContext context: Context
    ): CatalogDatabase = Room.databaseBuilder(
        context,
        CatalogDatabase::class.java,
        "catalog_database"
    )
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    @Singleton
    fun provideProductDao(database: CatalogDatabase): ProductDao =
        database.productDao()
}
