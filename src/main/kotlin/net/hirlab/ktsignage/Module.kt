package net.hirlab.ktsignage

import com.google.inject.AbstractModule
import net.hirlab.ktsignage.model.dao.PreferencesDao
import net.hirlab.ktsignage.model.dao.ProdPreferencesDao

/**
 * Application module for Google Guice, dependency injection framework.
 */
class Module : AbstractModule() {
    override fun configure() {
        bind(PreferencesDao::class.java).to(ProdPreferencesDao::class.java)
    }
}