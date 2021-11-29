package ru.zapashnii.weather.utils.inputmask.helper

import ru.zapashnii.weather.utils.inputmask.model.CaretString
import ru.zapashnii.weather.utils.inputmask.model.Notation
import java.util.HashMap

/**
 * ### RTLMask
 *
 * Подкласс Mask с написанием справа налево. Применяет формат с конца строки.
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
class RTLMask(format: String, customNotations: List<Notation>) : Mask(reversedFormat(format), customNotations) {
    companion object Factory {
        private val cache: MutableMap<String, RTLMask> = HashMap()

        fun getOrCreate(format: String, customNotations: List<Notation>): RTLMask {
            var cachedMask: RTLMask? = cache[reversedFormat(format)]
            if (null == cachedMask) {
                cachedMask = RTLMask(format, customNotations)
                cache[reversedFormat(format)] = cachedMask
            }
            return cachedMask
        }
    }

    override fun apply(text: CaretString): Result {
        return super.apply(text.reversed()).reversed()
    }

    override fun makeIterator(text: CaretString): CaretStringIterator {
        return RTLCaretStringIterator(text)
    }
}

private fun reversedFormat(format: String) =
    format
        .reversed()
        .replace("[\\", "\\]")
        .replace("]\\", "\\[")
        .replace("{\\", "\\}")
        .replace("}\\", "\\{")
        .map {
            when (it) {
                '[' -> ']'
                ']' -> '['
                '{' -> '}'
                '}' -> '{'
                else -> it
            }
        }.joinToString("")
