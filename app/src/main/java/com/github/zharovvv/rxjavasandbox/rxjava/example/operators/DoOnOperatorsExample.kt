package com.github.zharovvv.rxjavasandbox.rxjava.example.operators

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DoOnOperatorsExample {

    companion object {
        const val LOG_TAG = "DoOnOperators"
    }

    fun doOnExample(): Disposable {
        return Observable.range(0, 2)//<------------------------------- 1
            .doOnSubscribe {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doOnSubscribe 1; currentThread: $thread; value: $it;")
            }
            .doOnNext {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main]
                Log.i(LOG_TAG, "Observable#doOnNext 1; currentThread: $thread; value: $it;")
            }
            .doAfterNext {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doAfterNext 1; currentThread: $thread; value: $it;")
            }
            .doOnComplete {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doOnComplete 1; currentThread: $thread;")
            }
            .doFinally {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doFinally 1; currentThread: $thread;")
            }
            .doOnTerminate {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doOnTerminate 1; currentThread: $thread;")
            }
            .doAfterTerminate {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doAfterTerminate 1; currentThread: $thread;")
            }
            .subscribeOn(Schedulers.computation())//<------------------------------- 2
            .doOnSubscribe {
                val thread = Thread.currentThread() //currentThread: Thread[main,5,main];
                Log.i(LOG_TAG, "Observable#doOnSubscribe 2; currentThread: $thread; value: $it;")
            }
            .doOnNext {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doOnNext 2; currentThread: $thread; value: $it;")
            }
            .doAfterNext {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doAfterNext 2; currentThread: $thread; value: $it;")
            }
            .doOnComplete {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doOnComplete 2; currentThread: $thread;")
            }
            .doFinally {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doFinally 2; currentThread: $thread;")
            }
            .doOnTerminate {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doOnTerminate 2; currentThread: $thread;")
            }
            .doAfterTerminate {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxComputationThreadPool-1,5,main];
                Log.i(LOG_TAG, "Observable#doAfterTerminate 2; currentThread: $thread;")
            }
            .observeOn(Schedulers.io())  //<------------------------------- 3
            .doOnSubscribe {
                val thread = Thread.currentThread() //currentThread: Thread[main,5,main];
                Log.i(LOG_TAG, "Observable#doOnSubscribe 3; currentThread: $thread; value: $it;")
            }
            .doOnNext {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxCachedThreadScheduler-3,5,main];
                Log.i(LOG_TAG, "Observable#doOnNext 3; currentThread: $thread; value: $it;")
            }
            .doAfterNext {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxCachedThreadScheduler-3,5,main];
                Log.i(LOG_TAG, "Observable#doAfterNext 3; currentThread: $thread; value: $it;")
            }
            .doOnComplete {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxCachedThreadScheduler-3,5,main];
                Log.i(LOG_TAG, "Observable#doOnComplete 3; currentThread: $thread;")
            }
            .doFinally {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxCachedThreadScheduler-3,5,main];
                Log.i(LOG_TAG, "Observable#doFinally 3; currentThread: $thread;")
            }
            .doOnTerminate {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxCachedThreadScheduler-3,5,main];
                Log.i(LOG_TAG, "Observable#doOnTerminate 3; currentThread: $thread;")
            }
            .doAfterTerminate {
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxCachedThreadScheduler-3,5,main];
                Log.i(LOG_TAG, "Observable#doAfterTerminate 3; currentThread: $thread;")
            }
            .subscribe { t ->
                val thread =
                    Thread.currentThread() //currentThread: Thread[RxCachedThreadScheduler-3,5,main];
                Log.i(LOG_TAG, "Observer#onNext; currentThread: $thread; value: $t;")
            }   //<- вызываем этот метод из main thread-а
    }
}