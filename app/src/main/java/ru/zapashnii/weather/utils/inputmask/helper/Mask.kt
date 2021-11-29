package ru.zapashnii.weather.utils.inputmask.helper

import ru.zapashnii.weather.utils.inputmask.model.CaretString
import ru.zapashnii.weather.utils.inputmask.model.Next
import ru.zapashnii.weather.utils.inputmask.model.Notation
import ru.zapashnii.weather.utils.inputmask.model.State
import ru.zapashnii.weather.utils.inputmask.model.state.*
import java.util.*

/**
 * ### Mask
 *
 * Обходит пользовательский ввод. Создает из него форматированные строки. Извлекает значение, указанное маской
 * формат.
 *
 * Предоставленная строка формата маски переводится классом Compiler в набор состояний, которые
 * определить форматирование и извлечение значений.
 *
 * @see ```Compiler```, ```State``` и ```CaretString``` классы.
 *
 * @author taflanidi
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
open class Mask(format: String, protected val customNotations: List<Notation>) {

    /**
     * Convenience constructor.
     */
    constructor(format: String) : this(format, emptyList())

    /**
     * ### Result
     *
     * Конечный результат применения маски к строке ввода пользователя.
     */
    data class Result(
        /**
         * Отформатированный текст с обновленной позицией каретки.
         */
        val formattedText: CaretString,
        /**
         * Значение, извлеченное из форматированного текста в соответствии с форматом маски.
         */
        val extractedValue: String,
        /**
         * Расчетное значение абсолютного сходства между форматом маски и вводимым текстом.
         */
        val affinity: Int,
        /**
         * Пользовательский ввод завершен.
         */
        val complete: Boolean
    ) {
        fun reversed() =
            Result(
                this.formattedText.reversed(),
                this.extractedValue.reversed(),
                this.affinity,
                this.complete
            )
    }

    companion object Factory {
        private val cache: MutableMap<String, Mask> = HashMap()

        /**
         * Заводской конструктор.
         *
         * Работает с собственным кешем `` Маска '', в котором инициализированные объекты `` Маски '' хранятся в
         * соответствующий ключ формата:
         * `` `[формат: маска]` ``
         *
         * @returns Ранее кэшированный объект `` Маска '' для запрошенной строки формата. Если это так
         * не существует в кеше, объект создается, кэшируется и возвращается.
         */
        fun getOrCreate(format: String, customNotations: List<Notation>): Mask {
            var cachedMask: Mask? = cache[format]
            if (null == cachedMask) {
                cachedMask = Mask(format, customNotations)
                cache[format] = cachedMask
            }
            return cachedMask
        }

        /**
         * Убедитесь, что ваш формат маски действителен.
         *
         * @param format формат маски.
         * @param customNotations - список настраиваемых правил для компиляции квадратных скобок "` `[]` `` групп символов формата.
         *
         * @returns `` true '', если этот формат в сочетании с пользовательскими обозначениями будет компилироваться в рабочий объект `` Mask ''.
         * В противном случае - false.
         */
        fun isValid(format: String, customNotations: List<Notation>): Boolean {
            return try {
                Mask(format, customNotations)
                true
            } catch (e: Compiler.FormatError) {
                false
            }
        }
    }

    private val initialState: State = Compiler(this.customNotations).compile(format)

    /**
     * Применить маску к строке ввода пользователя.
     *
     * @param text строка ввода пользователем с текущей позицией курсора
     *
     * @returns Форматированный текст с извлеченным значением и отрегулированной позицией курсора.
     */
    open fun apply(text: CaretString): Result {
        val iterator = this.makeIterator(text)

        var affinity = 0
        var extractedValue = ""
        var modifiedString = ""
        var modifiedCaretPosition: Int = text.caretPosition

        var state: State = this.initialState
        val autocompletionStack = AutocompletionStack()

        var insertionAffectsCaret: Boolean = iterator.insertionAffectsCaret()
        var deletionAffectsCaret: Boolean = iterator.deletionAffectsCaret()
        var character: Char? = iterator.next()

        while (null != character) {
            val next: Next? = state.accept(character)
            if (null != next) {
                if (deletionAffectsCaret) autocompletionStack.push(state.autocomplete())
                state = next.state
                modifiedString += next.insert ?: ""
                extractedValue += next.value ?: ""
                if (next.pass) {
                    insertionAffectsCaret = iterator.insertionAffectsCaret()
                    deletionAffectsCaret = iterator.deletionAffectsCaret()
                    character = iterator.next()
                    affinity += 1
                } else {
                    if (insertionAffectsCaret && null != next.insert) {
                        modifiedCaretPosition += 1
                    }
                    affinity -= 1
                }
            } else {
                if (deletionAffectsCaret) {
                    modifiedCaretPosition -= 1
                }
                insertionAffectsCaret = iterator.insertionAffectsCaret()
                deletionAffectsCaret = iterator.deletionAffectsCaret()
                character = iterator.next()
                affinity -= 1
            }
        }

        while (text.caretGravity.autocomplete && insertionAffectsCaret) {
            val next: Next = state.autocomplete() ?: break
            state = next.state
            modifiedString += next.insert ?: ""
            extractedValue += next.value ?: ""
            if (null != next.insert) {
                modifiedCaretPosition += 1
            }
        }

        while (text.caretGravity.autoskip && !autocompletionStack.empty()) {
            val skip: Next = autocompletionStack.pop()
            if (modifiedString.length == modifiedCaretPosition) {
                if (null != skip.insert && skip.insert == modifiedString.last()) {
                    modifiedString = modifiedString.dropLast(1)
                    modifiedCaretPosition -= 1
                }
                if (null != skip.value && skip.value == extractedValue.last()) {
                    extractedValue = extractedValue.dropLast(1)
                }
            } else {
                if (null != skip.insert) {
                    modifiedCaretPosition -= 1
                }
            }
        }

        return Result(
            CaretString(
                modifiedString,
                modifiedCaretPosition,
                text.caretGravity
            ),
            extractedValue,
            affinity,
            this.noMandatoryCharactersLeftAfterState(state)
        )
    }

    open fun makeIterator(text: CaretString) = CaretStringIterator(text)

    /**
     * Создать заполнитель.
     *
     * @return Строка-заполнитель.
     */
    fun placeholder(): String = this.appendPlaceholder(this.initialState, "")

    /**
     * Минимальная длина текста внутри поля для заполнения всех обязательных символов в маске.
     *
     * @return Минимальное удовлетворительное количество символов внутри текстового поля.
     */
    fun acceptableTextLength(): Int {
        var state: State? = this.initialState
        var length = 0

        while (null != state && state !is EOLState) {
            if (state is FixedState || state is FreeState || state is ValueState) {
                length += 1
            }
            state = state.child
        }

        return length
    }

    /**
     * Максимальная длина текста внутри поля.
     *
     * @return Общее доступное количество обязательных и дополнительных символов внутри текстового поля.
     */
    fun totalTextLength(): Int {
        var state: State? = this.initialState
        var length = 0

        while (null != state && state !is EOLState) {
            if (state is FixedState || state is FreeState || state is ValueState || state is OptionalValueState) {
                length += 1
            }
            state = state.child
        }

        return length
    }

    /**
     * Minimal length of the extracted value with all mandatory characters filled.
     *
     * @return Minimal satisfying count of characters in extracted value.
     */
    fun acceptableValueLength(): Int {
        var state: State? = this.initialState
        var length = 0

        while (null != state && state !is EOLState) {
            if (state is FixedState || state is ValueState) {
                length += 1
            }
            state = state.child
        }

        return length
    }

    /**
     * Максимальная длина извлекаемого значения.
     *
     * @return Общее доступное количество обязательных и дополнительных символов для извлеченного значения.
     */
    fun totalValueLength(): Int {
        var state: State? = this.initialState
        var length = 0

        while (null != state && state !is EOLState) {
            if (state is FixedState || state is ValueState || state is OptionalValueState) {
                length += 1
            }
            state = state.child
        }

        return length
    }

    private fun appendPlaceholder(state: State?, placeholder: String): String {
        if (null == state) {
            return placeholder
        }

        if (state is EOLState) {
            return placeholder
        }

        if (state is FixedState) {
            return this.appendPlaceholder(state.child, placeholder + state.ownCharacter)
        }

        if (state is FreeState) {
            return this.appendPlaceholder(state.child, placeholder + state.ownCharacter)
        }

        if (state is OptionalValueState) {
            return when (state.type) {
                is OptionalValueState.StateType.AlphaNumeric -> {
                    this.appendPlaceholder(state.child, placeholder + "-")
                }

                is OptionalValueState.StateType.Literal -> {
                    this.appendPlaceholder(state.child, placeholder + "a")
                }

                is OptionalValueState.StateType.Numeric -> {
                    this.appendPlaceholder(state.child, placeholder + "0")
                }

                is OptionalValueState.StateType.Custom -> {
                    this.appendPlaceholder(state.child, placeholder + state.type.character)
                }
            }
        }

        if (state is ValueState) {
            return when (state.type) {
                is ValueState.StateType.AlphaNumeric -> {
                    this.appendPlaceholder(state.child, placeholder + "-")
                }

                is ValueState.StateType.Literal -> {
                    this.appendPlaceholder(state.child, placeholder + "a")
                }

                is ValueState.StateType.Numeric -> {
                    this.appendPlaceholder(state.child, placeholder + "0")
                }

                is ValueState.StateType.Ellipsis -> placeholder

                is ValueState.StateType.Custom -> {
                    this.appendPlaceholder(state.child, placeholder + state.type.character)
                }
            }
        }

        return placeholder
    }

    private fun noMandatoryCharactersLeftAfterState(state: State): Boolean {
        return when (state) {
            is EOLState -> { true }
            is ValueState -> { return state.isElliptical }
            is FixedState -> { false }
            else -> { this.noMandatoryCharactersLeftAfterState(state.nextState()) }
        }
    }

    /**
     * При сканировании входной строки в методе `.apply (…)` маска строит график
     * шаги автозаполнения.
     *
     * На этом графике накапливаются результаты вызовов `.autocomplete ()` для каждого последовательного `State`,
     * действует как стек экземпляров объекта Next.
     *
     * Каждый раз, когда State возвращает null для своего .autocomplete (), график сбрасывается пустым.
     */
    private class AutocompletionStack : Stack<Next>() {
        override fun push(item: Next?): Next? {
            return if (null != item) {
                super.push(item)
            } else {
                this.removeAllElements()
                null
            }
        }
    }
}
