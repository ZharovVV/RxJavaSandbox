package com.github.zharovvv.rxjavasandbox.rxjava.example.operators

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

@Suppress("ObjectLiteralToLambda")
class CreateObservablesExample {

    /**
     * Создание Observable из Emitter
     */
    fun createFromEmitter() {
        val observable = Observable.create<Int>(
            //В метод create<T> передается объект, реализующий интерфейс ObservableOnSubscribe<T>.
            //Интерфейс ObservableOnSubscribe имеет один метод
            // void subscribe(@NonNull ObservableEmitter<T> emitter) throws Exception;
            // т.е. ObservableOnSubscribe это Consumer ObservableEmitter-а.
            object : ObservableOnSubscribe<Int> {
                override fun subscribe(emitter: ObservableEmitter<Int>) {
                    //ObservableEmitter<T> : Emitter<T>
                    // интерфейс Emitter имеет 3 метода:
                    // void onNext(@NonNull T value);
                    // void onError(@NonNull Throwable error);
                    // void onComplete();
                    try {
                        emitter.onNext(1)
                        emitter.onNext(2)
                        emitter.onNext(3)
                        emitter.onNext(4)
                    } catch (e: Exception) {
                        emitter.onError(e)
                    }
                    emitter.onComplete()
                }
            }
        )
        /*
        Под капотом здесь происходит следующее:
        1. Создается объект public final class ObservableCreate<T> extends Observable<T>
        2. Передаваемый в create объект ObservableOnSubscribe передается в конструктор объекта
        ObservableCreate (и сохраняется в поле source)
        3. Это все. Но стоит отметить что данные действия происходят через вызов RxJavaPlugins.onAssembly,
        который сначала проверяет что есть функция
        Function<? super Observable, ? extends Observable> RxJavaPlugins.onObservableAssembly и если она есть,
        то применяет её. Задать преобразующую функцию, которая будет применятся при создании
        всех Observables можно методом RxJavaPlugins.setOnObservableAssembly(...).
        Предотвратить вызов этого метода можно, вызвав RxJavaPlugins.lockdown().
         */
    }

    /**
     * Создание Observable через fromArray.
     */
    fun createByFromArray() {
        // Если элементов нет, то вызывается Observable.empty
        // Если элемент 1, то вызывается Observable.just(items[0])
        // Если элементов > 1, то происходит следующее:
        val observable = Observable.fromArray(1, 2, 3, 4, 5)
        /*
        1. Создается объект ObservableFromArray<T>
        2. В конструктор этого объекта передается T[] array и кладется в поле array.
        3. Пункт аналогичен пункту 3 для createFromEmitter.
         */
    }

    /**
     * Создание Observable через just.
     */
    fun createByJust() {
        //Оператор just имеет перегруженные версии. Если элементов > 1, то под капотом будет
        // вызываться Observable.fromArray.
        // Если элементов == 1, то происходит следующее:
        val observable = Observable.just(1)
        /*
        1. Создается объект ObservableJust<T>
        2. В конструктор этого объекта передается значение T и сохраняется в поле value.
        3. Пункт аналогичен пункту 3 для createFromEmitter.
         */
    }

}