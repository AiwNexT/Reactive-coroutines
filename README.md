# RC: reactive coroutines

RC is a lightweight Android library built for better asynchronous operations management. It is like ReaciveX, but less complicated and built on Kotlin coroutines. 
You can run single tasks, subscribe to different events, create streams to sequentially process code with different coroutine contexts or simply fire chain to execute something sequentially.

**Usage**

To use the library, you need to implement the RCScope interface. 

```Kotlin
class MainActivity : AppCompatActivity(), RCScope
```

**Single tasks**

You can create a single task using the code below

```Kotlin
  task({
    println("Some task")
  }, Workers.default())
    .execute()
```
