package com.github.zharovvv.rxjavasandbox.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
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

    companion object {
        private const val LOG_RX_JAVA_TAG = "RxJava"
    }

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

    private lateinit var progressBar: ProgressBar

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
                Log.i(LOG_RX_JAVA_TAG, "emitter thread: ${Thread.currentThread().name}")
                for (i in 1..10) {
                    emitter.onNext(i)
                }
                emitter.onComplete()
            }
                .doOnSubscribe {
                    Log.i(
                        LOG_RX_JAVA_TAG,
                        "#doOnSubscribe; thread: ${Thread.currentThread().name}"
                    ) //#doOnSubscribe; thread: RxCachedThreadScheduler-2
                    Thread.sleep(2000)
//                    showLoadingIndicator()
//                     - при размещении здесь данного метода в случае
//                    hot observable для подписчиков будет вызван метод onError,
//                    в который будет передано исключение CalledFromWrongThreadException(
//                    "Only the original thread that created a view hierarchy can touch its views.")
                }
                .doOnNext {
                    Log.i(LOG_RX_JAVA_TAG, "#doOnNext; thread: ${Thread.currentThread().name}")
                    Thread.sleep(1000)
                }
                .doOnComplete {
                    Log.i(LOG_RX_JAVA_TAG, "#doOnComplete; thread: ${Thread.currentThread().name}")
                    Thread.sleep(1000)
                }
                .doOnError {

                }
                .doOnTerminate {
                    Log.i(LOG_RX_JAVA_TAG, "#doOnTerminate; thread: ${Thread.currentThread().name}")
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Int::toString)
        coldTimerObservable = timerObservable
        /**
         * Есть способы превратить холодные Observable в горячие и наоборот.
         * * Холодные объекты становятся горячими из-за publish() оператора.
         * * Используя Subject (можем не только преобразовать холодное в горячее наблюдаемое, но также можем создать горячее наблюдаемое с нуля)
         * Because a Subject subscribes to an Observable,
         * it will trigger that Observable to begin emitting items
         * (if that Observable is “cold” — that is, if it waits for a subscription before
         * it begins to emit items). This can have the effect of making the resulting Subject
         * a “hot” Observable variant of the original “cold” Observable.
         * [http://reactivex.io/documentation/subject.html]
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
        progressBar = view.findViewById(R.id.progress_bar_indicator)
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
                    Log.i(
                        LOG_RX_JAVA_TAG,
                        "#onNext; observableValue: $observableNumber thread: ${Thread.currentThread().name}"
                    )
                    coldTextView.apply {
                        text = observableNumber
                    }
                },
                { throwable ->
                    coldTextView.apply {
                        Log.e(LOG_RX_JAVA_TAG, "cold observable; throwable: ${throwable.message}")
                        text = throwable.message
                    }
                },
                {
                    Log.i(LOG_RX_JAVA_TAG, "#onComplete; thread: ${Thread.currentThread().name}")
                    coldTextView.apply {
                        text = "Complete"
                    }
                    hideLoadingIndicator()
                },
                {
                    showLoadingIndicator()
                }
            )

        hotDisposable = hotTimerObservable
            .subscribe(
                { observableNumber: String ->
                    hotTextView.apply {
                        text = observableNumber
                    }
                },
                { throwable ->
                    Log.e(LOG_RX_JAVA_TAG, "hot observable; throwable: ${throwable.message}")
                    hotTextView.apply {
                        text = throwable.message
                    }
                },
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

    private fun showLoadingIndicator() {
        progressBar.visibility = VISIBLE
    }

    private fun hideLoadingIndicator() {
        progressBar.visibility = GONE
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