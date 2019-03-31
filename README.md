# KotlinFrameWork
> 本实例封装Kotlin网络核心库以及重要核心逻辑

## 开始

### 1.app模块:
#### 添加依赖
```gradle
###### retrofit相关
implementation 'com.squareup.okhttp3:logging-interceptor:3.10.0'
implementation 'com.squareup.retrofit2:adapter-rxjava2:2.5.0'
implementation 'com.squareup.retrofit2:retrofit:2.5.0'
###### rxjava2
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
###### rxlifecycle
implementation 'com.trello.rxlifecycle2:rxlifecycle-kotlin:2.2.0'
implementation 'com.trello.rxlifecycle2:rxlifecycle-components:2.2.0'
```

**NOTE**:可以去[Retrofit](https://github.com/square/retrofit)、[Rxjava2(RxAndroid)](https://github.com/ReactiveX/RxAndroid)、[okhttp](https://github.com/square/okhttp)、[RxLifecycle](https://github.com/trello/RxLifecycle)，查询最新版本号。

### 2.net库之封装请求类
为了秉承`RxJava`的链式调用风格，也为了方便每一个`API`的调用操作，创建了一个单例类`ApiClient`，具体如下：
```kotlin
class RetrofitManager private constructor() {

//    lateinit var apiService: ApiService
    lateinit var  retrofit:Retrofit

    /**
     * 单例模式
     */
    companion object {
        val instance: RetrofitManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {

            RetrofitManager()
        }
    }

    fun init(string: String) {
        val okHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE))
                .connectTimeout(5,TimeUnit.SECONDS)
                .readTimeout(5,TimeUnit.SECONDS)
                .writeTimeout(5,TimeUnit.SECONDS)
                .build()
        retrofit = Retrofit.Builder()
            .baseUrl(string)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

//        apiService = retrofit.create(ApiService::class.java)

    }

    /**
     * 动态代理模式，创建请求接口类
     * @param tClass
     * @param <T>
     * @return
    </T> */
    fun <T> createService(tClass: Class<T>): T {

        return retrofit.create(tClass)
    }
}
```
其中接口声明类：GitHubService如下：
```kotlin
/**
 * 接口声明类
 */
interface ApiService{
    /**
     * 登录
     */
    @POST
    @FormUrlEncoded
    fun login(@Url string: String, @Field("phone") mobile:String, @Field("pwd")pwd: String): Observable<UserBean>

}
```
上面的`UserBean`即一个简单的`Kotlin`数据类，可以去[这里](https://github.com/ZYRzyr/ApiClient/tree/master/app/src/main/java/com/zyr/apiclient/data)查看。

### 3.支持`RESTful API`请求响应的处理
`API`的响应返回形式有很多种，此处介绍最常见的两种形式的处理：标准`RESTful API`与`任性的后端写的API`。

请求响应主要处理状态码与数据体，具体封装如下：
```kotlin
/**
 * 封装响应数据，统一异常处理
 */
abstract class NetResponseObserver<T>(private val context: Context):Observer<T>{

    /**
     * 事件接收完毕
     */
    override fun onComplete() {
        LoadingDialog.cancel()
    }

    /**
     * 订阅事件的回调
     */
    override fun onSubscribe(d: Disposable) {
        LoadingDialog.show(context)
    }

    /**
     * 接收事件
     */
    override fun onNext(t: T) {
        success(t)
    }

    /**
     * 成功的回调
     */
    abstract fun success(data: T)

    /**
     * 失败的回调
     */
    abstract fun failure(statusCode: Int, apiErrorModel: ApiErrorModel)

    /**
     * 异常处理
     */
    override fun onError(e: Throwable) {
        LoadingDialog.cancel()
        if (e is HttpException) {
            val apiErrorModel: ApiErrorModel = when (e.code()) {
                ApiErrorType.INTERNAL_SERVER_ERROR.code ->
                    ApiErrorType.INTERNAL_SERVER_ERROR.getApiErrorModel(context)
                ApiErrorType.BAD_GATEWAY.code ->
                    ApiErrorType.BAD_GATEWAY.getApiErrorModel(context)
                ApiErrorType.NOT_FOUND.code ->
                    ApiErrorType.NOT_FOUND.getApiErrorModel(context)
                else -> otherError(e)

            }
            failure(e.code(), apiErrorModel)
            return
        }

        val apiErrorType: ApiErrorType = when (e) {
            is UnknownHostException -> ApiErrorType.NETWORK_NOT_CONNECT
            is ConnectException -> ApiErrorType.NETWORK_NOT_CONNECT
            is SocketTimeoutException -> ApiErrorType.CONNECTION_TIMEOUT
            else -> ApiErrorType.UNEXPECTED_ERROR
        }
        failure(apiErrorType.code, apiErrorType.getApiErrorModel(context))

    }
    private fun otherError(e: HttpException) =
        Gson().fromJson(e.response().errorBody()?.charStream(), ApiErrorModel::class.java)

}
```
**说明** :

1.每个响应继承`Observer`，其中的`泛型`以适配返回的不同的数据体；

2.定义两个抽象方法`success`和`failure`，在使用的时候只需关注成功和失败这两种情况；

3.在`onSubscribe`即开始请求的时候显示`Loading`，在请求完成或出错时隐藏；

4.在`onNext`即`Observer`成功接收数据后直接调用`success`，在调用处可直接使用返回的数据；

5.在`onError`即请求出错时处理，此处包含两种情况：连接服务器成功但服务器返回错误状态码、网络或其它问题。

在错误处理中，定义了一个枚举类`ApiErrorType`，用于列举出服务器定义的错误状态码情况：
```kotlin
/**
 * 响应状态码处理
 */
enum class ApiErrorType(val code: Int, @param: StringRes private val messageId: Int) {
    //灵活定制
    INTERNAL_SERVER_ERROR(500, R.string.service_error),
    BAD_GATEWAY(502, R.string.service_error),
    NOT_FOUND(404, R.string.not_found),
    CONNECTION_TIMEOUT(408, R.string.timeout),
    NETWORK_NOT_CONNECT(499, R.string.network_wrong),
    UNEXPECTED_ERROR(700, R.string.unexpected_error);

    private val DEFAULT_CODE = 1

    fun getApiErrorModel(context: Context): ApiErrorModel {
        return ApiErrorModel(DEFAULT_CODE, context.getString(messageId))
    }
}
```
还定义了一个错误消息的的实体类`ApiErrorModel`(在`Kotlin`中即为一个数据类)，用于包含错误信息提示用户或服务器返回的错误信息以提示开发人员：
```kotlin
data class ApiErrorModel(var status: Int, var message: String)
```

### 4.线程与生命周期
`RxJava`的一大特色即方便的线程切换操作，在请求`API`中需要进行线程的切换，通常是以下形式(伪代码)：
```kotlin
observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
```
但每个请求都写一段这个，显得特别麻烦，所以进行以下简单封装:
```kotlin
/**
 * 线程调度器
 */
object NetScheduler{
    fun <T> compose():ObservableTransformer<T,T>{

        return ObservableTransformer {
            observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }
}
```
使用的时候简单搞定，伪代码如下：
```kotlin
observable.compose(NetScheduler.compose())
```
在`Android`中，当一个`Activity`在调`API`时`onDestroy`了，需要取消请求，所以此处引入了`RxLifecycle`进行管理：
`Activity`继承`RxAppCompatActivity`后，在`observable`的调用链中加入`.bindUntilEvent(this, ActivityEvent.DESTROY)`即可，伪代码如下：
```kotlin
observable.compose(NetScheduler.compose())
          .bindUntilEvent(this, ActivityEvent.DESTROY)  //加入这句
          .subscribe(...)
```

### 5.使用
在以上准备工作完成后，即可开始使用：

首先在`Application`中初始化`ApiClient`：
```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
          RetrofitManager.instance.init(Api.BASE_URL)
    }
}
```

在需要的地方使用`ApiClient`，点击按钮时，请求数据，成功后用`TextView`显示出来:
```kotlin
class MainActivity : RxAppCompatActivity(), View.OnClickListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

    }

    private fun initView() {

        login.setOnClickListener(this)

    }

    /**
     * 登录测试
     */
    fun login() {

        //链式调用
        RetrofitManager.instance.createService(ApiService::class.java).login(Api.LOGIN_URL,"18612991023","111111")
            .compose(NetScheduler.compose())
            .bindUntilEvent(this, ActivityEvent.DESTROY)
            .subscribe(object : NetResponseObserver<UserBean>(this){
                override fun success(data: UserBean) {
                    Toast.makeText(this@MainActivity,data.result.phone,Toast.LENGTH_SHORT).show()
                }

                override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {

                }

            })

    }

    /**
     * 点击事件
     */
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.login -> login()
            R.id.reg -> reg()
        }
    }

    /**
     * 注册测试
     */
    private fun reg() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
```


### 6.`任性的后端写的API`请求响应的处理
这种情况只需要对数据类和响应处理进行修改即可。有些后端开发者们，可能将返回体写成如下形式:
```json
{
    "result": {
        "headPic": "http://mobile.bwstudent.com/images/small/head_pic/2019-02-26/20190226233015.jpeg",
        "nickName": "Jr_09b24",
        "phone": "18612991023",
        "sessionId": "1553950250180251",
        "sex": 1,
        "userId": 251
    },
    "message": "登录成功",
    "status": "0000"
}
```
所有返回的数据中，最外层都包裹了一层信息，以表示请求成功或失败，中间`data`才是具体数据，所以定义数据类(实体类)时，需要定义成如下形式：
```kotlin
/**
 * 实体类：通过jsontokotlin插件生成
 */
data class UserBean(
    val message: String,
    val result: Result,
    val status: String
)

data class Result(
    val headPic: String,
    val nickName: String,
    val phone: String,
    val sessionId: String,
    val sex: Int,
    val userId: Int
)
```

**2019年3月31日更新**

后续新增上传图片的方法，敬请期待
