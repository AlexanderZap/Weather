package ru.zapashnii.weather.utils.inputmask.helper

import ru.zapashnii.weather.utils.inputmask.model.CaretString

/**
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
class RTLCaretStringIterator(caretString: CaretString) : CaretStringIterator(caretString) {
    override fun insertionAffectsCaret(): Boolean {
        return this.currentIndex <= this.caretString.caretPosition
    }
}