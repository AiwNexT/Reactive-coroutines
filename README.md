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

**Subscribers**

You can create a subscriber and retrieve the result of your code asynchronously. To create a subscriber, add the following lines of code:

```Kotlin
val subscriber = subscriber({
  return@subscriber "Something happened"
}, Workers.ui())
```

Then you can subscribe to it and receive results asynchronously. You can create as many subscriptions as you wish. Each of them will be executed with the appropriate worker.

```Kotlin
subscriber.subscribe(object : Subscription<String> {
  override fun onComplete(result: String) {
    println("What happened? $result")
  }
}, Workers.default())

subscriber.subscribe(object : Subscription<String> {
  override fun onComplete(result: String) {
    println("Here is some result: $result")
  }
}, Workers.io())
```

You can add onComplete and onSchedule blocks for subscribers as well. 
To run your subscriber, just add:

```Kotlin
subscriber.execute()
```

**Streams**

Streams can help you to pass some parameters for future asynchronous processing. You can create as long streams as you wish. To create a stream, just add:

```Kotlin
val stream = stream({
  return@stream 10
}, Workers.io())
```

Then you can add another stream component by adding the following code:

```Kotlin
stream.stream(object : Stream<Int, String> {
  override fun onComplete(result: Int): String {
    println("The result is $result")
    return result.toString()
  }
}, Workers.default())
        
stream.stream(object : Stream<String, Boolean> {
  override fun onComplete(result: String): Boolean {
    println("The result is $result")
    return result.toInt() == 10
  }
}, Workers.ui())
        
stream.stream(object : Stream<Boolean, Unit> {
  override fun onComplete(result: Boolean) {
    println("The final result is $result")
  }
}, Workers.dedicated())
```

You can add onSchedule and onComplete operations for streams as well. To run your stream, just call execute().
