package ru.zapashnii.weather.utils.inputmask.model

/**
 * ### Next
 *
 * Объект модели, представляющий набор действий, которые должны произойти при переходе от одного
 * ```State``` до другого.
 *
 * @author taflanidi
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
class Next(val state: State, val insert: Char?, val pass: Boolean, val value: Char?)
