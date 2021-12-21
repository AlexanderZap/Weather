package ru.zapashnii.weather.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream

/** Утилита для работы с файлом: создание и запись */
object FileUtil {

    /**
     * Создать файл
     *
     * @param file      создаваемый файл.
     * @return          созданный файл или null
     */
    private fun createFileOrNull(file: File): File? {
        val parent = file.parent
        val name = file.name

        return if (!parent.isNullOrBlank() && name.isNotBlank()) {
            createFileOrNull(parent, name)
        } else {
            Logger.logError(
                tag = this.javaClass.name,
                message = "Unable to create file $parent/$name"
            )
            null
        }
    }

    /**
     * Создать файл
     *
     * @param directoryPath      директория(место) где создается файл
     * @param fileName           имя файла
     * @return                   созданный файл или null
     */
    private fun createFileOrNull(directoryPath: String, fileName: String): File? {
        return try {
            createFile(directoryPath, fileName)
        } catch (ex: IOException) {
            Logger.logError(tag = this.javaClass.name, message = "$ex")
            null
        }
    }

    /**
     * Создать файл
     *
     * @param directoryPath     директория(место) где создается файл
     * @param fileName          имя файла
     * @return                  созданный файл
     */
    @Throws(IOException::class)
    fun createFile(directoryPath: String, fileName: String): File {
        val isDirectoryPathCorrect = directoryPath.isNotBlank()
        val isFileNameCorrect = fileName.isNotBlank()

        return if (isDirectoryPathCorrect && isFileNameCorrect) {
            val directory = File(directoryPath)
            directory.mkdirs()

            val file = File(directory, fileName)
            file.createNewFile()

            file
        } else {
            Logger.logError(
                tag = this.javaClass.name,
                message = "Unable to create file $directoryPath/$fileName. Directory path or fileName is incorrect"
            )
            throw IOException("Unable to create file $directoryPath/$fileName. Directory path or fileName is incorrect")
        }
    }

    /**
     * Записать [InputStream] в файл
     *
     * @param file          создаваемый файл. Пример: File("${statementTempDirectoryPath}$name" statementTempDirectoryPath = "${AppContext.instance.cacheDir}/statements/" Путь сохранения файла
     * @param content       последовательность байтов [InputStream]
     * @return              созданный файл
     */
    suspend fun writeToFile(file: File?, content: InputStream): File? {
        val fileToWrite = file?.let { createFileOrNull(it) }

        return if (fileToWrite?.exists() == true && fileToWrite.canWrite()) {
            try {
                withContext(Dispatchers.IO) {
                    file.outputStream().use { outputStream -> content.copyTo(outputStream) }
                    file
                }
            } catch (ex: Exception) {
                Logger.logError(tag = this.javaClass.name, message = "$ex")
                null
            }
        } else {
            Logger.logError(
                tag = this.javaClass.name,
                message = "Unable to write file $fileToWrite, isFileExist = ${fileToWrite?.exists()}, canWriteToFile = ${fileToWrite?.canWrite()}"
            )
            null
        }
    }
}