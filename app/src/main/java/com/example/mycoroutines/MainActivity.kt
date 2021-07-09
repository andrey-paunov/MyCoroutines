package com.example.mycoroutines

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "!===="
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doStuff()
    }

    private fun doStuff() {
        val mainCoroutineScope = CoroutineScope(Dispatchers.Main)

        mainCoroutineScope.launch {
            Log.d(TAG, "doStuff Thread(${getThreadName()})")

            try {
                val deferredOne = async {
                    doSomethingLongOne(2, Dispatchers.Default)
                }

                val deferredTwo = async {
                    doSomethingLongTwo(2, Dispatchers.Default)
                }

                doIt()

                val resultOne = deferredOne.await()
                val resultTwo = deferredTwo.await()

                if (resultOne is Result.Success<Int> && resultTwo is Result.Success<Int>) {
                    Log.d(
                        TAG,
                        "doStuff Thread(${getThreadName()}) Sum=${resultOne.data + resultTwo.data}"
                    )
                } else if (resultOne is Result.Error && resultTwo is Result.Success) {
                    Log.d(TAG, "doStuff Thread(${getThreadName()}) resultOne API call Failed")
                } else if (resultTwo is Result.Error && resultOne is Result.Success) {
                    Log.d(TAG, "doStuff Thread(${getThreadName()}) resultTwo API call Failed")
                } else {
                    Log.d(TAG, "doStuff Thread(${getThreadName()}) Both API calls Failed")
                }
            } catch (e: Exception) {
                Log.d(TAG, "eCause: ${e.cause} message: ${e.message}")
            }
        }
    }

    private suspend fun doIt(): Result<String> {
        return Result.Undefined("Hello!")
    }

    private suspend fun doSomethingLongOne(
        retryCount: Int,
        dispatcher: CoroutineDispatcher
    ): Result<Int> {
        return withContext(dispatcher) {
            Log.d(TAG, "doSomethingLongOne Thread(${getThreadName()}) retryCount: $retryCount")
            delay(3000)

            if (retryCount > 0) {
                // Even
                return@withContext doSomethingLongOne(retryCount - 1, dispatcher)
            } else {
                // Odd
                val random = Random.nextInt(2)
                val ranMod = random % 2

                if (ranMod == 0) {
                    Log.d(TAG, "doSomethingLongOne Thread(${getThreadName()}) retryCount: $retryCount Error")

                    return@withContext Result.Error(Exception("Two Result Error"))
                }
            }

            Log.d(TAG, "doSomethingLongOne Thread(${getThreadName()}) retryCount: $retryCount Success")
            return@withContext Result.Success(43)
        }
    }

    private suspend fun doSomethingLongTwo(
        retryCount: Int,
        dispatcher: CoroutineDispatcher
    ): Result<Int> {
        return withContext(dispatcher) {
            Log.d(TAG, "doSomethingLongTwo Thread(${getThreadName()}) retryCount: $retryCount")
            delay(5000)
//            throw Exception("Uh-Oh! Error!")

            if (retryCount > 0) {
                // Even
                return@withContext doSomethingLongTwo(retryCount - 1, dispatcher)
            } else {
                // Odd
                val random = Random.nextInt(2)
                val ranMod = random % 2

                if (ranMod == 0) {
                    Log.d(TAG, "doSomethingLongTwo Thread(${getThreadName()}) retryCount: $retryCount Error")
                    return@withContext Result.Error(Exception("Two Result Error"))
                }
            }

            Log.d(TAG, "doSomethingLongTwo Thread(${getThreadName()}) retryCount: $retryCount Success")
            return@withContext Result.Success(15)
        }
    }

    private fun getThreadName(): String {
        val thread = Thread.currentThread()
        return thread.name
    }
}