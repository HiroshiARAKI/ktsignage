package net.hirlab.ktsignage

import com.google.inject.AbstractModule
import net.hirlab.ktsignage.model.dao.PreferencesDao
import net.hirlab.ktsignage.model.dao.AppPreferencesDao

/**
 * Application module for Google Guice, dependency injection framework.
 */
class AppModule : AbstractModule() {
    override fun configure() {
        bind(PreferencesDao::class.java).to(AppPreferencesDao::class.java)
    }
}