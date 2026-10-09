package com.incentives.prototype.minigameT1

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.wear.widget.WearableLinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.incentives.prototype.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Math.min
import kotlin.random.Random

//import com.squareup.picasso.Picasso


class RewardFragment : Fragment() {

    lateinit var minigameT1Activity:MinigameT1

    private lateinit var simpleProgressBar:ProgressBar
    private lateinit var txtViewLable:TextView
    private lateinit var txtViewLevel:TextView

    private lateinit var mainAnimationView: LottieAnimationView

    private lateinit var txtViewAvatarName:TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_minigame_t1_s1, container, false)
        minigameT1Activity = activity as MinigameT1


        txtViewAvatarName=view.findViewById(R.id.avatar_name)
        txtViewAvatarName.text = MyTimer.avatarProfile.Name

        val circularImageView = view.findViewById(R.id.avatar) as CircleImageView
        val filename = "avatar.png"
        try {
            val fileInputStream: FileInputStream = minigameT1Activity.openFileInput(filename)
            val bitmap = BitmapFactory.decodeStream(fileInputStream)
            fileInputStream.close()
            circularImageView.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            Log.e("WearablePart", "File not found: $filename, using default avatar", e)
            // Set a default avatar image from drawable resources when the specific file is not found
            circularImageView.setImageResource(R.drawable.avatar) // Ensure you have a default_avatar.png in your drawable resources
        } catch (e: IOException) {
            Log.e("WearablePart", "Error loading the image from internal storage", e)
            // Fallback to default avatar in case of other IO exceptions
            circularImageView.setImageResource(R.drawable.avatar)
        }


        val avatarProfile = MyTimer.avatarProfile


        simpleProgressBar = view.findViewById(R.id.simpleProgressBar) as ProgressBar
        txtViewLable=view.findViewById(R.id.label)
        txtViewLevel=view.findViewById(R.id.level)

        mainAnimationView = view.findViewById(R.id.mainAnimationView)
        mainAnimationView.visibility = View.VISIBLE
        mainAnimationView.repeatCount = LottieDrawable.INFINITE

        mainAnimationView.playAnimation()

        return view
    }

    fun setProgressMax(max: Int){
        this.simpleProgressBar.max = max
    }

    fun setProgress(pro: Int){
        this.simpleProgressBar.progress = pro
    }
}

