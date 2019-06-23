package synthesis.voice.com.eventbustestkotlin

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button

class SecondActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second)
        findViewById<Button>(R.id.click).setOnClickListener(View.OnClickListener {
            EventBus.getDefault().post(EventBean("测试","发送消息给你"))
        })

    }
}