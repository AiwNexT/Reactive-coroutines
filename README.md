# RC: reactive coroutines
[ ![Download](https://api.bintray.com/packages/aiwnext/RC/com.aiwnext.rc/images/download.svg?version=1.0.1) ](https://bintray.com/aiwnext/RC/com.aiwnext.rc/1.0.1/link)

RC is a lightweight Android library built for better asynchronous operations management. It is like ReaciveX, but less complicated and built on Kotlin coroutines. 
You can run single tasks, subscribe to different events, create streams to sequentially process code with different coroutine contexts or simply fire chain to execute something sequentially.

**Usage**

To use the library, you will need to add the dependency in your app's build.gradle

```Gradle
implementation 'com.aiwnext.rc:reactive-coroutines:1.0.1'

// You also need these 2 dependencies for the library to work
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9'
```

And also you need to add Jcenter to repositories in your project's build.gradle

```Gradle
buildscript {    
  repositories {
    jcenter()
  }
}
```

To start using RC in your class, you need to implement the RCScope interface. 

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
You can skip the worker field and the default worker will be used automatically.

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

**Subscriptions**

You can create a subscription and retrieve the result of your code asynchronously. To create a subscription, add the following lines of code:

```Kotlin
val subscription = subscription({
  return@subscription "Something happened"
}, Workers.ui())
```

Then you can subscribe to it and receive results asynchronously. You can add as many subscribers as you wish. Each of them will be executed with the appropriate worker.

```Kotlin
subscription.subscribe(object : Subscriber<String> {
  override fun onComplete(result: String) {
    println("What happened? $result")
  }
}, Workers.default())

subscription.subscribe(object : Subscriber<String> {
  override fun onComplete(result: String) {
    println("Here is some result: $result")
  }
}, Workers.io())
```

You can add onComplete and onSchedule blocks for subscriptions as well. 
To run your subscription, just add:

```Kotlin
subscription.execute()
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
stream.add(object : StreamComponent<Int, String> {
  override fun onComplete(result: Int): String {
    println("The result is $result")
    return result.toString()
  }
}, Workers.default())

stream.add(object : StreamComponent<String, Boolean> {
  override fun onComplete(result: String): Boolean {
    println("The result is $result")
    return result.toInt() == 10
  }
}, Workers.ui())

stream.add(object : StreamComponent<Boolean, Unit> {
  override fun onComplete(result: Boolean) {
    println("The final result is $result")
  }
}, Workers.dedicated())
```

You can add onSchedule and onComplete operations for streams as well. To run your stream, just call execute().

**Chains**

Chains are useful when you want to execute some code sequentially, but when you do not any result to be returned (something like fire and forget).
To create a chain, write the following code:

```Kotlin
val chain = chain()
```

After that just add your blocks and specify workers for each of them

```Kotlin
chain.add({
  print("Some block")
}, Workers.default())

chain.add({
  print("Another block")
}, Workers.default())

chain.add({
  print("Yet another block")
}, Workers.ui())
```

You can run your chain by calling execute()

The library is in development and I plan to add more and more features in future updates.

License
-------

    Copyright 2020 Alexander Kucherenko

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
