package com.github.zharovvv.rxjavasandbox.rxjava.example.operators

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe

@Suppress("ObjectLiteralToLambda")
class CreateFlowableExample {

    /**
     * Создание Flowable из Emitter.
     */
    fun createFlowableFromEmitter() {
        /*
        1. Создается объект FlowableCreate, в конструктор которого передается
        FlowableOnSubscribe<T> source и BackpressureStrategy backpressure.
        2. Все
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
            //Представляет варианты применения противодавления к исходной последовательности.
            /*
             - MISSING
             События OnNext записываются без какой-либо буферизации или отбрасывания.
             Нисходящему потоку приходится иметь дело с любым переполнением.
             Полезно, когда применяется один из операторов пользовательских параметров onBackpressureXXX.
             - ERROR
             Сигнализирует об исключении MissingBackpressureException в случае,
             если нисходящий поток не может справиться.
             - BUFFER (используется по умолчанию, например если передать null)
             Буферизует все значения onNext, пока нисходящий поток не потребляет их.
             По умолчанию размер буфера - 128.
             - DROP
             Отбрасывает самое последнее значение onNext, если нисходящий поток не успевает.
             - LATEST
             Сохраняет только последнее значение onNext, перезаписывая любое предыдущее значение,
             если нисходящий поток не может поддерживать его.
             */
            BackpressureStrategy.LATEST
        )
        //Как пример мы можем создать в методе subscribe(emitter: FlowableEmitter)
        //какой-нибудь объект наблюдатель за каким-нибудь
        //событием (Например подписываемся на обновление БД Room). В этом объекте-наблюдателе
        // вызываем emitter.onNext. Не забываем удалить этот наблюдатель. Это можно сделать
        // через вызов emitter.setDisposable(Disposables.fromAction(...)).
        //Ниже пример такого действия:
        //return Flowable.create(new FlowableOnSubscribe<Object>() {
        //            @Override
        //            public void subscribe(final FlowableEmitter<Object> emitter) throws Exception {
        //                final InvalidationTracker.Observer observer = new InvalidationTracker.Observer(
        //                        tableNames) {
        //                    @Override
        //                    public void onInvalidated(@androidx.annotation.NonNull Set<String> tables) {
        //                        if (!emitter.isCancelled()) {
        //                            emitter.onNext(NOTHING);
        //                        }
        //                    }
        //                };
        //                if (!emitter.isCancelled()) {
        //                    database.getInvalidationTracker().addObserver(observer);
        //                    emitter.setDisposable(Disposables.fromAction(new Action() {
        //                        @Override
        //                        public void run() throws Exception {
        //                            database.getInvalidationTracker().removeObserver(observer);
        //                        }
        //                    }));
        //                }
        //
        //                // emit once to avoid missing any data and also easy chaining
        //                if (!emitter.isCancelled()) {
        //                    emitter.onNext(NOTHING);
        //                }
        //            }
        //        }, BackpressureStrategy.LATEST);
    }
}