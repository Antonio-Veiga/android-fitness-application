package com.estg.ipp.fitnessapp

import android.Manifest
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Login.Login
import com.estg.ipp.fitnessapp.databinding.MainActivityBinding
import com.estg.ipp.fitnessapp.train.services.BackgroundService
import com.estg.ipp.fitnessapp.train.threads.StartBackgroundServiceThread
import com.google.android.material.navigation.NavigationView
import java.time.LocalDateTime
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: MainActivityBinding
    private lateinit var navController: NavController
    private var context = this
    private lateinit var name: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)


        val dao = FitnessDB.getInstance(this)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)

        executors.execute {
            name = dao.getUsersAndActualWeight()[0].user.name
        }

        executors.shutdown()


        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                }
                permissions.getOrDefault(Manifest.permission.RECORD_AUDIO, false) -> {

                }
                else -> {
                    // No location access granted. Means no web
                }
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContentView(binding.root)

        setSupportActionBar(binding.appBar.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout

        val navView: NavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )

        val headerv: View = navView.getHeaderView(0)

        val username: TextView = headerv.findViewById(R.id.name_title)

        val usernameText : String = getString(R.string.nav_header_title) + " $name"

        username.text = usernameText

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener(this)

        startTrainingServices()
        removeExtraSessions()
    }


    private fun startTrainingServices() {
        // Service Intent creation
        if (!isMyServiceRunning(BackgroundService::class.java)) {
            StartBackgroundServiceThread(applicationContext).run()
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun removeExtraSessions() {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = FitnessDB.executors

        executors.execute {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val date = LocalDateTime.now().minusDays(1)
                val list = dao.getAllSessionsBeforeDate(date)
                for (i in list) {
                    dao.deleteSession(i)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_log && checkInternet(context)) {
            val intent = Intent(this, Login::class.java)
            intent.putExtra("iniciar", "iniciar")
            startActivity(intent)
            return true
        }
        return NavigationUI.onNavDestinationSelected(
            item,
            navController
        ) || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onBackPressed() {
        navController.navigateUp(appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        var connectivity: ConnectivityManager? = null
        var net: Network? = null

        fun checkInternet(context: Context): Boolean {
            connectivity =
                context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
            net = connectivity!!.activeNetwork

            if (net != null) {
                return true
            }

            Toast.makeText(context, "Não tem acesso a internet", Toast.LENGTH_SHORT).show()

            return false
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.nav_session_exercise) {
            supportActionBar?.title = arguments!!.getString("title")
        }
    }
}