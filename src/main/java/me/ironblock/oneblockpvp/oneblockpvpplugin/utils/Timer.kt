package me.ironblock.oneblockpvp.oneblockpvpplugin.utils

class Timer(var timer: Int) {
    var counter = 0
    fun update(): Boolean {
        counter++
        if (counter > timer) {
            reset()
            return true
        }
        return false
    }

    fun reset() {
        counter = 0
    }



    fun setTime(time:Int){
        timer = time
    }
}