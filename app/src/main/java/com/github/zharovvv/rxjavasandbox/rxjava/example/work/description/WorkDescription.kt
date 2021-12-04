package com.github.zharovvv.rxjavasandbox.rxjava.example.work.description

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class WorkDescription {

    /**
     * ВНИМАНИЕ!
     * При вызове через метод subscribe, не возвращающий Disposable, ответственность за удаление
     * подписки при onError и onComplete несет разработчик.
     */
    fun simpleObservableWork() {
        /*
        При вызове Observable.fromArray:
        1. Создается объект ObservableFromArray<Int>
        2. В конструктор этого объекта передается Int[] array и кладется в поле array.
         */
        val observable: Observable<Int> = Observable.fromArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val observer: Observer<Int> = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Int) {}

            override fun onError(e: Throwable) {}

            override fun onComplete() {}
        }
        /*
        Далее при вызове subscribe происходит следующее:
        3. Вызывается RxJavaPlugins.onSubscribe, проверяется наличие преобразующей observer
        функции BiFunction<? super Observable, ? super Observer, ? extends Observer> onObservableSubscribe.
        Если функция есть, она применяется и преобразует observer.
        4. Далее вызывается метод subscribeActual(Observer<? super T> observer), который каждый тип
        Observable реализуют по-своему.
        В данном случае вызывается ObservableFromArray.subscribeActual(...).
        - Внутри метода subscribeActual:
        5. Создается объект FromArrayDisposable<Int>, в конструктор которого передается observer
        и сохраненный при создании Observable array.
        6. Вызывается observer.onSubscribe(FromArrayDisposable<Int>)
        7. Затем вызывается FromArrayDisposable.run(), внутри которого осуществляется обход array
        в цикле. На каждой итерации вызывается observer.onNext( i ). Затем вызывается
        observer.onComplete().
         */
        observable.subscribe(
            observer
        )   //Данный метод возвращает Unit, однако есть перегруженные варианты, где передаются
        // только соответствующие Consumer-ы (для onComplete используется Action). В этих методах
        // создается LambdaObserver, который принимает эти лямбды в конструкторе,
        // а также он является Disposable. При таком варианте вызова метод subscribe будет возвращать
        // Disposable (а под капотом будет возвращать LambdaObserver).

        // Если говорить немного подробнее про LambdaObserver
        // Он сохраняет Disposable, переданный в onSubscribe в AtomicReference<Disposable>.
        // При вызове у LambdaObserver.dispose из AtomicReference<Disposable> достается
        // исходный Disposable, который сразу диспоузится.
    }
}