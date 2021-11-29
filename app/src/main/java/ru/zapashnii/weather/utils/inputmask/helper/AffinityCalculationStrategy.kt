package ru.zapashnii.weather.utils.inputmask.helper

import ru.zapashnii.weather.utils.inputmask.model.CaretString

/**
 * ### AffinityCalculationStrategy
 *
 * Позволяет выбрать другой алгоритм выбора маски в слушателях текстового поля.
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
enum class AffinityCalculationStrategy {
    /**
     * Стратегия по умолчанию.
     *
     * Использует встроенный механизм ```Mask``` для вычисления общего соответствия между текстом и форматом маски.
     *
     * Например:
     * ```
     * format: [00].[00]
     *
     * input1: 1234
     * input2: 12.34
     * input3: 1.234
     *
     * affinity1 = 4 (symbols) - 1 (missed dot)                       = 3
     * affinity2 = 5 (symbols)                                        = 5
     * affinity3 = 5 (symbols) - 1 (superfluous dot) - 1 (missed dot) = 3
     * ```
     */
    WHOLE_STRING,

    /**
     * Находит самый длинный общий префикс между исходным текстом и тем же текстом после применения маски.
     *
     * Например:
     * ```
     * format1: +7 [000] [000]
     * format2: 8 [000] [000]
     *
     * input: +7 12 345
     * affinity1 = 5
     * affinity2 = 0
     *
     * input: 8 12 345
     * affinity1 = 0
     * affinity2 = 4
     * ```
     */
    PREFIX,

    /**
     * Сходство - это допуск между длиной ввода и общим объемом текста, который может вместить текущая маска.
     *
     * Если текущая маска не может вместить весь текст, сродство равно ``Int.min``.
     *
     * Например:
     * ```
     * format1: [00]-[0]
     * format2: [00]-[000]
     * format3: [00]-[00000]
     *
     * input          affinity1          affinity2    affinity3
     * 1              -3                 -5           -7
     * 12             -2                 -4           -6
     * 123            -1                 -3           -5
     * 12-3           0                  -2           -4
     * 1234           0                  -2           -4
     * 12345          Int.MIN_VALUE      -1           -3
     * 123456         Int.MIN_VALUE      0            -2
     * ```
     *
     * Эта стратегия вычисления сродства пригодится, когда формат маски радикально меняется в зависимости от входной длины.
     *
     * Примечание: убедитесь, что самый широкий формат маски является основным форматом маски.
     */
    CAPACITY,

    /**
     * Сходство - это допуск между длиной извлеченного значения и общей длиной извлеченного значения, которую может вместить текущая маска.
     *
     * Если текущая маска не может вместить весь текст, сродство равно `Int.min`.
     *
     * Например:
     * ```
     * format1: [00]-[0]
     * format2: [00]-[000]
     * format3: [00]-[00000]
     *
     * input          affinity1          affinity2          affinity3
     * 1              -2                 -4                 -6
     * 12             -1                 -3                 -5
     * 123            0                  -2                 -4
     * 12-3           0                  -2                 -4
     * 1234           Int.MIN_VALUE      -1                 -3
     * 12345          Int.MIN_VALUE      0                  -2
     * 123456         Int.MIN_VALUE      Int.MIN_VALUE      -1
     * ```
     *
     * Эта стратегия вычисления сродства пригодится, когда формат маски радикально меняется в зависимости от длины значения.
     *
     * Примечание: убедитесь, что самый широкий формат маски является основным форматом маски.
     */
    EXTRACTED_VALUE_CAPACITY;

    fun calculateAffinityOfMask(mask: Mask, text: CaretString): Int {
        return when (this) {
            WHOLE_STRING -> mask.apply(text).affinity

            PREFIX -> mask.apply(
                text
            ).formattedText.string.prefixIntersection(text.string).length

            CAPACITY -> if (text.string.length > mask.totalTextLength()) {
                Int.MIN_VALUE
            } else {
                text.string.length - mask.totalTextLength()
            }

            EXTRACTED_VALUE_CAPACITY -> {
                val extractedValueLength: Int = mask.apply(text).extractedValue.length
                if (extractedValueLength > mask.totalValueLength()) {
                    Int.MIN_VALUE
                } else {
                    extractedValueLength - mask.totalValueLength()
                }
            }
        }
    }

    private fun String.prefixIntersection(another: String): String {
        if (this.isEmpty() || another.isEmpty()) return ""

        var endIndex = 0
        while (endIndex < this.length && endIndex < another.length) {
            if (this[endIndex] == another[endIndex]) {
                endIndex += 1
            } else {
                return this.substring(0, endIndex)
            }
        }

        return this.substring(0, endIndex)
    }
}