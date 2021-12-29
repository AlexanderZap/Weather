package ru.zapashnii.weather.domain.mapper

/**
 * Базовый тип преобразователя типов
 *
 * @param Entity    Преобразуемый тип
 * @param Model     Результирующий тип
 */
abstract class BaseMapper<Entity, Model> {

    /**
     * Преобразовать из Entity в Model
     */
    abstract fun map(entity: Entity?): Model?

    /**
     * Преобразовать из Model в Entity
     */
    abstract fun reverseMap(model: Model?): Entity?

    /**
     * Преобразовать из коллекции Entity в коллекцию Model
     */
    fun map(entityList: List<Entity>): List<Model> {
        val modelList = arrayListOf<Model>()
        entityList.forEach { entity ->
            map(entity)?.let { model ->
                modelList.add(model)
            }
        }
        return modelList
    }

    /**
     * Преобразовать из коллекции Model в коллекцию Entity
     */
    fun reverseMap(modelList: List<Model>): List<Entity> {
        val entityList = arrayListOf<Entity>()
        modelList.forEach { model ->
            reverseMap(model)?.let { entity ->
                entityList.add(entity)
            }
        }
        return entityList
    }
}