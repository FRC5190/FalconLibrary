package frc.team5190.lib.utils

fun <T> Array<out T>.asCollection(): Collection<T> = ArrayAsCollection(this)

private class ArrayAsCollection<T>(val values: Array<out T>) : Collection<T> {
    override val size: Int get() = values.size
    override fun isEmpty(): Boolean = values.isEmpty()
    override fun contains(element: T): Boolean = values.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }
    override fun iterator(): Iterator<T> = values.iterator()
    // override hidden toArray implementation to prevent copying of values array
    fun toArray(): Array<out Any?> = values.copyOf()
}