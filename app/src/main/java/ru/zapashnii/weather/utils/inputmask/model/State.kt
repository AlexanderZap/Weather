package ru.zapashnii.weather.utils.inputmask.model

/**
 * ### State
 *
 * Состояние маски, аналогичное состоянию в регулярных выражениях.
 * Каждое состояние представляет собой символ из строки формата маски.
 *
 * @author taflanidi
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
abstract class State(val child: State?) {

    /**
     * Абстрактный метод.
     *
     * Определяет, принимает ли состояние вводимый пользователем символ или нет, и какие действия следует предпринять
     * место, когда персонаж принят.
     *
     * @param character символ из строки, вводимой пользователем.
     *
     * @returns `` Next`` экземпляр объекта с набором действий, которые должны произойти, когда пользователь
     * вводимый символ допустим.
     *
     * @throws Exception, если метод не реализован.
     */
    abstract fun accept(character: Char): Next?

    /**
     * Автоматический ввод данных пользователем.
     *
     * @returns ```Next``` экземпляр объекта с набором действий для завершения пользовательского ввода. Если нет
     * доступно автозаполнение, возвращает ```null```.
     */
    open fun autocomplete(): Next? {
        return null
    }

    /**
     * Получить следующее состояние.
     *
     * Иногда необходимо изменить это поведение. Например, ```State``` может захотеть
     * вернуть ```self``` в качестве следующего состояния при определенных условиях.
     *
     * @returns объект State.
     */
    open fun nextState(): State {
        return this.child!!
    }

    override fun toString(): String {
        return "BASE -> " + if (null != this.child) this.child.toString() else "null"
    }
}