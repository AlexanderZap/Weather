package ru.zapashnii.weather.domain.repository.base

/** Реализуют репозитории, которые должны быть очищены после использования. */
interface ICleanableRepository {
    fun clear()
}