package me.ironblock.oneblockpvp.oneblockpvpplugin.voicechat

object VoiceMixer {
    fun mix(vararg byteArrays: ByteArray): ByteArray {
        val longest = byteArrays.toList().stream().max { o1, o2 -> o1.size - o2.size }.get().size
        val toReturn = ByteArray(longest)
        toReturn.forEachIndexed { index, _ ->
            byteArrays.forEach { bytes ->
                if (index < bytes.size){
                    var result = toReturn[index]+bytes[index]
                    if (result>127)result=127
                    if (result<-127)result=-127
                    toReturn[index] = result.toByte()
                }

            }
        }
        return toReturn


    }
}