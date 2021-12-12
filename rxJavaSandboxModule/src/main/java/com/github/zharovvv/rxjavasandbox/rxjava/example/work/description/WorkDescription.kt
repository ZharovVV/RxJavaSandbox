package com.github.zharovvv.rxjavasandbox.rxjava.example.work.description

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription

@Suppress("ObjectLiteralToLambda")
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

    fun simpleFlowableWork() {
        /*
        1. Создается объект FlowableCreate, в конструктор которого передается
        FlowableOnSubscribe<T> source и BackpressureStrategy backpressure.
         */
        val flowable: Flowable<Int> = Flowable.create(
            object : FlowableOnSubscribe<Int> {
                override fun subscribe(emitter: FlowableEmitter<Int>) {
                    try {
                        emitter.onNext(1)
                        emitter.onNext(2)
                        emitter.onNext(3)
                        emitter.onNext(4)
                        emitter.onNext(5)
                        emitter.onComplete()
                    } catch (e: Exception) {
                        emitter.onError(e)
                    }
                }
            },
            BackpressureStrategy.DROP
        )
        val flowableSubscriber: FlowableSubscriber<Int> = object : FlowableSubscriber<Int> {
            private lateinit var subscription: Subscription
            override fun onSubscribe(s: Subscription) {
                subscription = s
                s.request(1L)
            }

            override fun onNext(t: Int?) {
                println(t)
                subscription.request(1L)
            }

            override fun onError(t: Throwable?) {
                subscription.cancel()
                println(t?.message)
            }

            override fun onComplete() {
                subscription.cancel()
            }
        }
        /*
        2. у FlowableCreate вызывается метод subscribeActual
        3. В зависимости от типа BackpressureStrategy создается соответствующий эмиттер.
        В нашем случае создается DropAsyncEmitter, в конструктор которого передается Subscriber.
        4. Затем у подписчика вызывается onSubscribe, в который передается BaseEmitter.
        (В нашем случае это DropAsyncEmitter, который также реализует интерфейс Subscription)
        5. В нашей реализации FlowableSubscriber.onSubscribe мы вызываем subscription.request(1L)
        6. У DropAsyncEmitter вызывается метод request, внутри которого в AtomicLong сохраняется значение 1L.
        (на самом деле там под капотом значение прибавляется к текущему, если оно конечно есть; так как нет, то просто устанавливается значение 1L)
        7. Далее вызывается FlowableOnSubscribe source.subscribe(BaseEmitter)
        8. В нашей реализзации FlowableOnSubscribe мы сразу вызываем emitter.onNext(..)
        9. DropAsyncEmitter onNext имеет следующую реализацию:
        @Override
        public final void onNext(T t) {
            if (isCancelled()) {
                return;
            }

            if (t == null) {
                onError(new NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."));
                return;
            }
            //Далее проверяется AtomicLong (сколько элементов запросил подписчик в последний раз)
            if (get() != 0) {   //То есть если бы мы ранее не вызвали  subscription.request(1L), то был бы вызван сразу onOverflow().
                downstream.onNext(t);   //FlowableSubscriber.onNext(t)
                BackpressureHelper.produced(this, 1);   //Уменьшает счетчик на 1
            } else {
                onOverflow();
            }
        }
        10. Так как мы в onNext нашего подписчика каждый раз вызываем subscription.request(1L) (то есть увеличиваем счетчик на 1)
        то подписчик получит все элементы.
        11. У эмиттера вызовется onComplete(), внутри которого вызовется onComplete() уже у самого подписчика.
        12. Все
         */
        flowable.subscribe(flowableSubscriber)
    }

    /**
     * [Observable.subscribeOn] - задает Scheduler, на котором выполняется подписка на Observable,
     * и имеет эффект от создания Observable и вниз по цепочке вызовов RxJava до первого observeOn().
     * Это поведение обусловлено реализацией оператора subscribeOn():
     * - 1. При вызове Observable.subscribeOn() создается объект класса ObservableSubscribeOn,
     * который является наследником Observable и выступает в качестве враппера для оригинального observable.
     * - 2. Когда на observable вызывается метод subscribe(),
     * вызов делегируется в абстрактный метод subscribeActual() класса Observable,
     * который реализован в ObservableSubscribeOn.
     * - 3. В методе subscribeActual() вызывается scheduleDirect() на объекте типа Scheduler,
     * который был передан аргументом в оператор subscribeOn().
     * Параметром метода scheduleDirect() передается Runnable,
     * в котором вызывается source.subscribe(), где source – это оригинальный Observable.
     *
     * Из такой реализации следует, что все что делает subscribeOn() – это создание класса-враппера,
     * который делегирует вызов subscribe() на оригинальный Observable,
     * со сменой треда на переданный шедулер.
     */
    fun subscribeOnWorkExample() {
        val disposable: Disposable = Observable.fromArray(1, 2, 3, 4, 5)
            .subscribeOn(Schedulers.computation())
            //observeOn() – задает Scheduler, на котором выполняются операторы, следующие после observeOn().
            //В Rx-стриме может быть несколько observeOn(), каждый из которых будет менять поток выполнения.
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                val thread = Thread.currentThread()
                println("Current thread: $thread; $t")
            }

    }
}