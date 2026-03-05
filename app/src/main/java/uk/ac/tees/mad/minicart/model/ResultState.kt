package uk.ac.tees.mad.minicart.model

sealed class ResultState<out T> {
    data class Succes<T>(val data: T) : ResultState<T>()
    data class error<T>(val message: String) : ResultState<T>()
    data object Loading : ResultState<Nothing>()

}