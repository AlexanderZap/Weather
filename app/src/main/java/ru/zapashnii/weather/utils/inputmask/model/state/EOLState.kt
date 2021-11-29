package ru.zapashnii.weather.utils.inputmask.model.state

import ru.zapashnii.weather.utils.inputmask.model.Next
import ru.zapashnii.weather.utils.inputmask.model.State

/**
 * ### EOLState
 *
 * Конечное состояние. Служит символом-ограничителем формата маски.
 *
 * Не принимает никаких символов. Всегда возвращает ```self``` в качестве следующего состояния, игнорируя дочерний элемент.
 * состояние, указанное во время инициализации.
 *
 * @author taflanidi
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
class EOLState : State(child = null) {
    override fun accept(character: Char): Next? {
        return null
    }

    override fun toString(): String {
        return "EOL"
    }
}