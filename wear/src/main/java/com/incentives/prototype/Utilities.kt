package com.incentives.prototype

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.incentives.prototype.minigameT1.MyTimer
import kotlinx.serialization.KSerializer
import kotlin.random.Random
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class Utilities {
    companion object{
        val ListOfReminderRelatedLayouts = arrayOf(R.layout.fragment_minigame_t1_r1,R.layout.fragment_minigame_t1_r2,R.layout.fragment_minigame_t1_r3,R.layout.fragment_minigame_t1_r4, R.layout.fragment_minigame_t1_r5, R.layout.fragment_minigame_t1_r6, R.layout.fragment_minigame_t1_r7, R.layout.fragment_minigame_t1_r8, R.layout.fragment_minigame_t1_r9, R.layout.fragment_minigame_t1_r10,  R.layout.fragment_minigame_t1_r11)
        //val ListOfReminderDurationSeconds= arrayOf(                               30,                                30,                              30,                                 30,                               30,30,30,30,30,30,30)
        val ListOfReminderRelatedLayoutsTexts = arrayOf("Instructions: Pick the person and memory you experienced gratefulness and explain 3 reasons why.",
            "Instructions: found gratefulness in your last 5 min. Pick 1 reason why.",
            "Instructions: Be grateful about yourself. Pick 3 reasons why.?",
            "Instructions: wish good to your environment. Pick the stranger.?",
            "Affirmations: Send love to yourself.accept yourself with your failures.?",
            "Instructions: Remember who your are. From where you came. What you trying to achieve. Appreciate that! ",
            "Instructions: Let go all toxicity from your mind. Imagine healing ball traveling through your body",
            "Instructions: Remember your childhood. Pick the memory. Be grateful for that. Visualise feel it.",
            "Instructions: Remember your teenager life. Pick the memory.Be grateful for that. Visualise feel it",
            "Instructions: who you wanted to be in childhood ? answer 2 reasons why Try to find commonality in your life.",
            "Instructions: send good energy to your family. Wish them luck health and happiness.")
        val ListOfReminderTextviewIds= arrayOf(R.string.txtViewDesc1,R.string.txtViewDesc2,R.string.txtViewDesc3,R.string.txtViewDesc4,R.string.txtViewDesc5,R.string.txtViewDesc6,R.string.txtViewDesc7,R.string.txtViewDesc8,R.string.txtViewDesc9,R.string.txtViewDesc10,R.string.txtViewDesc11)
        val ListOfScreensIds= arrayOf(R.string.screens1,R.string.screens2,R.string.screens3,R.string.screens4,R.string.screens5,R.string.screens6,R.string.screens7,R.string.screens8,R.string.screens9,R.string.screens10,R.string.screens11)
        val ListOfRemindersActive= arrayOf(R.string.rem1,R.string.rem2,R.string.rem3,R.string.rem4,R.string.rem5,R.string.rem6,R.string.rem7,R.string.rem8,R.string.rem9,R.string.rem10,R.string.rem11)
        val chests = arrayOf(
            arrayOf(R.drawable.closed0, R.drawable.open0, R.drawable.light0,R.drawable.coins0),
            arrayOf(R.drawable.closed1, R.drawable.open1, R.drawable.light1,R.drawable.coins1),
            arrayOf(R.drawable.closed2, R.drawable.open2, R.drawable.light2,R.drawable.coins2)
        )
        val ListOfSpritesNames = arrayOf("kh","idle","nezuko","neptune","miku","You","World","Others","Boy")

        fun GetListOfFeedbackRelatedLayouts(FeedbackTypeOrdinal:Int):Int{
            when(FeedbackTypeOrdinal){
                EnumFeebackType.FEEDBACK_GOOD.ordinal->{
                    val v = arrayOf(R.layout.fragment_minigame_t1_f1,R.layout.fragment_minigame_t1_f2)
                    return v[Random.nextInt(v.size)]
                }
                EnumFeebackType.FEEDBACK_BAD.ordinal->{
                    val v = arrayOf(R.layout.fragment_minigame_t1_f3,R.layout.fragment_minigame_t1_f4)
                    return v[Random.nextInt(v.size)]
                }
                else->
                    return R.layout.fragment_minigame_t1_f1//default value. Usually if the code is right, this line should not be reached.
            }
        }
        fun GetRandomIndexOfReminderLayouts():Int{
            return  Random.nextInt(ListOfReminderRelatedLayouts.size)
        }
        fun GetReminderLayoutGivenIndex(i:Int):Int{
            return ListOfReminderRelatedLayouts[i]
        }
        fun IsSecondaryTimerRunning(gameState:Int):Boolean{
            return (gameState == EnumStateType.SEC_TMR_RNG_SNOOZE_TO_ANS_QUES.ordinal ||
                    gameState == EnumStateType.SEC_TMR_RNG_SHWNG_RMNDR.ordinal||
                    gameState == EnumStateType.SEC_TMR_RNG_SHWNG_FDBCK.ordinal)
        }
        fun getRandomString(): String {
            val characterPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            return (1..10)
                .map { characterPool.random() }
                .joinToString("")
        }
    }
}

class ReminderTimes(context: Context, totalTimerDurationSecond: Int){
    var reminderTimePoints:IntArray
    var nextReminderPointer:Int=-1
    init {
        reminderTimePoints= IntArray(MyTimer.totalRemindersString)

        val gap = Math.floorDiv(totalTimerDurationSecond, reminderTimePoints.size + 1)
        for(i in this.reminderTimePoints.size downTo 1){
            this.reminderTimePoints[this.reminderTimePoints.size - i]=i*gap
        }

        nextReminderPointer=0
    }
    fun resetReminderTimes(){
        nextReminderPointer=0
    }
    fun isAllReminderDone():Boolean{
        return nextReminderPointer==-1 || nextReminderPointer>=reminderTimePoints.size
    }
}

