# KotlinEventBus
KotlinEventBus

介绍：
使用第三方框架可以加快项目开发进度，为了加强对EventBus框架原理的理解同时更好掌握kotlin语言，这次我使用kotlin实现eventbus基本功能：注册，取消注册，消息传递，主线程，子线程事件传递。

为了更好理解通过下面几个流程图来说明：

EventBus注册流程


//注解注册
fun register(item: Object){
        //判断改对象是否已经注册
       var list : ArrayList<SubscrbileMethod>? = cacheMap?.get(item)
        if(list == null){
            list = findSubscrbileMethods(item)
            //将所有注解方法和对象绑定到map中
            list?.let { cacheMap?.put(item, it) }
        }
}

//获取改注解所有注解方法
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
                    //只要一个参数的注解方法
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
消息通信流程


fun post(bean:EventBean){
        //变量cacheMap所有带有bean类型的方法并调用
        cacheMap?.forEach {
            var vm:Map.Entry<Object,List<SubscrbileMethod>> = it
            it.value.forEach {
                val subscrbileMethod: SubscrbileMethod = it
                //判断当前类型与传入类型关系
                if(it.type!!.isAssignableFrom(bean.javaClass)){
                    when(subscrbileMethod.mThreadMod){
                         ThreadMode.MAIN->{//主线程执行
                            if(Looper.myLooper() == Looper.getMainLooper()){//当前是在主线程
                                invoke(it,vm,bean)
                            }else{
                                handler?.post(Runnable {//handler切换到主线程
                                    invoke(it,vm,bean)
                                })
                            }
                        }
                        ThreadMode.BACKGROUND ->{//子线程执行
                            if(Looper.myLooper() != Looper.getMainLooper()){//当前是在子线程
                                invoke(it,vm,bean)
                            }else {
                                GlobalScope.launch(){//启动一个新的线程
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
注销流程


fun unregister(item: Object){
        var list : ArrayList<SubscrbileMethod>? = cacheMap?.get(item)
        list?.clear()
        cacheMap?.remove(item)
    }
