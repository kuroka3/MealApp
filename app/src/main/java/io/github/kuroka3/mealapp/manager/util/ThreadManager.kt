package io.github.kuroka3.mealapp.manager.util

object ThreadManager {

    fun runInAnotherThread(runnable: Runnable): Thread {
        val thr = Thread(runnable)
        thr.start()
        return thr
    }

    /*fun runLater(runnable: Runnable, delay: Long): Thread {
        val thr = Thread {
            Thread.sleep(delay)
            runnable.run()
        }
        thr.start()
        return thr
    }

    fun runTimer(runnable: Runnable, delay: Long): Thread {
        val thr = Thread {
            while (true) {
                runnable.run()
                Thread.sleep(delay)
            }
        }
        thr.start()
        return thr
    }*/
}