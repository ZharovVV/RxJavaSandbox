package com.github.zharovvv.rxjavasandbox.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.zharovvv.rxjavasandbox.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observables.ConnectableObservable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class MainFragment : RetainFragment() {

    private val timerObservable: Observable<String>

    /**
     * Холодные Observable - это Observable, которые запускают свою последовательность,
     * когда и если на них подписаны.
     * Они предоставляют последовательность с самого начала для каждого подписчика.
     * Независимо от того, когда он создан и на что подписан,
     * он будет генерировать одну и ту же последовательность для каждого подписчика.
     *
     * Два подписчика не получают одно и то же значение одновременно,
     * даже если они оба подписаны на одно и то же наблюдаемое.
     * Они действительно видят ту же последовательность, за исключением того,
     * что каждый из них видит, что она началась, когда они подписались.
     *
     * Каждый Observable, созданный с помощью, Observable.create является холодным Observable.
     * Это включает в себя все Shorthands, такие как just, range, timer и from.
     *
     * Холодные Observable не обязательно представляют одинаковую последовательность
     * каждому подписчику.
     * Если, например, Observable подключается к базе данных и выдает результаты запроса,
     * фактическое значение будет зависеть от состояния базы данных во время подписки.
     * Тот факт, что подписчик будет получать весь запрос с самого начала,
     * и делает этот Observable холодным.
     */
    private val coldTimerObservable: Observable<String>

    /**
     * Горячие Observable генерируют значения независимо от индивидуальных подписок.
     * У них своя временная шкала, и события происходят вне зависимости от того,
     * слушают их кто-то или нет.
     *
     * Примером этого являются события мыши.
     * Мышь генерирует события независимо от того, прослушивает ли их подписка.
     * При оформлении подписки наблюдатель получает текущие события по мере их возникновения.
     * Вы не получаете и не хотите получать отчет обо всем,
     * что сделала мышь с момента загрузки системы.
     *
     * Когда вы отказываетесь от подписки, это также не мешает вашей мыши генерировать события.
     * Вы их просто не получаете.
     * Если вы повторно подпишетесь, вы снова увидите текущие события без повторения того,
     * что вы пропустили.
     */
    private val hotTimerObservable: ConnectableObservable<String>

    private lateinit var coldTextView: TextView
    private lateinit var hotTextView: TextView
    private lateinit var coldDisposable: Disposable
    private lateinit var hotDisposable: Disposable

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
//        Observable.just(1, 2, 3, 4, 5)
//        Observable.fromIterable(listOf(1, 2, 3, 4, 5))
        timerObservable =
            Observable.create<Int> { emitter ->   //крайне не желательно использовть create для создания Observable
                for (i in 1..10) {
                    emitter.onNext(i)
                }
                emitter.onComplete()
            }
                .doOnSubscribe {
                    Thread.sleep(1000)
                }
                .doOnNext {
                    Thread.sleep(1000)
                }
                .doOnComplete {
                    Thread.sleep(1000)
                }
                .doOnError {

                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Int::toString)
        coldTimerObservable = timerObservable
        /**
         * Есть способы превратить холодные Observable в горячие и наоборот.
         * Холодные объекты становятся горячими из-за publish() оператора.
         */
        hotTimerObservable = timerObservable.publish()

        /**
         * Метод connect() заставляет ConnectableObservable начать рассылку элементов
         * своим подписчикам. Возвращет объект Disposable.
         *
         * Вызов dispose() у этого Disposable остановит передачу событий наблюдателям,
         * но не отменит их подписку на ConnectableObservable.
         *
         * При повторном вызове connect() у ConnectableObservable начнется новая подписка,
         * и старые наблюдатели снова начнут получать значения.
         */
        val connectionDisposable = hotTimerObservable.connect()
        compositeDisposable += connectionDisposable

        /**
         * refCount()
         *
         * Подключение длится до тех пор пока на Observable есть подписчики
         * Возвращает Observable<T>.
         *
         * Другой метод share() аналогичен вызову publish().refCount()
         */
//            timerObservable.publish().refCount()
        /**
         * replay()
         *
         * После подключения он начнет сбор значений.
         * Как только новый наблюдатель подписывается на наблюдаемое,
         * ему будут воспроизведены все собранные значения.
         * Как только он догонит, он будет получать значения параллельно всем остальным наблюдателям.
         *
         * Возвращает ConnectableObservable<T>
         */
//            timerObservable.replay()
        /**
         * cache()
         *
         * Оператор похож на replay(), однако последовательность начинается не тогда,
         * когда наблюдаемый объект был создан, а при первой подписке.
         * Как только появится первый подписчик, наблюдаемый источник будет раз и навсегда кэширован.
         *
         * Важно отметить, что значения будут кэшироваться до тех пор,
         * пока источник не прекратит работу или у нас не закончится память, не зависимо от того,
         * есть ли у Observable подписчики или нет.
         *
         * Возвращает Observable<T>.
         */
//        timerObservable.cache()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coldTextView = view.findViewById(R.id.cold_text_view)
        hotTextView = view.findViewById(R.id.hot_text_view)
        subscribeOnTimer()
    }

    private fun subscribeOnTimer() {
        coldDisposable = coldTimerObservable
            /**
             * Cold Observable
             * - Не рассылает объекты, пока на него не подписался хотя бы один подписчик
             * - Если observable имеет несколько подписчиков, то он будет рассылать
             * всю последовательность объектов каждому подписчику
             */
            .subscribe(
                { observableNumber: String ->
                    coldTextView.apply {
                        text = observableNumber
                    }
                },
                { throwable ->
                    coldTextView.apply {
                        Log.e("observableError", throwable.message ?: "null")
                        text = throwable.message
                    }
                },
                {
                    coldTextView.apply {
                        text = "Complete"
                    }
                }
            )

        hotDisposable = hotTimerObservable
            .subscribe(
                { observableNumber: String ->
                    hotTextView.apply {
                        text = observableNumber
                    }
                },
                {},
                {
                    hotTextView.apply {
                        text = "Complete"
                    }
                }
            )
        /**
         * Добавление Disposable в compositeDisposable.
         *
         * Если compositeDisposable.isDisposed == true, то добавления Disposable не происходит и
         * для добавляемого Disposable вызывается dispose().
         */
        compositeDisposable += coldDisposable
        compositeDisposable += hotDisposable
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onDetach() {
        super.onDetach()
        coldDisposable.dispose()
        hotDisposable.dispose()
    }
}