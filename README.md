# RC: reactive coroutines

RC is a lightweight Android library built for better asynchronous operations management. It is like ReaciveX, but less complicated and built on Kotlin coroutines. 
You can run single tasks, subscribe to different events, create streams to sequentially process code with different coroutine contexts or simply fire chain to execute something sequentially.

**Usage**

To use the features, you need to implement the RCScope interface. 

```Kotlin
class MainActivity : AppCompatActivity(), RCScope
```

**Single tasks**

To create a single task, use the code below.

```Kotlin
val singleTask = task({
  println("Some task")
}, Workers.default())
```

You can add onComplete and onSchedule options to execute some code when your task will be started and/or completed. You can specify a worker for both onComplete and onSchedule operations.

```Kotlin
singleTask.doOnSchedule { println("Task started") }
singleTask.doOnComplete { println("Task completed") }

singleTask.setOnScheduleWorker(Workers.dedicated())
singleTask.setOnCompleteWorker(Workers.ui())
```

To start your task, just do the following:

```Kotlin
singleTask.execute()
```

**Different workers**

There are 5 different types of workers: ui, io, default, dedicated, merged. Each of them represents the coroutine context. 
1. Ui executes your code with access to the ui thread. 
2. Io represents the io coroutine context.
3. A default one is the most common one. It executes your code in the background using a fixed thread pool equal to the CPU cores number.
4. A dedicated one uses a single thread for an operation.
5. A merged one represents a context based on a thread pool with a number of threads you specified.

```Kotlin
Workers.ui()
Workers.io()
Workers.default()
Workers.dedicated()
Workers.merged(threadCount)
```
