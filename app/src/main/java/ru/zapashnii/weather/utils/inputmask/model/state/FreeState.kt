package ru.zapashnii.weather.utils.inputmask.model.state

import ru.zapashnii.weather.utils.inputmask.model.Next
import ru.zapashnii.weather.utils.inputmask.model.State

/**
 * ### FreeState
 *
 * Обозначает «свободные» символы за пределами квадратных и фигурных скобок.
 *
 * Принимает каждый символ, но не помещает его в строку результата, если только этот символ не равен
 * один из формата маски. Если это не так, вставляет символ из формата маски в
 * результат.
 *
 * Всегда возвращает ```null``` как извлеченное значение, не влияет на результирующее значение.
 *
 * @author taflanidi
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
class FreeState(child: State, val ownCharacter: Char) : State(child) {

    override fun accept(character: Char): Next? {
        return if (this.ownCharacter == character) {
            Next(
                this.nextState(),
                character,
                true,
                null
            )
        } else {
            Next(
                this.nextState(),
                this.ownCharacter,
                false,
                null
            )
        }
    }

    override fun autocomplete(): Next? {
        return Next(
            this.nextState(),
            this.ownCharacter,
            false,
            null
        )
    }

    override fun toString(): String {
        return "" + this.ownCharacter + " -> " + if (null == this.child) "null" else child.toString()
    }
}
