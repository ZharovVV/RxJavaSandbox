package com.github.zharovvv.rxjavasandbox.rxjava.example.operators

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

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
     * Создание Observable через fromCallable
     * Также есть другие операторы группы from:
     * [Observable.fromFuture], [Observable.fromIterable], [Observable.fromPublisher].
     */
    fun createByFromCallable() {
        fun longOperation(): Int {
            try {
                Thread.sleep(1000L)
                return 1
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw e
            }
        }

        /*
        Все аналогично.
        Создается объект ObservableFromCallable<T> в его поле callable сохраняется Callable<T>.
         */
        val observable = Observable.fromCallable(
            object : Callable<Int> {
                override fun call(): Int {
                    return longOperation()
                }
            }
        )
    }

    /**
     * Операторы Empty, Never и Throw (В контексте RxJava это оператор error) генерируют Observables
     * с очень специфическим и ограниченным поведением. Они полезны для целей тестирования,
     * а иногда также для объединения с другими наблюдаемыми объектами
     * или в качестве параметров для операторов,
     * которые ожидают другие наблюдаемые объекты в качестве параметров.
     */
    fun createByEmptyNeverThrow() {
        //После подписки сразу вызывает у наблюдателя onComplete
        val emptyObservable = Observable.empty<Int>()
        //Создает Observable, который не испускает никаких элементов и не завершается
        val neverObservable = Observable.never<Int>()
        //Создает Observable, который не испускает никаких элементов и завершается с ошибкой
        val throwObservable = Observable.error<Int>(IllegalArgumentException())
    }

    /**
     * Создает Observable<Long>, который будет испускать последовательность Long, начиная с 0L,
     * через заданые промежутки времени.
     */
    fun createByInterval() {
        val initialDelay =
            1000L //начальное время задержки для ожидания перед выдачей первого значения 0L
        val period = 1500L
        val unit = TimeUnit.MILLISECONDS
        val scheduler = // Планировщик, на котором происходит ожидание и отправляются элементы
            Schedulers.computation()    //По умолчанию используется именно этот Scheduler.
        val observable: Observable<Long> = Observable.interval(
            initialDelay, period, unit, scheduler
        )
    }

    /**
     * Создает Observable<Int> начиная от n элемента до n + m - 1,
     * где n - начальный элемент, m - общее количество элементов.
     */
    fun createByRange() {
        val n = 3
        val count = 100
        val observable: Observable<Int> = Observable.range(n, count)    //В данном случае
        // сгенерируется последовательность от 3 до 102.
    }

    /**
     * Возвращает Observable, который повторяет последовательность элементов,
     * отправленных источником ObservableSource, заданное количество раз.
     */
    fun createByRepeat() {
        val sourceObservable: Observable<Int> = Observable.range(0, 10)
        val repeatObservable = sourceObservable.repeat()    //Под капотом repeat(Long.MAX_VALUE)
    }

    /**
     * Возвращает Observable<Long>, который излучает 0L после указанной задержки
     * в указанном планировщике, а затем завершается.
     */
    fun createByTimer() {
        val timerObservable: Observable<Long> =
            Observable.timer(1000L, TimeUnit.MILLISECONDS, Schedulers.computation())
    }

    /**
     * Позволяет отложить создание Observable до тех пор, пока на него него не подпишутся.
     * То есть в момент подписки будет создаваться новый (актуальный) инстанс Observable.
     * Пример: [https://blog.danlew.net/2015/07/23/deferring-observable-code-until-subscription-in-rxjava/]
     */
    fun createByDefer() {
        //В примере ниже каждый раз при новой подписке будет создаваться новый Observable
        //как следствие каждому новому подписчику будет приходить уникальное число.
        val deferObservable: Observable<Int> = Observable.defer(
            object : Callable<ObservableSource<Int>> {
                override fun call(): ObservableSource<Int> {
                    return Observable.just((Math.random() * 10).toInt())
                }
            }
        )
        //В примере ниже всем подписчикам придет одно и то же число.
        val simpleObservable: Observable<Int> = Observable.just((Math.random() * 10).toInt())
    }
}