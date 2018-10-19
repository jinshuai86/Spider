#### 单例模式(懒汉式)

```
    private static volatile HTTPUTILS;
    
    public static HttpUtils getSingleInstance() {
        // 判断是否已经创建了该实例
        if (HTTPUTILS == null) {
            // 因为是非原子操作，多线程访问修改不安全，要加锁。
            synchronized (HttpUtils.class) {
                // 防止并发情况下在经过了第一个if以后该线程挂起，另一个线程创建了对象。
                if (HTTPUTILS == null) {
                    /**
                     * new HttpUtils() 可能会发生指令重排序:
                     *   memory = allocate();   //1：分配对象的内存空间
                     *   ctorInstance(memory);  //2：初始化对象
                     *   HTTPUTILS = memory;    //3：设置instance指向刚分配的内存地址
                     *   由于2初始化状态比较长，顺序可能会变成1 3 2
                     *   比如： 
                     *     线程1 将HTTPUTILS已经指向内存空间，但此时的对象没初始化。
                     *     线程2 进行第二次判断的时候 HTTPUTILS != NULL 直接去使用，导致错误。
                     * */
                    HTTPUTILS = new HttpUtils();
                }
            }
        }
        return HTTPUTILS;
    }
```
### 单例模式(饿汉式)

```
    private static final HttpUtils HTTPUTILS = new HttpUtils();
    
    public static getSingleInstance() {
        return HTTPUTILS;
    }
```