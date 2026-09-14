package com.jarvisos

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private lateinit var content: FrameLayout
    private val cyan = Color.rgb(0, 220, 255)
    private val bg = Color.rgb(4, 8, 12)

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        buildUI()
    }

    private fun buildUI() {
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(bg) }
        val top = TextView(this).apply {
            text = "  JARVIS OS                                      ONLINE"
            textSize = 16f; setTextColor(cyan); setPadding(10,12,10,12)
            setBackgroundColor(Color.rgb(8,16,22))
        }
        root.addView(top, LinearLayout.LayoutParams(-1, 52))
        content = FrameLayout(this)
        root.addView(content, LinearLayout.LayoutParams(-1, 0, 1f))
        val nav = LinearLayout(this).apply { setBackgroundColor(Color.rgb(7,13,18)) }
        listOf("HOME","AI","BROWSER","CAMERA","SYSTEM","APPS","SETTINGS").forEach { label ->
            val b = Button(this).apply {
                text = label; textSize = 11f; setTextColor(cyan); setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener { openModule(label) }
            }
            nav.addView(b, LinearLayout.LayoutParams(0, 58, 1f))
        }
        root.addView(nav)
        setContentView(root)
        openModule("HOME")
    }

    private fun clear() { content.removeAllViews() }

    private fun openModule(name: String) {
        clear()
        when(name) {
            "HOME" -> home()
            "AI" -> ai()
            "BROWSER" -> browser()
            "CAMERA" -> camera()
            "SYSTEM" -> system()
            "APPS" -> apps()
            "SETTINGS" -> settings()
        }
    }

    private fun title(t:String) = TextView(this).apply {
        text=t; textSize=26f; setTextColor(cyan); setPadding(25,20,10,10)
    }

    private fun home() {
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL; setPadding(25,15,25,15)}
        box.addView(title("JARVIS // COMMAND DECK"))
        val status=TextView(this).apply{
            text="\n● CORE ONLINE\n\nAI READY     CAMERA READY     BROWSER READY\n\nSay or type a command below.\n"
            textSize=18f; setTextColor(Color.LTGRAY)
        }
        box.addView(status)
        val time=TextView(this).apply{textSize=22f;setTextColor(cyan)}
        box.addView(time)
        Handler(Looper.getMainLooper()).post(object:Runnable{override fun run(){
            time.text=SimpleDateFormat("HH:mm:ss  •  dd.MM.yyyy",Locale.getDefault()).format(Date())
            Handler(Looper.getMainLooper()).postDelayed(this,1000)
        }})
        content.addView(box)
    }

    private fun ai() {
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(20,10,20,10)}
        box.addView(title("JARVIS // AI"))
        val chat=TextView(this).apply{text="JARVIS: Awaiting command...\n";textSize=17f;setTextColor(Color.LTGRAY)}
        box.addView(chat,LinearLayout.LayoutParams(-1,0,1f))
        val row=LinearLayout(this)
        val input=EditText(this).apply{hint="Enter command...";setTextColor(Color.WHITE);setHintTextColor(Color.GRAY)}
        val send=Button(this).apply{text="EXECUTE";setTextColor(cyan);setOnClickListener{
            val q=input.text.toString().trim()
            if(q.isEmpty()) return@setOnClickListener
            chat.append("\nUSER: $q\nJARVIS: Processing...\n")
            when {
                q.contains("camera",true)->{chat.append("Opening camera module.\n");openModule("CAMERA")}
                q.contains("browser",true)->{chat.append("Opening browser.\n");openModule("BROWSER")}
                q.contains("system",true)->{chat.append("Opening system monitor.\n");openModule("SYSTEM")}
                else->chat.append("Command recognized as conversation. Connect an AI endpoint in Settings for full AI reasoning.\n")
            }
            input.text.clear()
        }}
        row.addView(input,LinearLayout.LayoutParams(0,-2,1f));row.addView(send,LinearLayout.LayoutParams(-2,-2))
        box.addView(row);content.addView(box)
    }

    private fun browser() {
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        val row=LinearLayout(this)
        val url=EditText(this).apply{hint="https://...";setSingleLine(true);setTextColor(Color.WHITE)}
        val go=Button(this).apply{text="GO";setTextColor(cyan)}
        row.addView(url,LinearLayout.LayoutParams(0,55,1f));row.addView(go,LinearLayout.LayoutParams(100,55))
        val web=WebView(this).apply{settings.javaScriptEnabled=true;webViewClient=WebViewClient()}
        go.setOnClickListener{var u=url.text.toString();if(!u.startsWith("http"))u="https://www.google.com/search?q="+Uri.encode(u);web.loadUrl(u)}
        box.addView(row);box.addView(web,LinearLayout.LayoutParams(-1,0,1f));content.addView(box)
    }

    private fun camera() {
        if(Build.VERSION.SDK_INT>=23 && checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.CAMERA),10)
        val b=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        b.addView(title("JARVIS // CAMERA"))
        b.addView(TextView(this).apply{text="Camera permission is requested when this module is opened.\n\nLive camera integration is isolated here for future vision modules.";textSize=17f;setTextColor(Color.LTGRAY);setPadding(25,20,25,20)})
        content.addView(b)
    }

    private fun system() {
        val mem=Runtime.getRuntime().maxMemory()/1024/1024
        val b=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(25,15,25,15)}
        b.addView(title("JARVIS // SYSTEM"))
        b.addView(TextView(this).apply{
            text="Android: ${Build.VERSION.RELEASE}\nDevice: ${Build.MODEL}\nSDK: ${Build.VERSION.SDK_INT}\nRuntime memory limit: ${mem} MB\n\nJARVIS CORE STATUS: ONLINE"
            textSize=18f;setTextColor(Color.LTGRAY)
        })
        content.addView(b)
    }

    private fun apps() {
        val b=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(25,15,25,15)}
        b.addView(title("JARVIS // APP LAUNCHER"))
        b.addView(TextView(this).apply{text="Installed applications can be launched through Android intents.\n\nUse this module as the foundation for a searchable launcher.";textSize=18f;setTextColor(Color.LTGRAY)})
        content.addView(b)
    }

    private fun settings() {
        val b=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(25,15,25,15)}
        b.addView(title("JARVIS // SETTINGS"))
        val endpoint=EditText(this).apply{hint="AI endpoint (optional)";setTextColor(Color.WHITE)}
        val model=EditText(this).apply{hint="Model name";setTextColor(Color.WHITE)}
        b.addView(endpoint);b.addView(model)
        b.addView(Button(this).apply{text="SAVE CONFIG";setTextColor(cyan);setOnClickListener{
            getPreferences(0).edit().putString("endpoint",endpoint.text.toString()).putString("model",model.text.toString()).apply()
            Toast.makeText(this@MainActivity,"Configuration saved",Toast.LENGTH_SHORT).show()
        }})
        b.addView(Button(this).apply{text="ANDROID APP SETTINGS";setOnClickListener{startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:$packageName")))}})
        content.addView(b)
    }
}
