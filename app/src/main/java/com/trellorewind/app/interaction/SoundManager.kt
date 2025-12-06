package com.trellorewind.app.interaction

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.trellorewind.app.R

class SoundManager(private val context: Context) {
    
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
    
    // Sound IDs loaded from raw resources
    private var cardPickupSoundId: Int = -1
    private var cardDragStartSoundId: Int = -1
    private var cardDropSoundId: Int = -1
    private var moveToDoneSoundId: Int = -1
    private var createCardSoundId: Int = -1
    private var createBoardSoundId: Int = -1
    
    var isEnabled: Boolean = true
    
    init {
        // Load all sound files from res/raw/
        try {
            cardPickupSoundId = soundPool.load(context, R.raw.card_pickup, 1)
            cardDragStartSoundId = soundPool.load(context, R.raw.card_drag_start, 1)
            cardDropSoundId = soundPool.load(context, R.raw.card_drop, 1)
            moveToDoneSoundId = soundPool.load(context, R.raw.move_to_done, 1)
            createCardSoundId = soundPool.load(context, R.raw.create_new_card, 1)
            createBoardSoundId = soundPool.load(context, R.raw.create_new_board, 1)
        } catch (e: Exception) {
            // Silently fail if sounds can't be loaded
            // App will continue to work without sounds
        }
    }
    
    /**
     * Play sound when picking up/starting to drag a card
     */
    fun playCardDrag() {
        if (!isEnabled || cardPickupSoundId == -1) return
        soundPool.play(cardPickupSoundId, 0.5f, 0.5f, 1, 0, 1.0f)
    }
    
    /**
     * Play sound when dropping a card (normal drop, not to DONE)
     */
    fun playCardDrop() {
        if (!isEnabled || cardDropSoundId == -1) return
        soundPool.play(cardDropSoundId, 0.6f, 0.6f, 1, 0, 1.0f)
    }
    
    /**
     * Play sound when moving a card to DONE (completion celebration)
     */
    fun playCardDone() {
        if (!isEnabled || moveToDoneSoundId == -1) return
        soundPool.play(moveToDoneSoundId, 0.7f, 0.7f, 1, 0, 1.0f)
    }
    
    /**
     * Play sound when creating a new card
     */
    fun playCardCreated() {
        if (!isEnabled || createCardSoundId == -1) return
        soundPool.play(createCardSoundId, 0.6f, 0.6f, 1, 0, 1.0f)
    }
    
    /**
     * Play sound when creating a new board
     */
    fun playBoardCreated() {
        if (!isEnabled || createBoardSoundId == -1) return
        soundPool.play(createBoardSoundId, 0.7f, 0.7f, 1, 0, 1.0f)
    }
    
    /**
     * Release sound resources
     */
    fun release() {
        soundPool.release()
    }
}

@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current
    return remember { SoundManager(context) }
}

