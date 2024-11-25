package com.snapone.weatherproject.base

/**
 * A functional interface representing a generic mapper for transforming a list of models into a list of views.
 *
 * This interface allows defining transformation logic between two types (`Model` and `View`),
 * used for converting domain models to UI models or vice versa.
 *
 * @param Model The input type (data layer representation).
 * @param View The output type (presentation layer representation).
 */
fun interface ListMapper<in Model : Any, out View : Any> {
    /**
     * Transforms a list of items from type [Model] to a list of items of type [View].
     *
     * @param items The list of input models to be transformed.
     * @return A list of transformed view models.
     */
    fun map(items: List<Model>): List<View>
}


/**
 * A functional interface for merging two models of potentially different types.
 *
 * @param Model1 The type of the first input model. It is contravariant (can accept a subtype).
 * @param Model2 The type of the second input model and the return type. It represents the model that will be updated or merged.
 */
fun interface Merger<in Model1 : Any, Model2 : Any> {

    /**
     * Merges two models and produces an updated version of the second model.
     *
     * @param item1 The first input model of type [Model1]. This is typically used as the source of additional data.
     * @param item2 The second input model of type [Model2]. This is the target that gets updated or modified.
     * @return An updated version of [item2], reflecting the merge with [item1].
     */
    fun merge(item1: Model1, item2: Model2): Model2
}