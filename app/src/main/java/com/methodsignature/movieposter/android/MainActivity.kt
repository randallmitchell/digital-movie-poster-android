package com.methodsignature.movieposter.android

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView

const val DISPLAY_DURATION = 60000L
const val ANIMATION_SPEED = 5000L

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    private val imageOne: ImageView by lazy { findViewById<ImageView>(R.id.image_one) }
    private val imageTwo: ImageView by lazy { findViewById<ImageView>(R.id.image_two) }

    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val posterDrawableResIds = listOf(R.drawable.poster_et, R.drawable.poster_jaws)

        imageOne.setImageResource(R.drawable.poster_et)

        fun queueNextTransition(currentImagePosition: Int, currentImageView: ImageView, nextImageView: ImageView) {
            handler.postDelayed(
                {
                    val nextImagePosition = if (currentImagePosition == posterDrawableResIds.size - 1) {
                        0
                    } else {
                        currentImagePosition + 1
                    }

                    val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in).apply {
                        duration = ANIMATION_SPEED
                        interpolator = LinearInterpolator()
                        setAnimationListener(
                            object: Animation.AnimationListener {
                                override fun onAnimationRepeat(animation: Animation?) {}
                                override fun onAnimationEnd(animation: Animation?) {}
                                override fun onAnimationStart(animation: Animation?) {
                                    nextImageView.alpha = 1.0f
                                    nextImageView.visibility = View.VISIBLE
                                }

                            }
                        )
                    }

                    val fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out).apply {
                        duration = ANIMATION_SPEED
                        interpolator = LinearInterpolator()
                        setAnimationListener(
                            object: Animation.AnimationListener {
                                override fun onAnimationRepeat(animation: Animation?) {}
                                override fun onAnimationStart(animation: Animation?) {}
                                override fun onAnimationEnd(animation: Animation?) {
                                    currentImageView.alpha = 0.0f
                                    currentImageView.visibility = View.GONE

                                    handler.removeCallbacksAndMessages(null)
                                    queueNextTransition(nextImagePosition, nextImageView, currentImageView)
                                }
                            }
                        )
                    }

                    nextImageView.setImageResource(posterDrawableResIds[nextImagePosition])
                    currentImageView.startAnimation(fadeOut)
                    nextImageView.startAnimation(fadeIn)
                },
                DISPLAY_DURATION
            )
        }

        queueNextTransition(0, imageOne, imageTwo)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