class AlgosIds(context: Context) {
    private val gson = Gson()
    private val type: Type = object : TypeToken<ArrayList<UUID>>() {}.type
    var algosIdsList: ArrayList<UUID>

    init {
        val algosIdsString = MyTimer.settings.getString("algos_ids_list", null)
        algosIdsList = if (algosIdsString != null) {
            gson.fromJson(algosIdsString, type)
        } else {
            ArrayList()
        }
    }

    fun addId(id: UUID) {
        algosIdsList.add(id)
        saveIds()
    }

    fun removeId(id: UUID) {
        algosIdsList.remove(id)
        saveIds()
    }

    fun containsId(id: UUID): Boolean {
        return algosIdsList.contains(id)
    }

    // Changing the access level of saveIds to public
    public fun saveIds() {
        val algosIdsString = gson.toJson(algosIdsList, type)
        MyTimer.settings.edit().putString("algos_ids_list", algosIdsString).apply()
    }

    companion object {
        private var instance: AlgosIds? = null

        fun getInstance(context: Context): AlgosIds {
            if (instance == null) {
                instance = AlgosIds(context)
            }
            return instance!!
        }
    }
}

class Scores{
    var scoreCorrect:Int=0
    var scoreIncorrect:Int=0


    fun reset() {
        scoreCorrect=0
        scoreIncorrect=0
    }
}

enum class EnumAlarmTypes{
    UNINITIALIZED, MinigameT1,MinigameT2
}

enum class EnumStateType{
    //NOTE ADDING STATES THAT INDICATED INACTIVITY SHOULD ADDRESS EXISTING USAGE PROPERLY
    ALL_INACTIVE,
    PRMR_TMR_RNG_ONLY,
    ALL_TMR_PSD_WAITING_USR_INPUT_R_PRE, ALL_TMR_PSD_WAITING_USR_INPUT_F_PRE, ALL_TMR_PSD_TRANSITIONING
    ,SEC_TMR_RNG_SNOOZE_TO_ANS_QUES,SEC_TMR_RNG_SHWNG_RMNDR,SEC_TMR_RNG_SHWNG_FDBCK,
    PRMR_TMR_PSD,ENTRY_WAIT,EXIT_WAIT,ALL_TMR_PSD_WAITING_USR_INPUT_R_IDLE,SHWNG_RMNDR_PAGER,MENU_PAGER,ANSWER_MENU,RECORDER,GENERATE
}

enum class EnumFeebackType{
    FEEDBACK_GOOD,FEEDBACK_BAD
}

////////////////////////////////////

sealed  class NoteBase
{
    //abstract var Id: Int
    //abstract var Name: String
    abstract var Discription: String
}

@Serializable
data class NoteFile(
    var Description: String = "Default Text"
)

@Serializable
data class LayoutFile(
    //public override  var Id: Int = -1,
    //public override var Name: String= "",

    public var DisplayScreens: ArrayList<NoteFile>,
    public var DisableInList: Boolean = true,
    //public override var Discription: String= ""
)//:NoteBase()
{
    fun resetScreens() {
        if(DisplayScreens!=null){
            //val len = DisplayScreens.size
            while(DisplayScreens.size > 1){
                DisplayScreens.removeAt(1)
            }
            /*if(len > 1){
                for(i in 1 until len){
                    DisplayScreens.removeAt(1)
                }
            }*/
            DisplayScreens[0].Description = "Default Text"
        }
    }
}

@Serializable
data class AlgorithmFile(
    @Serializable(with = UUIDSerializer::class)
    var Id: UUID = UUID(0L, 0L),
    var Name: String = "",
    var Description: String = "",
    //var AIImageTex: ByteArray = ByteArray(0),
    var AIImageFilePath: String,
    //var LevelNo: List<Int> = arrayListOf(),
    var CurrentProgress: List<Int> = arrayListOf(),
    var CurrentAnalytics: List<Int> = arrayListOf(),
    var LayoutList: ArrayList<LayoutFile>
)

@Serializable
data class ProgressBarUnity(
    var ProgressName: String = "Default",
    var ProgressValue: Float = 0.0f,
    //@Transient var MaxProgressValue: Int = 180
)

@Serializable
data class AvatarProfile(
    var Name: String = "",
    var ProgressBars: ArrayList<ProgressBarUnity> = arrayListOf()
)


@Serializer(forClass = UUID::class)
object UUIDSerializer : KSerializer<UUID> {
    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }
}

data class HintNoteFileState(
    var isActive: Boolean = false,
    var oldHint: String = ""
)


data class ActionData(val action: String, val parameters: Map<String, Any?> = emptyMap())


class LoadingProgressViewModel: ViewModel() {
    val progressState: MutableLiveData<ProgressState> = MutableLiveData()

    // Function to update state
    fun updateAssetProcessingStatus(isLoading: Boolean, progress: Int, message: String, isSuccess: Boolean, isAvatar: Boolean) {
        val state = ProgressState(isLoading, progress, message, isSuccess, isAvatar)
        progressState.postValue(state) // Or .setValue(state) if already on the main thread
    }

    fun resetState() {
        // Reset the progressState or any other state to its initial condition
        progressState.value = ProgressState.initialState()
    }

    data class ProgressState(
        val isLoading: Boolean = false,
        val progress: Int = 0,
        val message: String = "Downloading",
        val isSuccess: Boolean = false,
        val isAvatar: Boolean = false
    ) {
        companion object {
            // Method to get an initial state
            fun initialState(): ProgressState = ProgressState()
        }
    }
}
