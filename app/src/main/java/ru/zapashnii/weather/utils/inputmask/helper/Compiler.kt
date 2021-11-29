package ru.zapashnii.weather.utils.inputmask.helper

import ru.zapashnii.weather.utils.inputmask.model.Notation
import ru.zapashnii.weather.utils.inputmask.model.State
import ru.zapashnii.weather.utils.inputmask.model.state.*

/**
 * ### Compiler
 *
 * Creates a sequence of states from the mask format string.
 * @see ```State``` class.
 *
 * @complexity ```O(formatString.characters.count)``` plus ```FormatSanitizer``` complexity.
 *
 * @requires Строка формата, содержащая только плоские группы символов в квадратных скобках `` [] `` и `` `{}` `
 * без вложенных скобок, например «« [[000] 99] ««. Кроме того, группы «« […] «« могут содержать только
 * указанные символы («0», «9», «A», «a», «…», «_» и «-»). Квадратная скобка "` `[]` `` группы не могут
 * содержат символы смешанного типа («0» и «9» с «A» и «a» или «_» и «-»).
 *
 * Объект Compiler инициализируется, а Compiler.compile (formatString:) вызывается во время инициализации экземпляра Mask.
 *
 * Compiler использует FormatSanitizer для подготовки formatString к компиляции.
 *
 * @author taflanidi
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
class Compiler(
    /**
     * Список настраиваемых правил для составления квадратных скобок «` `[]` `` групп символов формата.
     *
     * @see ```Notation``` class.
     */
    private val customNotations: List<Notation>
) {

    /**
     * ### FormatError
     *
     * Тип исключения ошибки компилятора, возникающий, когда formatString содержит несоответствующие последовательности символов.
     *
     * ```FormatError``` используется классами ```Compiler``` и ```FormatSanitizer```.
     */
    class FormatError : Exception()

    /**
     * Compile ```formatString``` into the sequence of states.
     *
     * * Свободные символы из formatString преобразуются в FreeState-s.
     * * Символы в квадратных скобках преобразуются в значения ValueState и OptionalValueState.
     * * Символы в фигурных скобках преобразуются в `` FixedState``-ы.
     * * Конец строки formatString образует EOLState.
     *
     * Например,
     *
     * ```
     * [09]{.}[09]{.}19[00]
     * ```
     *
     * преобразуется в последовательность:
     *
     * ```
     * 0. ValueState.Numeric          [0]
     * 1. OptionalValueState.Numeric  [9]
     * 2. FixedState                  {.}
     * 3. ValueState.Numeric          [0]
     * 4. OptionalValueState.Numeric  [9]
     * 5. FixedState                  {.}
     * 6. FreeState                    1
     * 7. FreeState                    9
     * 8. ValueState.Numeric          [0]
     * 9. ValueState.Numeric          [0]
     * ```
     *
     * @param formatString строка с форматом маски.
     *
     * @see ```State``` class.
     *
     * @complexity `` O (formatString.characters.count) `` `` плюс `` FormatSanitizer`` сложность.
     *
     * @requires: Форматирующая строка, содержащая только плоские группы символов в квадратных скобках `` `[]` `и` `` {} ``
     * без вложенных скобок, например «« [[000] 99] ««. Кроме того, группы «« […] «« могут содержать только
     * указанные символы («0», «9», «A», «a», «…», «_» и «-»).
     *
     * @returns Инициализированный объект State с присвоенной цепочкой State.child.
     *
     * @throws `` FormatError``, если `` formatString`` не соответствует требованиям метода.
     */
    @Throws(FormatError::class)
    fun compile(formatString: String): State {
        val sanitizedString: String = FormatSanitizer().sanitize(formatString)

        return this.compile(
            sanitizedString,
            false,
            false,
            null
        )
    }

    private fun compile(formatString: String, valuable: Boolean, fixed: Boolean, lastCharacter: Char?): State {
        if (formatString.isEmpty()) {
            return EOLState()
        }

        val char: Char = formatString.first()

        when (char) {
            '[' -> {
                if ('\\' != lastCharacter) {
                    return this.compile(
                        formatString.drop(1),
                        true,
                        false,
                        char
                    )
                }
            }

            '{' -> {
                if ('\\' != lastCharacter) {
                    return this.compile(
                        formatString.drop(1),
                        false,
                        true,
                        char
                    )
                }
            }

            ']' -> {
                if ('\\' != lastCharacter) {
                    return this.compile(
                        formatString.drop(1),
                        false,
                        false,
                        char
                    )
                }
            }

            '}' -> {
                if ('\\' != lastCharacter) {
                    return this.compile(
                        formatString.drop(1),
                        false,
                        false,
                        char
                    )
                }
            }

            '\\' -> {
                if ('\\' != lastCharacter) {
                    return this.compile(
                        formatString.drop(1),
                        valuable,
                        fixed,
                        char
                    )
                }
            }
        }

        if (valuable) {
            when (char) {
                '0' -> {
                    return ValueState(
                        this.compile(
                            formatString.drop(1),
                            true,
                            false,
                            char
                        ),
                        ValueState.StateType.Numeric()
                    )
                }

                'A' -> {
                    return ValueState(
                        this.compile(
                            formatString.drop(1),
                            true,
                            false,
                            char
                        ),
                        ValueState.StateType.Literal()
                    )
                }

                '_' -> {
                    return ValueState(
                        this.compile(
                            formatString.drop(1),
                            true,
                            false,
                            char
                        ),
                        ValueState.StateType.AlphaNumeric()
                    )
                }

                '…' -> {
                    return ValueState(determineInheritedType(lastCharacter))
                }

                '9' -> {
                    return OptionalValueState(
                        this.compile(
                            formatString.drop(1),
                            true,
                            false,
                            char
                        ),
                        OptionalValueState.StateType.Numeric()
                    )
                }

                'a' -> {
                    return OptionalValueState(
                        this.compile(
                            formatString.drop(1),
                            true,
                            false,
                            char
                        ),
                        OptionalValueState.StateType.Literal()
                    )
                }

                '-' -> {
                    return OptionalValueState(
                        this.compile(
                            formatString.drop(1),
                            true,
                            false,
                            char
                        ),
                        OptionalValueState.StateType.AlphaNumeric()
                    )
                }

                else -> return compileWithCustomNotations(char, formatString)
            }
        }

        if (fixed) {
            return FixedState(
                this.compile(
                    formatString.drop(1),
                    false,
                    true,
                    char
                ),
                char
            )
        }

        return FreeState(
            this.compile(
                formatString.drop(1),
                false,
                false,
                char
            ),
            char
        )
    }

    private fun determineInheritedType(lastCharacter: Char?): ValueState.StateType {
        return when (lastCharacter) {
            '0', '9' -> ValueState.StateType.Numeric()
            'A', 'a' -> ValueState.StateType.Literal()
            '_', '-' -> ValueState.StateType.AlphaNumeric()
            '…' -> ValueState.StateType.AlphaNumeric()
            '[' -> ValueState.StateType.AlphaNumeric()
            else -> determineTypeWithCustomNotations(lastCharacter)
        }
    }

    private fun compileWithCustomNotations(char: Char, string: String): State {
        for (customNotation in this.customNotations) {
            if (customNotation.character == char) {
                return if (customNotation.isOptional) {
                    OptionalValueState(
                        this.compile(
                            string.drop(1),
                            true,
                            false,
                            char
                        ),
                        OptionalValueState.StateType.Custom(char, customNotation.characterSet)
                    )
                } else {
                    ValueState(
                        this.compile(
                            string.drop(1),
                            true,
                            false,
                            char
                        ),
                        ValueState.StateType.Custom(char, customNotation.characterSet)
                    )
                }
            }
        }
        throw FormatError()
    }

    private fun determineTypeWithCustomNotations(lastCharacter: Char?): ValueState.StateType {
        customNotations.forEach { notation: Notation ->
            if (notation.character == lastCharacter) return ValueState.StateType.Custom(
                lastCharacter,
                notation.characterSet
            )
        }
        throw FormatError()
    }

}
