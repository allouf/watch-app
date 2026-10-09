package com.incentives.prototype

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import android.graphics.Rect




class GameView(context: Context?, sprite: String) : SurfaceView(context), Runnable
{
    var gameThread: Thread? = null
    var ourHolder: SurfaceHolder
    @Volatile
    var playing: Boolean
    var canvas: Canvas? = null
    var paint: Paint
    var fps: Long = 0
    private var timeThisFrame: Long = 0
    var bitmapBob: Bitmap
    var background: Bitmap= BitmapFactory.decodeResource(this.resources, R.drawable.world_map_01)
    var isMoving = false
    //var walkSpeedPerSecond = 250f
    //var passedSprite:String = sprite
    var bobXPosition = 0f
    private var lastFrameChangeTime: Long = 0
    private val frameLengthInMilliseconds = 100
    private var frameWidth = 350
    private var frameHeight = 350
    private var frameCount = 10
    private var currentFrame = 0

    //var renderRow = 0
    //var renderColumn = 0
    //var spriteWidth = 350
    //var spriteHeight = 350
    //var framesPerRow = 7
    var spriteSheetRows = 1

    val sprites = arrayOf(R.drawable.image2,R.drawable.image1, R.drawable.idle,R.drawable.nezuko,
        R.drawable.neptune,R.drawable.miku,R.drawable.me, R.drawable.earth,R.drawable.people,R.drawable.boyspritesheet)
    val spritesFrameCount = arrayOf(4,6, 10,1, 1,1,1,1,1,6)

    var currentSprite = 0

    private var counter = 0

    public var isInList:Boolean = false

