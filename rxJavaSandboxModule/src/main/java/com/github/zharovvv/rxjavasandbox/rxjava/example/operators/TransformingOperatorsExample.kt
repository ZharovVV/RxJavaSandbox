package com.github.zharovvv.rxjavasandbox.rxjava.example.operators

import io.reactivex.Observable

class TransformingOperatorsExample {

    /**
     * [](https://raw.github.com/wiki/ReactiveX/RxJava/images/rx-operators/flatMap.png)
     *
     * [](https://raw.github.com/wiki/ReactiveX/RxJava/images/rx-operators/flatMapDelayError.o.png)
     *
     * [](https://raw.github.com/wiki/ReactiveX/RxJava/images/rx-operators/flatMapMaxConcurrency.o.png)
     *
     * [flatMap] - Возвращает Observable, который генерирует элементы на основе применения функции,
     * которую вы предоставляете к каждому элементу, испускаемому источником ObservableSource,
     * где эта функция возвращает ObservableSource,
     * а затем объединяет полученные ObservableSources и генерирует результаты этого слияния.
     *
     * Порядок не гарантируется.
     *
     * Разделяет исходный Observable на несколько промежуточных Observables,
     * назовем их [A, B, C], и затем соединяет результат в один Observable.
     * Элементы промежуточных Observables передаются напрямую в конечный Observable
     * без гарантии сохранения порядка, в котором созданы A, B и C.
     * Конечный результат может быть таким: [C1, A1, A2, B1, C2, A3, B2, B3, C3].
     * Полезен если порядок выполнения не важен, но нужно единовременное выполнение.
     */
    fun flatMapExample() {
        val observable: Observable<Char> = Observable.just("str1", "str2")
            .flatMap(
                { string: String ->
                    val charArray = string.toCharArray().toTypedArray()
                    Observable.fromArray(*charArray)
                },
                false, //delay Errors
                // - если true, исключения из текущего Observable и всех внутренних ObservableSources
                // задерживаются до тех пор, пока все они не завершатся, если false,
                // первый сигнализирующий об исключении немедленно завершит всю последовательность
                Int.MAX_VALUE,  //maxConcurrency
                // - максимальное количество ObservableSources, на которые можно подписаться одновременно
                128 //bufferSize
                //количество элементов для предварительной выборки из каждого внутреннего ObservableSource
            )
        val disposable = observable.subscribe { print(it) }
    }

    /**
     * Технически данный оператор выдает похожий с [flatMap] результат,
     * меняется только последовательность эмиттируемых данных.
     * [Observable.concatMap] поддерживает порядок эмиссии данных и ожидает исполнения текущего Observable.
     * Поэтому лучше использовать его когда необходимо обеспечить порядок выполнения задач.
     * При этом нужно помнить, что выполнение [Observable.concatMap] занимает больше времени чем [flatMap].
     */
    fun concatMapExample() {

    }

    /**
     * [Observable.switchMap] похож на [flatMap] и также как [Observable.concatMap] сохраняет порядок.
     * Но при использовании [Observable.switchMap] каждый предыдущий промежуточный Observable останавливается в тот момент,
     * когда стартует следующий.
     */
    fun switchMapExample() {

    }
}