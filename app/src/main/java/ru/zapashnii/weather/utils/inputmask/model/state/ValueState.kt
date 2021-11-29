package ru.zapashnii.weather.utils.inputmask.model.state

import ru.zapashnii.weather.utils.inputmask.model.Next
import ru.zapashnii.weather.utils.inputmask.model.State

/**
 * ### ValueState
 *
 * Обозначает обязательные символы в квадратных скобках [].
 *
 * Принимает только символы собственного типа (см. `` StateType``). Помещает принятые символы в
 * строка результата.
 *
 * Возвращает принятые символы как извлеченное значение.
 *
 * @ см. `` ValueState.StateType``
 *
 * @author taflanidi
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
class ValueState : State {

    /**
     * ### StateType
     *
     * ```«Numeric```       означает [9] символов.
     * ```Literal```        означает символы [a].
     * ```AlphaNumeric```   означает символы [-].
     * ```Многоточие```     означает […] символов.
     */
    sealed class StateType {
        class Numeric : StateType()
        class Literal : StateType()
        class AlphaNumeric : StateType()
        class Ellipsis(val inheritedType: StateType) : StateType()
        class Custom(val character: Char, val characterSet: String) : StateType()
    }

    val type: StateType

    /**
     * Конструктор эллиптического ```ValueState```
     */
    constructor(inheritedType: StateType) : super(null) {
        this.type = StateType.Ellipsis(inheritedType)
    }

    constructor(child: State?, type: StateType) : super(child) {
        this.type = type
    }

    private fun accepts(character: Char): Boolean {
        return when (this.type) {
            is StateType.Numeric -> character.isDigit()
            is StateType.Literal -> character.isLetter()
            is StateType.AlphaNumeric -> character.isLetterOrDigit()
            is StateType.Ellipsis -> when (this.type.inheritedType) {
                is StateType.Numeric -> character.isDigit()
                is StateType.Literal -> character.isLetter()
                is StateType.AlphaNumeric -> character.isLetterOrDigit()
                is StateType.Custom -> this.type.inheritedType.characterSet.contains(character)
                else -> false
            }
            is StateType.Custom -> this.type.characterSet.contains(character)
        }
    }

    override fun accept(character: Char): Next? {
        if (!this.accepts(character)) return null

        return Next(
            this.nextState(),
            character,
            true,
            character
        )
    }

    val isElliptical: Boolean
        get() = when (this.type) {
            is StateType.Ellipsis -> true
            else -> false
        }

    override fun nextState(): State = when (this.type) {
        is StateType.Ellipsis -> this
        else -> super.nextState()
    }

    override fun toString(): String {
        return when (this.type) {
            is StateType.Literal -> "[A] -> " + if (null == this.child) "null" else child.toString()
            is StateType.Numeric -> "[0] -> " + if (null == this.child) "null" else child.toString()
            is StateType.AlphaNumeric -> "[_] -> " + if (null == this.child) "null" else child.toString()
            is StateType.Ellipsis -> "[…] -> " + if (null == this.child) "null" else child.toString()
            is StateType.Custom -> "[" + this.type.character + "] -> " + if (null == this.child) "null" else child.toString()
        }
    }

}
