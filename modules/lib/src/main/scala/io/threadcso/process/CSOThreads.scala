package io.threadcso.process

/**
        This is the module in which the characteristics of the
        pooling of threads used by threaded-`CSO` are
        determined.

        To be specific, the following jdk runtime properties determine
        thread-pooling characteristics, and can be set
        either from a program that uses CSO (before any CSO
        process construct is executed) or (as we usually expect)
        by setting the appropriate jdk property
        from the (usually scala) command line that runs the program.

        - `io.threadcso.pool.SECS`      the time before pooled threads are deemed idle (ADAPTIVE and SIZED pools only). See [[CSOThreads.poolSECS]]
        - `io.threadcso.pool.KIND`      the kind of the pool: ADAPTIVE, SIZED, CACHED, UNPOOLED. See [[CSOThreads.poolKIND]]
        - `io.threadcso.pool.REPORT`    report pool performance on program exit. See [[CSOThreads.poolREPORT]]

        The following runtime properties have specialized interpretations.

        - `io.threadcso.pool.G + io.threadcso.pool.M + io.threadcso.pool.K` specifies default stacksize for non-SIZED pools. See [[CSOThreads.poolSTACKSIZE]]
        - io.threadcso.pool.MAX See [[CSOThreads.poolMAX]]
        - io.threadcso.pool.MIN See [[CSOThreads.poolMIN]]


*/
object CSOThreads
{  import java.util.concurrent._

  import io.threadcso.basis._

   private val mainThread = Thread.currentThread

   /** The thread group containing threads used to run CSO processes. */
   private val csoThreads = new ThreadGroup("CSO")

   /** PRE:  size of CSO thread pools (or count of CSO threads) is not increasing
       POST: returns the active CSO threads
   */
   def getActive: Seq[Thread] =
   { val count   = 30 + csoThreads.activeCount // overestimate
     val threads = Array.ofDim[Thread](count)
     val actual  = csoThreads.enumerate(threads)
     threads(actual)=mainThread
     for (i<-0 until actual+1) yield threads(i)
   }

   /** Make a thread factory for CSO threads of the specified size */
   private def factory(stackSize: Long) = new ThreadFactory
   {
     private val threadCount = new java.util.concurrent.atomic.AtomicLong

     def newThread(r: Runnable): Thread =
         new Thread(csoThreads, r, "cso-pool-%d[%d]".format(threadCount.getAndIncrement, stackSize),
                    stackSize)

   }

   /**
        Construct a single ADAPTIVE pooled executor to generate threads with
        stacks with the recommended `stackSize`.
   */
   private def sizePooledCSOExecutor(stackSize: Long) =
   {  import java.util.concurrent._
      val pool =
      new ThreadPoolExecutor( poolMIN, poolMAX
                            , poolSecs,  TimeUnit.SECONDS
                            , new SynchronousQueue[Runnable]()
                            , factory(stackSize))
      new ThreadPooledCSOExecutor(poolREPORT, pool, stackSize)
   }


   /** Setting a jdk property with `-Dio.threadcso.pool=`''secs'' specifies that idling pooled threads expire
        after ''secs'' idling. (deprecated: default 4)
   */
   val poolSecs: Long = getPropElse("io.threadcso.pool", _.toLong)(4l) // deprecated

   /** Setting a jdk property with `-Dio.threadcso.pool.SECS=`''secs'' specifies that idling pooled threads expire
        after ''secs'' idling. Default same as `poolSecs`.
   */
   val poolSECS: Long = getPropElse("io.threadcso.pool.SECS", _.toLong)(poolSecs)

   /** Setting a jdk property with `-Dio.threadcso.pool.MAX=`''number'' specifies an upper bound on the
       number of threads to be kept in ADAPTIVE or SIZED pools.
       Leave it at its default value unless you have understood the
       documentation of `ThreadPoolExecutor`.
   */
   val poolMAX: Int    = getPropElse("io.threadcso.pool.MAX", _.toInt)(Integer.MAX_VALUE)

   /** Setting a jdk property with `-Dio.threadcso.pool.MIN=`''number'' specifies a lower bound on the
       number of threads to be kept in ADAPTIVE or SIZED pools.
       Leave it at its default value unless you have understood the
       documentation of `ThreadPoolExecutor`.
   */
   val poolMIN: Int = getPropElse("io.threadcso.pool.MIN", _.toInt)(0)

   /** Setting a jdk property with `-Dio.threadcso.pool.KIND=`''kind'' what kind of thread pooling to use:
                - ADAPTIVE -- is the default. Retires idle threads after `poolSECS`. See [[SizePooledCSOExecutor]].
                - SIZED    -- runs several ADAPTIVE pools: each with threads of a similar stack size. See [[SizePooledCSOExecutor]].
                - CACHED   -- a pool that never retires idle threads.
                - UNPOOLED -- makes a new thread every time a `PROC` is run. Stacksize is as specified by the `PROC`.

                Default is  ADAPTIVE
   */
   val poolKIND: String = getPropElse("io.threadcso.pool.KIND", { s => s })("ADAPTIVE")

   /**
       Setting a jdk property with `-Dio.threadcso.pool.REPORT=true` causes adaptive pools to report on their activity when
       `ox.CSO.exit` is called, and/or the pool(s) are shut down.
   */
   val poolREPORT: Boolean = getPropElse("io.threadcso.pool.REPORT", _.toBoolean)(false)

   /** Life's too short to construct lovely prose. This is how to recommend
       the stack sizes of threads made with CACHED or ADAPTIVE pooling. Set
       the appropriate jdk property/ies with -D<propname>=...
   {{{
     val G = 1024*1024*1024*getPropElse("io.threadcso.pool.G", _.toLong)(0)
     val M =      1024*1024*getPropElse("io.threadcso.pool.M", _.toLong)(0)
     val K =           1024*getPropElse("io.threadcso.pool.K", _.toLong)(0)
     G+M+K
   }}}
   */
   val poolSTACKSIZE: Long =
   {
     val G = 1024*1024*1024*getPropElse("io.threadcso.pool.G", _.toLong)(0)
     val M =      1024*1024*getPropElse("io.threadcso.pool.M", _.toLong)(0)
     val K =           1024*getPropElse("io.threadcso.pool.K", _.toLong)(0)
     G+M+K // because life is too short to document something more complex
   }

   /**
        The `CSOExecutor` that is being used in this program. Its
        kind is fixed by `poolKind`.
   */
   val executor: CSOExecutor =
       poolKIND.toUpperCase match
       { case "SIZED" =>
              new SizePooledCSOExecutor(sizePooledCSOExecutor, poolREPORT)

         case "ADAPTIVE" =>
              sizePooledCSOExecutor(poolSTACKSIZE)

         case "CACHED" =>
              new PooledCSOExecutor(Executors.newCachedThreadPool(factory(poolSTACKSIZE)))

         case "UNPOOLED" =>
              new CSOExecutor {
                private val threadCount = new java.util.concurrent.atomic.AtomicLong
                def execute(r: Runnable, stackSize: Long): Unit =
                    new Thread(csoThreads, r, "cso-unpooled-%d".format(threadCount.getAndIncrement), stackSize).start()
                def shutdown(): Unit = {}
              }
         case _ =>
              throw new IllegalArgumentException("io.threadcso.pool.KIND should be SIZED, ADAPTIVE, CACHED, or UNPOOLED")
       }
}
