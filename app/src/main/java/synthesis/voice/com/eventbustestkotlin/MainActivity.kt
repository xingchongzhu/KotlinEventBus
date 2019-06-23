package synthesis.voice.com.eventbustestkotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    val Tag:String = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this as Object)
        val intent:Intent = Intent(this,SecondActivity::class.java)
        startActivity(intent)
       
    }

    @Subscrbile(threadMode = ThreadMode.MAIN)
    fun getMessage(bean:EventBean){
        Log.d(Tag,bean.toString())
    }
}
