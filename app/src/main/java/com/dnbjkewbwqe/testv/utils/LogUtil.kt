package com.dnbjkewbwqe.testv.utils

import android.util.Log

private val btn = true

fun Any.d(msg : String){
    if(btn.not())
        return
    val tag = "|${this.javaClass.simpleName}|${Thread.currentThread().name}|"
    val trace = Throwable().stackTrace
    val traceElement = trace.firstOrNull { it.className != this.javaClass.name }
    val message = if (traceElement != null)
        "(${traceElement.fileName}:${traceElement.lineNumber}) $msg"
    else msg
    Log.d(tag,msg)
}
fun Any.e(msg : String){
    if(btn.not())
        return
    val tag = "|${this.javaClass.simpleName}|${Thread.currentThread().name}|"
    val trace = Throwable().stackTrace
    val traceElement = trace.firstOrNull { it.className != this.javaClass.name }
    val message = if (traceElement != null)
        "(${traceElement.fileName}:${traceElement.lineNumber}) $msg"
    else msg
    Log.e(tag,msg)
}