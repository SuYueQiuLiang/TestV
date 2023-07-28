package com.dnbjkewbwqe.testv.utils

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.os.Bundle
import com.dnbjkewbwqe.testv.ui.StartActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

fun Activity.startActivity(clazz: Class<out Activity>, bundle: Bundle? = null) {
    val intent = Intent(this, clazz)
    bundle?.let { intent.putExtras(it) }
    startActivity(intent)
}

object ActivityManager : ActivityLifecycleCallbacks {
    private val activityList = mutableListOf<WeakReference<Activity>>()
    private var showingActivityList = mutableListOf<WeakReference<Activity>>()
    private var job: Job? = null
    private const val delay = 3000L
    var reLoadAd = false
    var coldBoot = false
    var plainB = true
    fun isAvailable(activity: Activity?): Boolean {
        if (activity == null)
            return false
        synchronized(showingActivityList) {
            val iterator = showingActivityList.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().get() == activity)
                    return true
            }
            return false
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        synchronized(activityList) {
            activityList.add(WeakReference(activity))
            d("activity $activity created,${activityList.size} in stack now")
        }
    }

    override fun onActivityStarted(activity: Activity) {
        synchronized(this) {
            showingActivityList.add(WeakReference(activity))
            job?.cancel()
            if (coldBoot) {
                coldBoot = false
                activity.startActivity(StartActivity::class.java)
            }
            d("activity $activity started,${showingActivityList.size} in stack now")
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onActivityStopped(activity: Activity) {
        synchronized(this) {
            removeWeakReferenceFromList(activity, showingActivityList)
            d("activity $activity stopped,${showingActivityList.size} in stack now")
            if (showingActivityList.size <= 0)
                job = GlobalScope.launch {
                    delay(delay)
                    if (isActive) {
                        coldBoot = true
                        if (ReferrerUtil.creSmall.cre_xtra == "2")
                            plainB = true
                    }
                }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        synchronized(activityList) {
            removeWeakReferenceFromList(activity, activityList)
            d("activity $activity destroyed,${activityList.size} in stack now")
        }
    }

    fun finishAPP() {
        synchronized(activityList) {
            val iterator = activityList.iterator()
            while (iterator.hasNext())
                iterator.next().get()?.finish()
        }
    }

    private fun removeWeakReferenceFromList(activity: Activity, list: MutableList<WeakReference<Activity>>) {
        synchronized(list) {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next().get()
                if (next == null) {
                    iterator.remove()
                    continue
                }
                if (next == activity) {
                    iterator.remove()
                    break
                }
            }
        }
    }
}