package ru.zapashnii.weather.utils.inputmask.model.state

import ru.zapashnii.weather.utils.inputmask.model.Next
import ru.zapashnii.weather.utils.inputmask.model.State

/**
 * ### OptionalValueState
 *
 * Представляет необязательные символы в квадратных скобках [].
 *
 * Принимает любые символы, но помещает в строку результата только символы собственного типа
 * (см. `` StateType``).
 *
 * Возвращает принятые символы собственного типа как извлеченное значение.
 *
 * @see ```OptionalValueState.StateType```
 *
 * @author taflanidi
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
class OptionalValueState(child: State, val type: StateType) : State(child) {

    sealed class StateType {
        class Numeric : StateType()
        class Literal : StateType()
        class AlphaNumeric : StateType()
        class Custom(val character: Char, val characterSet: String) : StateType()
    }

    private fun accepts(character: Char): Boolean {
        return when (this.type) {
            is StateType.Numeric -> character.isDigit()
            is StateType.Literal -> character.isLetter()
            is StateType.AlphaNumeric -> character.isLetterOrDigit()
            is StateType.Custom -> this.type.characterSet.contains(character)
        }
    }

    override fun accept(character: Char): Next? {
        return if (this.accepts(character)) {
            Next(
                this.nextState(),
                character,
                true,
                character
            )
        } else {
            Next(
                this.nextState(),
                null,
                false,
                null
            )
        }
    }

    override fun toString(): String {
        return when (this.type) {
            is StateType.Literal -> "[a] -> " + if (null == this.child) "null" else child.toString()
            is StateType.Numeric -> "[9] -> " + if (null == this.child) "null" else child.toString()
            is StateType.AlphaNumeric -> "[-] -> " + if (null == this.child) "null" else child.toString()
            is StateType.Custom -> "[" + this.type.character + "] -> " + if (null == this.child) "null" else child.toString()
        }
    }
}