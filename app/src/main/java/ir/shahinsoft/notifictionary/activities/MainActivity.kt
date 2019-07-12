package ir.shahinsoft.notifictionary.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.fragments.MainFragment
import ir.shahinsoft.notifictionary.model.Category
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.model.TranslateLanguage
import ir.shahinsoft.notifictionary.services.NotifictionaryService
import ir.shahinsoft.notifictionary.tasks.InsertFavoriteCategoryTask
import ir.shahinsoft.notifictionary.tasks.InsertTask
import ir.shahinsoft.notifictionary.utils.*
import ir.shahinsoft.notifictionary.widget.YesNoDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.translate.*
import java.lang.Exception

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val undoHandler = Handler()

    private val translateTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            hideOrShowFab()
            if (isTranslateChangingByStack) {
                isTranslateChangingByStack = false
            } else
                if (!(s.isEmpty())) undoHandler.postDelayed({ if (textTranslate.text.toString() == s.toString() && (translateStack.isEmpty() || translateStack.peek() != s.toString())) translateStack.push(s.toString()) }, 1000)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            hideOrShowFab()
        }
    }
    private val wordTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            hideOrShowFab()
            val word = textWord.text.toString()
            translateWord(word)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            hideOrShowFab()
        }
    }
    private val translateListener = object : Translator.TranslateListener {
        override fun onWordTranslated(translate: String?) {
            if (translate != null) {
                textTranslate.setText(translate)
            } else {
                onFailedToTranslate(null)
            }
        }

        override fun onFailedToTranslate(reason: String?) {
            toast(reason ?: getString(R.string.cannot_translate))
        }
    }

    private fun translateWord(word: String) {
        if (word.isEmpty()) return

        Translator.with(this)
                .TranslateTo(target.get())
                .callback(translateListener)
                .translate(word)
    }

    @SuppressLint("RestrictedApi")
    private fun hideOrShowFab() {
        val content = textTranslate.text.toString()
        val word = textWord.text.toString()
        if (word.isEmpty() || content.isEmpty()) {
            fab.visibility = View.GONE
        } else {
            fab.show()
        }
    }

    private val stackListener = object : CallbackStack.Callback<String> {
        override fun onItemInserted(item: String?) {
            undoTranslation.show()
        }

        override fun onStackEmpty() {
            undoTranslation.hide()
        }

        override fun onItemRemoved(item: String?) {
            //doing nothing
        }

    }

    private val appBarStateChangeListener = object : AppBarStateChangeListener() {
        override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
            when (state) {
                State.IDLE -> changeTablayoutToWhite()
                State.COLLAPSED -> changeTablayoutToPrimary()
                else -> {
                }
            }
        }

    }

    private fun changeTablayoutToPrimary() {
        mainFragment.changeTablayoutToPrimary()
    }

    private fun changeTablayoutToWhite() {
        mainFragment.changeTablayoutToWhite()
    }

    private lateinit var source: TranslateLanguage
    private lateinit var target: TranslateLanguage
    private var isTranslateChangingByStack = false
    private var translateStack = CallbackStack<String>(stackListener)
    private lateinit var mainFragment: MainFragment

    val onYesClicked = {dialog: DialogInterface, which: Int ->
        getSharedPreferences(APP, Context.MODE_PRIVATE).edit().putBoolean(LICENSE_ACCEPTANCE, true).apply()
        startNotifictionaryService()
    }

    val onNoClicked = {dialog: DialogInterface, which: Int ->
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        if (!(isLicenseAccepted())) {

        val dialog = AlertDialog.Builder(this)
            dialog.setTitle("License")
            dialog.setMessage("This app will use your information for scientific purposes and to make profit. \nYour identity will not be disclosed.\nPlease confirm to continue use.")
            dialog.setPositiveButton("Confirm" ,DialogInterface.OnClickListener(function = onYesClicked))
            dialog.setNegativeButton("EXIT",DialogInterface.OnClickListener(function = onNoClicked))
            dialog.show()
        }else{
            startNotifictionaryService()
        }
        setSupportActionBar(toolbar)

        insertFavoriteCategory()



        //making cardView stays bellow toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.elevation = UiUtils.convertDpToPx(this, 8f)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList = null


        setNavViewFont()

        mainFragment = MainFragment().apply {
            supportFragmentManager.beginTransaction().add(R.id.layoutWordList, this).commit()
        }


        textTranslate.addTextChangedListener(translateTextWatcher)
        textWord.addTextChangedListener(wordTextWatcher)
        fab.setOnClickListener { addNewWordToDatabase() }


        initTranslation()

        undoTranslation.setOnClickListener { undoTranslation() }
        undoTranslation.setOnLongClickListener { toast(R.string.undo); true }

        fab.setOnLongClickListener { toast(R.string.add_to_learn);true }

        appBarLayout.addOnOffsetChangedListener(appBarStateChangeListener)

        textSource.setOnClickListener { selectSource() }

        textTarget.setOnClickListener { selectTarget() }

        imgSwitch.setOnClickListener { swapTranslateLanguages() }

    }

    override fun onResume() {
        super.onResume()
        initUser()

        updateNavView()

    }

    private fun updateNavView() {
        if (isLogin(this)) {
            nav_view.menu.findItem(R.id.login).isVisible = false
            nav_view.menu.findItem(R.id.action_logout).isVisible = true
        } else {
            nav_view.menu.findItem(R.id.login).isVisible = true
            nav_view.menu.findItem(R.id.action_logout).isVisible = false
        }
    }

    private fun initUser() {
        val container = nav_view.getHeaderView(0).findViewById<LinearLayout>(R.id.parentLayout)
        container.setBackgroundColor(manipulateColor(getApplicationColor(), 0.8f))
        val profile = nav_view.getHeaderView(0).findViewById<ImageView>(R.id.imageProfile)
        val textUsername = nav_view.getHeaderView(0).findViewById<TextView>(R.id.textUsername)
        val textPoints = nav_view.getHeaderView(0).findViewById<TextView>(R.id.textPoints)

        val email = getEmail(context = this)
        val url = getGravatarUrl(this)
        val username = getUsername(this)
        val points = getPoints(this)

        if (email!!.isNotEmpty()) {
            Picasso.get().load(url).into(object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    bitmap?.apply { profile.setImageBitmap(getCroppedBitmap(this)) }
                }

            })
        }
        textUsername.text = username
        textPoints.text = points.toString() + " Points"
    }


    fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = 0xff424242
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height);

        paint.isAntiAlias = true;
        canvas.drawARGB(0, 0, 0, 0);
        paint.color = color.toInt()
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
                (bitmap.width / 2).toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
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

    private fun insertFavoriteCategory() {
        if (isFirstLaunch()) {

            InsertFavoriteCategoryTask(getAppDatabase()).execute(Category(getString(R.string.favorite), 1))
        }
    }

    private fun undoTranslation() {
        translateStack.pop()
        val word = if (translateStack.isEmpty()) "" else translateStack.peek()
        isTranslateChangingByStack = true
        textTranslate.setText(word)
    }

    private fun startNotifictionaryService() {
        Intent(this, NotifictionaryService::class.java).apply {
            action = ACTION_INIT
            startService(this)
        }
    }

    private fun addNewWordToDatabase() {
        val word = textWord.text.toString()
        val translateText = textTranslate.text.toString()
        val translate = createTranslate(word, translateText)
        translate.lang = "${source.get()}-${target.get()}"

        InsertTask(getAppDatabase()) {
            toast(String.format(getString(R.string.add_message_format), translate.name))
        }.execute(translate)

        mainFragment.addTranslateToCurrentCategory(translate)
        textWord.text?.clear()
        textTranslate.text?.clear()
        translateStack.clear()
    }

    private fun createTranslate(word: String, translateText: String): Translate {
        val regex = Regex("[!@#$%^&*()_+=\"\';:?/><.,]")

        return Translate().apply {
            name = regex.replace(word, " ")
            translate = translateText
            correctCount = 0
            wrongCount = 0
            catId = mainFragment.getCatId()
            type = resources.getStringArray(R.array.word_types)[0]
        }
    }

    private fun selectTarget() {
        val intent = Intent(this, SelectLanguageActivity::class.java)
        intent.putExtra("title", R.string.title_target)
        intent.putExtra("show", false)
        startActivityForResult(intent, 30)
    }

    private fun selectSource() {
        val intent = Intent(this, SelectLanguageActivity::class.java)
        intent.putExtra("title", R.string.title_source)
        intent.putExtra("show", true)
        startActivityForResult(intent, 20)
    }

    private fun swapTranslateLanguages() {
        val temp = source
        source = target
        target = temp

        textSource.text = source.toString()
        textTarget.text = target.toString()
        val sp = getSharedPreferences(TRANSLATION, Context.MODE_PRIVATE)

        sp!!.edit().putString(SOURCE_FULL, source.toString())
                .putString(SOURCE_SMALL, source.get())
                .putString(TARGET_FULL, target.toString())
                .putString(TARGET_SMALL, target.get()).apply()
    }

    private fun initTranslation() {
        val sf = getSharedPreferences(TRANSLATION, Context.MODE_PRIVATE)
        val translateSource = sf.getString(SOURCE_FULL, DEFAULT_SOURCE)
        val translateSourceSmall = sf.getString(SOURCE_SMALL, DEFAULT_SOURCE_SMALL)
        if (translateSourceSmall == null || translateSourceSmall.isEmpty()) {
            imgSwitch.tint(R.color.gray_transparent)
            imgSwitch.isEnabled = false
        } else {
            imgSwitch.tint(R.color.colorSecondary)
            imgSwitch.isEnabled = true
        }
        source = TranslateLanguage(translateSource, translateSourceSmall)
        val translateTarget = sf.getString(TARGET_FULL, DEFAULT_TARGET)
        val translateTargetSmall = sf.getString(TARGET_SMALL, DEFAULT_TARGET_SMALL)
        target = TranslateLanguage(translateTarget, translateTargetSmall)

        textSource.text = translateSource
        textTarget.text = translateTarget
    }

    private fun updateSource(data: Intent) {
        val source = data.getStringExtra(NAME)
        val short_ = data.getStringExtra(SHORT_NAME)
        this.source = TranslateLanguage(source, short_)
        textSource.text = source
        val sp = getSharedPreferences(TRANSLATION, Context.MODE_PRIVATE)
        sp.edit().putString(SOURCE_FULL, source).apply()
        sp.edit().putString(SOURCE_SMALL, short_).apply()
        if (short_ == null || short_.isEmpty()) {
            imgSwitch.tint(R.color.gray_transparent)
            imgSwitch.isEnabled = false
        } else {
            imgSwitch.tint(R.color.colorSecondary)
            imgSwitch.isEnabled = true
        }
    }

    private fun updateTarget(data: Intent) {
        val sp = getSharedPreferences(TRANSLATION, Context.MODE_PRIVATE)
        val source = data.getStringExtra(NAME)
        val short_ = data.getStringExtra(SHORT_NAME)
        this.target = TranslateLanguage(source, short_)
        textTarget.text = source
        sp.edit().putString(TARGET_FULL, source).apply()
        sp.edit().putString(TARGET_SMALL, short_).apply()
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
            R.id.action_quiz -> startQuizActivity()
            R.id.action_categories -> startCategoriesActivity()
            R.id.action_history -> startHistoryActivity()
            R.id.action_import -> startImportActivity()
            R.id.action_export -> startExportActivity()
            R.id.action_settings -> startSettingsActivity()
            R.id.action_about -> startAboutActivity()
            R.id.action_telegram -> openTelegram()
            R.id.login -> openLoginActivity()
            R.id.action_logout -> logout()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        //todo implement yes no dialog for logout
        setLogin(this, false)
        updateNavView()
    }

    private fun openLoginActivity() {
        Intent(this, LoginActivity::class.java).apply { startActivity(this) }
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
        if (isXLargeTablet())
            startActivity(SettingsActivity::class.java)
        else {
            startActivity(SettingActivityPhone::class.java)
        }
    }

    private fun isXLargeTablet(): Boolean {
        return resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
    }

    private fun startExportActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_WRITE_EXTERNAL_CODE)
                return
            }
        }
        startActivity(ExportActivity::class.java)
    }

    private fun startImportActivity() {
        Importer.import_(this)
    }

    private fun startHistoryActivity() {
        startActivity(HistoryActivity::class.java)
    }

    private fun startCategoriesActivity() {
        startActivity(CategoryActivity::class.java)
    }

    private fun startQuizActivity() {
        startActivity(AssayActivity::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_UPDATE_SOURCE_CODE -> {
                if (resultCode != Activity.RESULT_OK) return
                updateSource(data!!)
            }
            REQUEST_UPDATE_TARGET_CODE -> {
                if (resultCode != Activity.RESULT_OK) return
                updateTarget(data!!)
            }
            Importer.RESULT_CODE -> {
                if (data?.data != null) {
                    val intent = Intent(this, ImportActivity::class.java)
                    intent.data = data.data
                    startActivity(intent)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firstLaunchFinished()
    }

}
