package frc.team5190.lib.utils

import java.util.*

class EvictingQueue<E>(val maxSize: Int) : ArrayDeque<E>(maxSize) {

    override fun addFirst(e: E) {
        if(size >= maxSize)
            removeLast()
        super.addFirst(e)
    }

    override fun addLast(e: E) {
        if(size >= maxSize)
            removeFirst()
        super.addLast(e)
    }

}