    fun setIsInList(tmp : Boolean){
        isInList = tmp
    }
    fun getCurrentFrame() {
        if(spriteSheetRows == 1) {
            val time = System.currentTimeMillis()
            if (isMoving) { // Somente anima se Bob está se movendo
                if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
                    lastFrameChangeTime = time
                    currentFrame++
                    if (currentFrame >= frameCount) {
                        currentFrame = 0
                    }
                }
            }
            // atualiza os valores esquerdo e direito da fonte do próximo quadro na folha de sprites
            frameToDraw.left = currentFrame * frameWidth
            frameToDraw.right = frameToDraw.left + frameWidth
        }
        /*else{
            val time = System.currentTimeMillis()
            if (isMoving) { // Somente anima se Bob está se movendo
                if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
                    lastFrameChangeTime = time
                    currentFrame = ++currentFrame


                    if (renderColumn == framesPerRow - 1 ) {
                        renderRow = ++renderRow % spriteSheetRows
                    }
                    renderColumn = ++renderColumn % framesPerRow
                }
            }
            val srcFrame = Rect()
            val srcX = renderColumn * spriteWidth
            val srcY = renderRow * spriteHeight
            srcFrame.left = srcX.toInt()
            srcFrame.top = srcY.toInt()
            srcFrame.right = (srcX + spriteWidth).toInt()
            srcFrame.bottom = (srcY + spriteHeight).toInt()
            frameToDraw.set(srcFrame)

        }*/
    }

    private val frameToDraw = Rect(0, 0, frameWidth, frameHeight)

    var whereToDraw = RectF(bobXPosition, 0F, bobXPosition + frameWidth, frameHeight.toFloat())

    override fun run() {
        while (playing) {

            // Captura a hora atual em milissegundos no startFrameTime
            val startFrameTime = System.currentTimeMillis()

            // Atualiza o quadro
            update()

            // Desenha o quadro
            draw()

            // Calcule o fps neste quadro
            // Podemos então usar o resultado para tempo de animações e muito mais.
            timeThisFrame = System.currentTimeMillis() - startFrameTime
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame
            }
        }
    }

    fun update() {
        if (isMoving) {
            //bobXPosition = bobXPosition;
            //bobXPosition = bobXPosition + (walkSpeedPerSecond / fps);
        }
    }

    fun draw() {
        if (ourHolder.surface.isValid) {
            canvas = ourHolder.lockCanvas()
            //if(passedSprite.equals("girl") || passedSprite.equals("kh"))
                //canvas?.drawColor(Color.TRANSPARENT)
            //if(passedSprite.equals("coin"))
                canvas?.drawColor(Color.BLACK)
                paint.color = Color.argb(255, 249, 129, 0)
            paint.textSize = 45f
            //canvas?.drawText("FPS:$fps", 20f, 40f, paint)

            // Desenha bob at bobXPosition, 200 pixels
            //canvas.drawBitmap(bitmapBob, bobXPosition, 200, paint);
            whereToDraw.set(
                bobXPosition, 0f, (
                        bobXPosition.toInt() + frameWidth).toFloat(),
                frameHeight.toFloat()
            )
            getCurrentFrame()
            if(!isInList){
                canvas?.drawBitmap(
                    background, Rect(0, 0, background.width, background.height),
                    Rect(0, 0, canvas!!.getWidth(), canvas!!.getHeight()), null
                )
            }

            canvas?.drawBitmap (
                bitmapBob,
                frameToDraw,
                whereToDraw, paint
            )
            ourHolder.unlockCanvasAndPost(canvas)
        }
    }

    fun pause() {
        playing = false
        try {
            gameThread!!.join()
        } catch (e: InterruptedException) {
            Log.e("Error:", "joining thread")
        }
    }

    fun resume() {
        playing = true
        gameThread = Thread(this)
        gameThread!!.start()
    }

    fun changeSprite(){
        if(isInList){
            if(currentSprite == sprites.size -1){
                currentSprite = 0
            }else
                currentSprite+=1
            frameCount = spritesFrameCount[currentSprite]
            bitmapBob = BitmapFactory.decodeResource(this.resources, sprites[currentSprite])

            bitmapBob = Bitmap.createScaledBitmap(bitmapBob, frameWidth * frameCount, frameHeight, false)
        }
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN ->
                // Configura isMoving, para que Bob se mova
                isMoving = true
            MotionEvent.ACTION_UP -> {
                if(counter<=7){
                    isMoving = false
                    //this is just onClick, handle it(10 is example, try different numbers)
                }else{
                    changeSprite()
                }
                counter = 0;
                // Configura isMoving para que Bob não se mova

            }
            MotionEvent.ACTION_MOVE -> {
                counter++; //check how long the button is pressed
                if(counter>7){
                    //changeSprite()
                }


                //changeSprite()
            }
        }
        return true
    }

    init {
        ourHolder = holder
        paint = Paint()
        when(sprite){
            /*"idle"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.idle)
                frameCount = 10

                spriteSheetRows = 1
            }*/
            /*"kh"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.kh)
                frameCount = 4

                spriteSheetRows = 1
            }*/
            /*"robot"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.robot)
                frameCount = 32

                spriteSheetRows = 4
                framesPerRow = 8
                spriteWidth = bitmapBob.getWidth() / framesPerRow   // cut the sheet into pieces based on

                spriteHeight = bitmapBob.getHeight()/spriteSheetRows
            }*/
            "kh"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.image2)
                frameCount = 4

                //spriteSheetRows = 1
                //framesPerRow = 4
                //spriteWidth = bitmapBob.getWidth() / framesPerRow   // cut the sheet into pieces based on

                //spriteHeight = bitmapBob.getHeight()/spriteSheetRows

                frameWidth = 350
                frameHeight = 350
                currentSprite = 0
                bobXPosition = 0f
            }
            "coin"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.image1)
                frameCount = 6

                //spriteSheetRows = 1
                //framesPerRow = 6
                //spriteWidth = bitmapBob.getWidth() / framesPerRow   // cut the sheet into pieces based on

                //spriteHeight = bitmapBob.getHeight()/spriteSheetRows
                frameWidth = 50
                frameHeight = 100
                currentSprite = 1
                bobXPosition = 0f
            }
            "idle"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.idle)
                frameCount = 10
                frameWidth = 350
                frameHeight = 350
                //spriteSheetRows = 1
                currentSprite = 2
                bobXPosition = 0f
            }
            "nezuko"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.nezuko)
                frameCount = 1
                frameWidth = 350
                frameHeight = 350
                //spriteSheetRows = 1
                currentSprite = 3
                bobXPosition = 0f
            }
            "neptune"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.neptune)
                frameCount = 1
                frameWidth = 350
                frameHeight = 350
                //spriteSheetRows = 1
                currentSprite = 4
                bobXPosition = 0f
            }
            "miku"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.miku)
                frameCount = 1
                frameWidth = 350
                frameHeight = 350
                //spriteSheetRows = 1
                currentSprite = 5
                bobXPosition = 0f
            }
            "You"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.me)
                frameCount = 1
                frameWidth = 350
                frameHeight = 350
                //spriteSheetRows = 1
                currentSprite = 6
                bobXPosition = 0f
            }
            "World"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.earth)
                frameCount = 1
                frameWidth = 350
                frameHeight = 350
                //spriteSheetRows = 1
                currentSprite = 7
                bobXPosition = 0f
            }
            "Others"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.people)
                frameCount = 1
                frameWidth = 350
                frameHeight = 350
                //spriteSheetRows = 1
                currentSprite = 8
                bobXPosition = 0f
            }
            "Boy"->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.boyspritesheet)
                frameCount = 6
                frameWidth = 350
                frameHeight = 350
                //spriteSheetRows = 1
                currentSprite = 9
                bobXPosition = 40f
            }
            /*"girl"->{
                bitmapBob = ContextCompat.getDrawable(this.context, R.drawable.girl)?.toBitmap()!!
                //bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.image1)
                frameCount = 63

                spriteSheetRows = 9

                spriteWidth = bitmapBob.getWidth() / framesPerRow   // cut the sheet into pieces based on

                spriteHeight = bitmapBob.getHeight()/spriteSheetRows

                frameWidth = 350
                frameHeight = 350
            }*/
            else ->{
                bitmapBob = BitmapFactory.decodeResource(this.resources, R.drawable.image2)
                frameCount = 10

                spriteSheetRows = 1

                //framesPerRow = 10

                //spriteWidth = bitmapBob.getWidth() / framesPerRow   // cut the sheet into pieces based on

                //spriteHeight = bitmapBob.getHeight()/spriteSheetRows
                currentSprite = 0
            }
        }

        if(spriteSheetRows == 1)
            bitmapBob = Bitmap.createScaledBitmap(bitmapBob, frameWidth * frameCount, frameHeight, false)
        /*else
            bitmapBob = Bitmap.createScaledBitmap(bitmapBob, spriteWidth * framesPerRow, spriteHeight * spriteSheetRows, false)
*/
        playing = true

        //setZOrderOnTop(true)
        setWillNotDraw(false) //this line is very important!
    }
}