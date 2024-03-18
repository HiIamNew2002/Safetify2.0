package com.example.safetify203v

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.ViewSwitcher
import androidx.appcompat.app.AppCompatActivity

class Switcher : AppCompatActivity(){
    val images = arrayOf(R.drawable.ambulance,R.drawable.police,R.drawable.bang,R.drawable.bell)
    var counter = 0;
    val Alarmchc: ImageSwitcher = findViewById(R.id.Alarmchc)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_alarm)
        setForSwitching()
        val Right_button: ImageButton = findViewById(R.id.right_button)
        val Left_button: ImageButton = findViewById(R.id.left_button)

        Right_button.setOnClickListener {
            counter++
            if(counter==images.size) {
                counter=0
            }
            val imageForChange = images[counter]
            Alarmchc.setImageResource(imageForChange)
        }

        Left_button.setOnClickListener{
            counter--
            if(counter == -1){
                counter==images.size-1
            }
            val imageForChange = images[counter]
            Alarmchc.setImageResource(imageForChange)
        }
    }

    fun setForSwitching() {
        setFactory()
        setAnimations()
    }
    fun setFactory() {
        Alarmchc.setFactory(ViewSwitcher.ViewFactory {
            getImageView()

        })

    }

    fun getImageView(): ImageView {
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.setImageResource(R.drawable.ambulance)
        return imageView
    }

    fun setAnimations() {
        val animOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)
        val animIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)

        Alarmchc.outAnimation = animOut
        Alarmchc.inAnimation = animIn
    }
}//code for imageswitch