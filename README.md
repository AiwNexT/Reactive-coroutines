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


