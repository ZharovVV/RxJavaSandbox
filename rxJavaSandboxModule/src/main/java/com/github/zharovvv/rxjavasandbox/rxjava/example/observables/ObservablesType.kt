package com.github.zharovvv.rxjavasandbox.rxjava.example.observables

import com.github.zharovvv.rxjavasandbox.rxjava.example.operators.CreateObservablesExample
import io.reactivex.*
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription

class ObservablesType {

    /**
     * См. [CreateObservablesExample.createFromEmitter]
     * Observable push-ит элементы в Observer-а
     */
    fun observable() {
        //Ну тут все понятно
    }

    /**
     * Single ведет себя аналогично Observable, за исключением того,
     * что может выдавать только одно успешное значение или ошибку
     * (нет уведомления onComplete, как для Observable).
     */
    fun single() {
        val single: Single<Int> = Single.just(3)
        val singleObserver: SingleObserver<Int> = object : SingleObserver<Int> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(t: Int) {
            }

            override fun onError(e: Throwable) {
            }
        }
        single.subscribe(singleObserver)
    }

    /**
     * Класс Maybe предоставляет отложенное вычисление и выдачу одного значения (onSuccess),
     * отсутствие значения (onComplete) или исключения (onError).
     * Класс Maybe реализует базовый интерфейс MaybeSource, а тип потребителя по умолчанию,
     * с которым он взаимодействует, - это MaybeObserver через метод subscribe (MaybeObserver).
     * Maybe работает по следующему последовательному протоколу:
     * onSubscribe (onSuccess | onError | onComplete)?
     *
     * Обратите внимание, что onSuccess, onError и onComplete являются взаимоисключающими событиями;
     * в отличие от Observable, за onSuccess никогда не следует onError или onComplete.
     * Как и в случае с Observable, выполнение Maybe можно остановить с помощью экземпляра Disposable,
     * предоставляемого потребителям через MaybeObserver.onSubscribe.
     */
    fun maybe() {
        val maybe: Maybe<Int> = Maybe.just(3)
        val maybeObserver: MaybeObserver<Int> = object : MaybeObserver<Int> {
            private lateinit var disposable: Disposable
            override fun onSubscribe(d: Disposable) {
                disposable = d
            }

            override fun onSuccess(t: Int) {
                disposable.dispose()
            }

            override fun onError(e: Throwable) {
                disposable.dispose()
            }

            override fun onComplete() {
                disposable.dispose()
            }
        }
        maybe.subscribe(maybeObserver)
    }

    /**
     * Класс Completable предоставляет отложенное вычисление без какого-либо возвращаемого значения,
     * а только указание на завершение или исключение.
     * Completable ведет себя аналогично Observable, за исключением того,
     * что может выдавать только сигнал завершения или ошибки
     * (нет onNext или onSuccess, как у других реактивных типов).
     * Класс Completable реализует базовый интерфейс CompletableSource,
     * а тип потребителя по умолчанию, с которым он взаимодействует,
     * - это CompletableObserver через метод subscribe (CompletableObserver).
     * Completable работает по следующему последовательному протоколу:
     * onSubscribe (onError | onComplete)?
     *
     * Обратите внимание, что, как и в случае с протоколом Observable, onError и onComplete
     * являются взаимоисключающими событиями.
     * Как и Observable, работающий Completable можно остановить с помощью экземпляра Disposable,
     * предоставленного потребителям через SingleObserver.onSubscribe.
     */
    fun completable() {
        val completable: Completable = Completable.fromCallable { println("some Task!") }
        val completableObserver: CompletableObserver = object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onComplete() {
            }

            override fun onError(e: Throwable) {
            }
        }
        completable.subscribe(completableObserver)
    }

    /**
     * В отличие от Observable (который реализует интерфейс ObservableSource с методом
     *     void subscribe(@NonNull Observer<? super T> observer)),
     * Flowable реализует интерфейс Publisher с методом
     *     public void subscribe(Subscriber<? super T> s);
     * TODO Более подробное описание + сравнение с Observable.
     * FlowableSubscriber тянет (pull) элементы из Flowable.
     */
    fun flowable() {
        val flowable: Flowable<Int> = Flowable.fromArray(1, 2, 3, 4, 5)
        val flowableSubscriber: FlowableSubscriber<Int> = object : FlowableSubscriber<Int> {
            private lateinit var subscription: Subscription
            override fun onSubscribe(s: Subscription) {
                subscription = s
                //Издатель не отправляет никаких событий, пока через этот метод не поступит сигнал о потребности.
                //Его можно вызывать сколько угодно часто и всякий раз, когда это необходимо,
                //но непогашенный совокупный спрос никогда не должен превышать Long.MAX_VALUE.
                //Непогашенный совокупный спрос Long.MAX_VALUE может рассматриваться
                //Издателем как «фактически неограниченный».
                //Все, что было запрошено, может быть отправлено издателем,
                //поэтому только сигнализирует о спросе на то, что может быть безопасно обработано.
                //Издатель может отправить меньше, чем запрошено, если поток заканчивается,
                //но затем должен выдать либо Subscriber.onError (Throwable), либо Subscriber.onComplete().
                //Параметры:
                //n - строго положительное количество элементов для запросов к вышестоящему издателю
//                s.request(Long.MAX_VALUE)   //Без вызова Subscription.request подписчик не начнет получать элементы.
                s.request(1)
            }

            override fun onNext(t: Int?) {
                //handle onNext
                subscription.request(1)
            }

            override fun onError(t: Throwable?) {
                subscription.cancel()
            }

            override fun onComplete() {
                subscription.cancel()
            }
        }
        flowable.subscribe(flowableSubscriber)
        //Если вызвать вариант метода subscribe с консьюмерами и без консьюмера onSubscribe
        //то в качестве консьюмера onSubscribe будет передан объект:
        //public enum RequestMax implements Consumer<Subscription> {
        //        INSTANCE;
        //        @Override
        //        public void accept(Subscription t) throws Exception {
        //            t.request(Long.MAX_VALUE);
        //        }
        //    }
    }
}