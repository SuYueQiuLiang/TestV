package com.dnbjkewbwqe.testv.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.dnbjkewbwqe.testv.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class LoadProgressButton : View {
    private val textPaint : TextPaint = TextPaint()
    private val selectedPointPaint = Paint()
    private val unselectedPointPaint = Paint()
    private var loadingJob : Job? = null
    private var pointRadius by Delegates.notNull<Float>()
    private var pointMargin by Delegates.notNull<Float>()
    var text = ""
    private val yCenter
        get() = height/2
    private val xCenter
        get() = width/2
    private val point1X
        get() = xCenter - pointRadius * 4 - pointMargin * 2
    private val point2X
        get() = xCenter - pointRadius * 2 - pointMargin
    private val point3X
        get() = xCenter.toFloat()
    private val point4X
        get() = xCenter + pointRadius * 2 + pointMargin
    private val point5X
        get() = xCenter + pointRadius * 4 + pointMargin * 2
    private var progress = 0
    constructor(context : Context) : this(context,null)
    constructor(context: Context,attrs : AttributeSet?) : super(context,attrs){
        initAttrSet(context,attrs)
    }

    fun endLoading(){
        MainScope().launch(Dispatchers.IO){
            loadingJob?.cancel()
            loadingJob?.join()
            loadingJob = null
            postInvalidate()
        }
    }

    fun startLoading(){
        progress = 0
        loadingJob = MainScope().launch {
            while (true){
                if(progress>6)
                    progress = 0
                postInvalidate()
                delay(500L)
                progress++
            }
        }
    }

    private fun initAttrSet(context: Context,attrs: AttributeSet?){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadProgressButton)

        textPaint.isAntiAlias = true
        selectedPointPaint.isAntiAlias = true
        unselectedPointPaint.isAntiAlias = true

        textPaint.color = typedArray.getColor(R.styleable.LoadProgressButton_android_textColor,context.getColor(R.color.white))
        textPaint.textSize = typedArray.getDimension(R.styleable.LoadProgressButton_android_textSize, 10F)
        textPaint.textAlign = Paint.Align.CENTER

        selectedPointPaint.color = typedArray.getColor(R.styleable.LoadProgressButton_point_color_selected,context.getColor(R.color.black))
        unselectedPointPaint.color = typedArray.getColor(R.styleable.LoadProgressButton_point_color_unselected,context.getColor(R.color.white))
        pointRadius = typedArray.getDimension(R.styleable.LoadProgressButton_point_radius,10f)
        pointMargin = typedArray.getDimension(R.styleable.LoadProgressButton_point_margin,10f)

        typedArray.recycle()
    }

    private fun getTextBaseLineY(): Float {
        val fontMetrics = textPaint.fontMetrics
        return height / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(loadingJob!=null){
            drawProgress(canvas)
        }
        else{
            canvas?.drawText(text,width/2f,getTextBaseLineY(),textPaint)
        }

    }

    private fun drawProgress(canvas: Canvas?){
        when(progress){
            0 -> {
                drawOval(canvas,point1X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point2X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point3X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point4X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point5X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
            }
            1 -> {
                drawOval(canvas,point1X,yCenter.toFloat(),pointRadius,selectedPointPaint)
                drawOval(canvas,point2X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point3X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point4X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point5X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
            }
            2 -> {
                drawOval(canvas,point1X,yCenter.toFloat(),pointRadius,selectedPointPaint)
                drawOval(canvas,point2X,yCenter.toFloat(),pointRadius,selectedPointPaint)
                drawOval(canvas,point3X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point4X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point5X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
            }
            3 -> {
                drawOval(canvas,point1X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point2X,yCenter.toFloat(),pointRadius,selectedPointPaint)
                drawOval(canvas,point3X,yCenter.toFloat(),pointRadius,selectedPointPaint)
                drawOval(canvas,point4X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point5X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
            }
            4 -> {
                drawOval(canvas,point1X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point2X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point3X,yCenter.toFloat(),pointRadius,selectedPointPaint)
                drawOval(canvas,point4X,yCenter.toFloat(),pointRadius,selectedPointPaint)
                drawOval(canvas,point5X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
            }
            5 -> {
                drawOval(canvas,point1X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point2X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point3X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point4X,yCenter.toFloat(),pointRadius,selectedPointPaint)
                drawOval(canvas,point5X,yCenter.toFloat(),pointRadius,selectedPointPaint)
            }
            6 -> {
                drawOval(canvas,point1X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point2X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point3X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point4X,yCenter.toFloat(),pointRadius,unselectedPointPaint)
                drawOval(canvas,point5X,yCenter.toFloat(),pointRadius,selectedPointPaint)
            }
        }
    }

    private fun drawOval(canvas: Canvas?,xCenter : Float,yCenter : Float,radius : Float,paint: Paint){
        canvas?.drawOval(xCenter - radius,yCenter - radius,xCenter + radius,yCenter + radius,paint)
    }
}