package synthesis.voice.com.eventbustestkotlin;

import android.os.Handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    private Class<?> type;
    private Map<Object,List<SubscrbileMethod>> cache= new HashMap<Object,List<SubscrbileMethod>>();
    Method method;
    Subscrbile subscrbile = method.getAnnotation(Subscrbile.class);
    Handler handler;
    private void  tt(){
        EventBus.Companion.getDefault().register(this);
        handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
