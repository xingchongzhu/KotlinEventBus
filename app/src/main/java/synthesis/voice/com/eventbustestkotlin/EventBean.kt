package synthesis.voice.com.eventbustestkotlin

class EventBean(one:String, two:String) {
    var one:String = one
    var two:String = two
    override fun toString(): String {
        return one+" "+two
    }
}