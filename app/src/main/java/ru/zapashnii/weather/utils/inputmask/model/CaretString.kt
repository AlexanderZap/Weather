package ru.zapashnii.weather.utils.inputmask.model

/**
 * ### CaretString
 *
 * Объект модели, представляющий строку с текущей позицией курсора.
 *
 * @author taflanidi
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
data class CaretString(
    val string: String,
    val caretPosition: Int,
    val caretGravity: CaretGravity
) {
    fun reversed() =
        CaretString(
            this.string.reversed(),
            this.string.length - this.caretPosition,
            this.caretGravity
        )

    sealed class CaretGravity {
        class FORWARD(val autocompleteValue: Boolean) : CaretGravity()
        class BACKWARD(val autoskipValue: Boolean) : CaretGravity()

        val autocomplete: Boolean
            get() = when (this) {
                is FORWARD -> this.autocompleteValue
                else -> false
            }

        val autoskip: Boolean
            get() = when (this) {
                is BACKWARD -> this.autoskipValue
                else -> false
            }
    }
}
