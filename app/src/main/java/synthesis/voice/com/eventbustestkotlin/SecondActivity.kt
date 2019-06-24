package synthesis.voice.com.eventbustestkotlin

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SecondActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second)
        findViewById<Button>(R.id.click).setOnClickListener(View.OnClickListener {
            Log.d("SecondActivity","click")
            EventBus.getDefault().post(EventBean("测试main","发送消息给你"))
            /*GlobalScope.launch(){
                EventBus.getDefault().post(EventBean("测试background","发送消息给你"))
            }*/
        }
        )

    }
}