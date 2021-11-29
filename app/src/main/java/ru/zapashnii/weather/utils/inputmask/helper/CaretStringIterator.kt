package ru.zapashnii.weather.utils.inputmask.helper

import ru.zapashnii.weather.utils.inputmask.model.CaretString

/**
 * ### CaretStringIterator
 *
 * Обходит символы CaretString.string. Каждый вызов next () возвращает текущий символ и регулирует позицию итератора.
 *
 * ```CaretStringIterator``` используется экземпляром ```Mask``` для перебора строки, которая должна быть отформатирована.
 *
 * @author taflanidi
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
open class CaretStringIterator(
    protected val caretString: CaretString,
    protected var currentIndex: Int = 0
) {

    open fun insertionAffectsCaret(): Boolean {
        return when (this.caretString.caretGravity) {
            is CaretString.CaretGravity.BACKWARD -> this.currentIndex < this.caretString.caretPosition
            is CaretString.CaretGravity.FORWARD -> this.currentIndex <= this.caretString.caretPosition
                || (0 == this.currentIndex && 0 == this.caretString.caretPosition)
        }
    }

    open fun deletionAffectsCaret(): Boolean {
        return this.currentIndex < this.caretString.caretPosition
    }

    /**
     * Итерации по ```CaretString.string```
     * @postcondition: позиция итератора перемещается на следующий символ.
     * @returns Текущий символ. Если итератор дошел до конца строки, возвращает ```nil```.
     */
    open fun next(): Char? {
        if (this.currentIndex >= this.caretString.string.length) {
            return null
        }

        val char: Char = this.caretString.string.toCharArray()[this.currentIndex]
        this.currentIndex += 1
        return char
    }

}
