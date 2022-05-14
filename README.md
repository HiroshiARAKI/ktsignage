# KtSignage
Work in progress!!

KtSignage is a Simple smart display application worked on Kotlin.

## Screenshots
Whole view
![screenshot1](screenshots/screenshot1.png)

## Application design
KtSignage is designed based on MVVM (Model-View-ViewModel).
![design](screenshots/design.png)

* MyApp : App
  * has the own coroutine scope whose lifecycle depends on this application one's (we can access with `MyApp.applicationScope`)
* View Layer
  * constructs TornadoFX (JavaFV) View hierarchy
  * has `MainView` as the primary view of this application
* ViewModel Layer
  * has the own coroutine scope (`viewModelScope` whose default dispatcher is `Dispatchers.JavaFX`)
* Model Layer
  * includes `data class` and DAO (Data Access Object) like `PreferencesDao`
  * DAOs must have the own interfaces to debug easily

## LICENSE
