package ru.zapashnii.weather.utils.inputmask.helper

import java.util.*

/**
 * ### FormatSanitizer
 *
 * Дезинфицирует данный formatString перед компиляцией.
 *
 * @complexity ```O(2*floor(log(n)))```, and switches to ```O(n^2)``` for ```n < 20``` where
 * ```n = formatString.characters.count```
 *
 * @requires Форматная строка, содержащая только плоские группы символов в скобках `` [] `` и `` `{}` ``
 * без вложенных скобок, например «« [[000] 99] ««. Квадратные скобки "` `[]` `` группы могут содержать смешанные
 * типы символов («0» и «9» с «A» и «a» или «_» и «-»), которые дезинфицирующее средство разделит на
 * отдельные группы. Таким образом, группа `` [0000Aa] `` будет разделена на две группы: `` [0000] `` `
 * и «« [Aa] ««.
 *
 * FormatSanitizer используется компилятором перед компиляцией строки формата.
 *
 * @author taflanidi
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
class FormatSanitizer {

    /**
     * Очистите formatString перед компиляцией.
     *
     * Для этого sanitizer разбивает строку на группы обычных символов, символы в квадрате
     * квадратные скобки [] и символы в фигурных скобках {}. Затем символы в квадратных скобках сортируются по
     * способ, которым обязательные символы идут перед необязательными. Например,
     *
     * ```
     * a ([0909]) b
     * ```
     *
     * формат маски изменен на
     *
     * ```
     * a ([0099]) b
     * ```
     *
     * Кроме того, многоточие в квадратных скобках [] всегда ставится в конце.
     *
     * @complexity ```O(2*floor(log(n)))```, and switches to ```O(n^2)``` for ```n < 20``` where
     * ```n = formatString.characters.count```
     *
     * @requires Форматная строка, содержащая только плоские группы символов в скобках `` [] `` и `` `{}` ``
     * без вложенных скобок, например «« [[000] 99] ««. Квадратные скобки "` `[]` `` группы могут содержать смешанные
     * типы символов («0» и «9» с «A» и «a» или «_» и «-»), которые дезинфицирующее средство разделит на
     * отдельные группы. Таким образом, группа `` [0000Aa] `` будет разделена на две группы: `` [0000] `` `
     * и «« [Aa] ««.
     *
     * @param formatString: строка формата маски.
     *
     * @returns Очищенная строка формата.
     *
     * @throws ``FormatError``, если ``formatString`` не соответствует требованиям метода.
     */
    @Throws(Compiler.FormatError::class)
    fun sanitize(formatString: String): String {
        this.checkOpenBraces(formatString)

        val blocks: List<String> =
            this.divideBlocksWithMixedCharacters(this.getFormatBlocks(formatString))

        return this.sortFormatBlocks(blocks).joinToString("")
    }

    private fun getFormatBlocks(formatString: String): List<String> {
        val blocks: MutableList<String> = ArrayList()
        var currentBlock = ""
        var escape = false

        for (char in formatString.toCharArray()) {
            if ('\\' == char) {
                if (!escape) {
                    escape = true
                    currentBlock += char
                    continue
                }
            }

            if (('[' == char || '{' == char) && !escape) {
                if (currentBlock.isNotEmpty()) {
                    blocks.add(currentBlock)
                }
                currentBlock = ""
            }

            currentBlock += char

            if ((']' == char || '}' == char) && !escape) {
                blocks.add(currentBlock)
                currentBlock = ""
            }

            escape = false
        }

        if (!currentBlock.isEmpty()) {
            blocks.add(currentBlock)
        }

        return blocks
    }

    private fun divideBlocksWithMixedCharacters(blocks: List<String>): List<String> {
        val resultingBlocks: MutableList<String> = ArrayList()

        for (block in blocks) {
            if (block.startsWith("[")) {
                var blockBuffer = ""
                for (blockCharacter in block) {
                    if (blockCharacter == '[') {
                        blockBuffer += blockCharacter
                        continue
                    }

                    if (blockCharacter == ']' && !blockBuffer.endsWith("\\")) {
                        blockBuffer += blockCharacter
                        resultingBlocks.add(blockBuffer)
                        break
                    }

                    if (blockCharacter == '0' || blockCharacter == '9') {
                        if (blockBuffer.contains("A")
                            || blockBuffer.contains("a")
                            || blockBuffer.contains("-")
                            || blockBuffer.contains("_")
                        ) {
                            blockBuffer += "]"
                            resultingBlocks.add(blockBuffer)
                            blockBuffer = "[$blockCharacter"
                            continue
                        }
                    }

                    if (blockCharacter == 'A' || blockCharacter == 'a') {
                        if (blockBuffer.contains("0")
                            || blockBuffer.contains("9")
                            || blockBuffer.contains("-")
                            || blockBuffer.contains("_")
                        ) {
                            blockBuffer += "]"
                            resultingBlocks.add(blockBuffer)
                            blockBuffer = "[$blockCharacter"
                            continue
                        }
                    }

                    if (blockCharacter == '-' || blockCharacter == '_') {
                        if (blockBuffer.contains("0")
                            || blockBuffer.contains("9")
                            || blockBuffer.contains("A")
                            || blockBuffer.contains("a")
                        ) {
                            blockBuffer += "]"
                            resultingBlocks.add(blockBuffer)
                            blockBuffer = "[$blockCharacter"
                            continue
                        }
                    }

                    blockBuffer += blockCharacter
                }
            } else {
                resultingBlocks.add(block)
            }

        }

        return resultingBlocks
    }

    private fun sortFormatBlocks(blocks: List<String>): List<String> {
        val sortedBlocks: MutableList<String> = ArrayList()

        for (block in blocks) {
            var sortedBlock: String
            if (block.startsWith("[")) {
                if (block.contains("0") || block.contains("9")) {
                    sortedBlock =
                        "[" + block.replace("[", "").replace("]", "").toCharArray().sorted().joinToString("") + "]"
                } else if (block.contains("a") || block.contains("A")) {
                    sortedBlock =
                        "[" + block.replace("[", "").replace("]", "").toCharArray().sorted().joinToString("") + "]"
                } else {
                    sortedBlock = "[" + block.replace("[", "").replace("]", "").replace("_", "A").replace(
                        "-",
                        "a"
                    ).toCharArray().sorted().joinToString("") + "]"
                    sortedBlock = sortedBlock.replace("A", "_").replace("a", "-")
                }
            } else {
                sortedBlock = block
            }

            sortedBlocks.add(sortedBlock)
        }

        return sortedBlocks
    }

    private fun checkOpenBraces(string: String) {
        var escape = false
        var squareBraceOpen = false
        var curlyBraceOpen = false

        for (char in string.toCharArray()) {
            if ('\\' == char) {
                escape = !escape
                continue
            }

            if ('[' == char) {
                if (squareBraceOpen) {
                    throw Compiler.FormatError()
                }
                squareBraceOpen = true && !escape
            }

            if (']' == char && !escape) {
                squareBraceOpen = false
            }

            if ('{' == char) {
                if (curlyBraceOpen) {
                    throw Compiler.FormatError()
                }
                curlyBraceOpen = true && !escape
            }

            if ('}' == char && !escape) {
                curlyBraceOpen = false
            }

            escape = false
        }
    }

}
