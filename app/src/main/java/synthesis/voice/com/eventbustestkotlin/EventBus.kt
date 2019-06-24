package synthesis.voice.com.eventbustestkotlin

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import synthesis.voice.com.eventbustestkotlin.R.id.async
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EventBus {
    val Tag : String = "EventBus"
    private var cacheMap:HashMap<Object,ArrayList<SubscrbileMethod>>? = null
    //private val cache = java.util.HashMap<Any, List<SubscrbileMethod>>()
    private var handler: Handler? = null
    companion object {
       private var instance:EventBus? = null
               get() {
                   if (field == null) {
                       field = EventBus()
                   }
                   return field
               }
        @Synchronized
        fun getDefault():EventBus{
            return instance!!
        }
    }
    private constructor(){
        handler = Handler(Looper.getMainLooper())
        cacheMap = HashMap()

    }

    fun register(item: Object){
       var list : ArrayList<SubscrbileMethod>? = cacheMap?.get(item)
        if(list == null){
            list = findSubscrbileMethods(item)
            list?.let { cacheMap?.put(item, it) }
        }
    }

    fun unregister(item: Object){
        var list : ArrayList<SubscrbileMethod>? = cacheMap?.get(item)
        list?.clear()
        cacheMap?.remove(item)
    }

    fun post(bean:EventBean){
        //变量cacheMap所有带有bean类型的方法并调用
        cacheMap?.forEach {
            var vm:Map.Entry<Object,List<SubscrbileMethod>> = it
            it.value.forEach {
                val subscrbileMethod: SubscrbileMethod = it
                //判断当前类型与传入类型关系
                if(it.type!!.isAssignableFrom(bean.javaClass)){
                    when(subscrbileMethod.mThreadMod){
                         ThreadMode.MAIN->{
                            if(Looper.myLooper() == Looper.getMainLooper()){
                                invoke(it,vm,bean)
                            }else{
                                handler?.post(Runnable {
                                    invoke(it,vm,bean)
                                })
                            }
                        }
                        ThreadMode.BACKGROUND ->{
                            if(Looper.myLooper() != Looper.getMainLooper()){
                                invoke(it,vm,bean)
                            }else {
                                GlobalScope.launch(){
                                    invoke(it,vm,bean)
                                }
                            }
                        }
                        else->{
                            Log.d(Tag,"can't match")
                        }
                    }
                }
            }
        }
    }

    private fun invoke(subscrbileMethod: SubscrbileMethod, vm: Map.Entry<Object, List<SubscrbileMethod>>, bean: EventBean) {
        val method = subscrbileMethod.mMeath
        //调用注解方法
        method?.invoke(vm.key, bean)
    }


    fun findSubscrbileMethods(item:Object):ArrayList<SubscrbileMethod>{
        var list : ArrayList<SubscrbileMethod> = ArrayList()
        //找到类对象
        var clazz : Class<*> = item.`class`
        //循环查找父类带有注解的方法
        while(clazz != null){
            val name : String = clazz.name
            if(name.startsWith("java.") || name .startsWith("javax.")
              || name.startsWith("android.")){
                break
            }
            //找到类中所有方法
            var methods: Array<Method>? = clazz.declaredMethods
            methods?.forEach {
                var method = it
                //获取每一个方法所有Subscrbile注解
                var subscrbile=it.getAnnotation(Subscrbile::class.java)
                subscrbile?.let {
                    //找到注解参数
                    val types:Array<Class<*>> ? = method.parameterTypes
                    if(types?.size == 1){
                        //获取注解参数
                        val threadMode : ThreadMode = it.threadMode
                        val subscrbileMethod : SubscrbileMethod = SubscrbileMethod(method,threadMode,types[0])
                        list.add(subscrbileMethod)
                    }else{
                        Log.w("EventBus","参数个数不只一个")
                    }
                }
            }
            clazz = clazz.superclass
        }
        return list
    }
}