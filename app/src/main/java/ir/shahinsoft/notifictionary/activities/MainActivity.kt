package ir.shahinsoft.notifictionary.activities

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.text.SpannableString
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.dialog.LicenseDialog
import ir.shahinsoft.notifictionary.fragments.*
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.services.NotifictionaryService
import ir.shahinsoft.notifictionary.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlin.system.exitProcess



class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val onAccept = {
        getSharedPreferences(APP, Context.MODE_PRIVATE).edit().putBoolean(LICENSE_ACCEPTANCE, true).apply()
        startNotifictionaryService()
    }

    private val onExit = {
        finish()
        //make sure app is closed
        exitProcess(0)
    }

    private val homeFragment = MainFragment()

    private val translateFragment = TranslateFragment()

    private val boardsFragment = BoardsListFragment()

    private val bottomNavigationItemSelectListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.home -> {
                setFragment(homeFragment)
                menu.setColorFilter(ContextCompat.getColor(this, R.color.picker_deep_purple))
            }
            R.id.translate -> {
                setFragment(translateFragment)
                menu.setColorFilter(ContextCompat.getColor(this, R.color.white))
            }
            R.id.boards -> {
                setFragment(boardsFragment)
                menu.setColorFilter(ContextCompat.getColor(this, R.color.picker_deep_purple))
            }
        }
        return@OnNavigationItemSelectedListener true
    }

    private fun setFragment(fragment: Fragment) {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardsFragment.activityCallback = { board ->
            setBoardDetailFragment(board)
        }

        checkLicense()

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList = null

        bottom_navigation.setOnNavigationItemSelectedListener(bottomNavigationItemSelectListener)

        setNavViewFont()

        menu.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }

        supportFragmentManager.beginTransaction().add(R.id.fragment_container, homeFragment).commit()
    }

    private fun setBoardDetailFragment(board: Board) {
        val fragment = BoardDetailFragment()
        fragment.board = board
        fragment.learnFragment = {
            setLearnFragment(it)
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(board.name)
                .commit()
    }

    private fun setLearnFragment(board: Board){
        val fragment = LearnFragment()
        fragment.board = board

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,fragment)
                .addToBackStack("learn")
                .commit()
    }

    private fun checkLicense() {
        if (!(isLicenseAccepted())) {
            val licenseDialog = LicenseDialog(this, onAccept, onExit)
            licenseDialog.show()
        } else {
            startNotifictionaryService()
        }
    }

    private fun setNavViewFont() {
        val font = ResourcesCompat.getFont(this, R.font.prompt_regular)
        (0 until nav_view.menu.size()).map {
            nav_view.menu.getItem(it)
        }.forEach { menuItem ->
            SpannableString(menuItem.title).apply {
                setSpan(CustomTypefaceSpan("", font!!), 0, menuItem.title.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                menuItem.title = this
            }
            if (menuItem.hasSubMenu()) {
                (0 until menuItem.subMenu.size()).map {
                    menuItem.subMenu.getItem(it)
                }.forEach {
                    SpannableString(menuItem.title).apply {
                        setSpan(CustomTypefaceSpan("", font!!), 0, it.title.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                        it.title = this
                    }
                }
            }
        }
    }

    private fun startNotifictionaryService() {
        Intent(this, NotifictionaryService::class.java).apply {
            action = ACTION_INIT
            startService(this)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_history -> startHistoryActivity()
            R.id.action_settings -> startSettingsActivity()
            R.id.action_about -> startAboutActivity()
            R.id.action_telegram -> openTelegram()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openTelegram() {
        Intent(ACTION_VIEW).apply {
            data = Uri.parse("https://t.me/notifictionary")
            startActivity(this)
        }
    }

    private fun startAboutActivity() {
        startActivity(AboutActivity::class.java)
    }

    private fun startSettingsActivity() {
        startActivity(SettingsActivity::class.java)
    }

    private fun startImportActivity() {
        Importer.import_(this)
    }

    private fun startHistoryActivity() {
        startActivity(HistoryActivity::class.java)
    }

    private fun startCategoriesActivity() {

    }

    private fun startQuizActivity() {
        startActivity(AssayActivity::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        firstLaunchFinished()
    }

}
