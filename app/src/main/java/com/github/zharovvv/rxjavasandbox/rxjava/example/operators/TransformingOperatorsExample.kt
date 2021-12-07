package com.github.zharovvv.rxjavasandbox.rxjava.example.operators

import io.reactivex.Observable

class TransformingOperatorsExample {

    /**
     * [flatMap] - Возвращает Observable, который генерирует элементы на основе применения функции,
     * которую вы предоставляете к каждому элементу, испускаемому источником ObservableSource,
     * где эта функция возвращает ObservableSource,
     * а затем объединяет полученные ObservableSources и генерирует результаты этого слияния.
     */
    fun flatMapExample() {
        val observable: Observable<Char> = Observable.just("str1", "str2")
            .flatMap { string: String ->
                val charArray = string.toCharArray().toTypedArray()
                Observable.fromArray(*charArray)
            }
    }
}