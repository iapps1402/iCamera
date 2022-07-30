package ir.stackcode.videoproject.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import com.google.android.exoplayer2.MediaItem
import com.imherrera.videoplayer.VideoPlayer
import com.imherrera.videoplayer.VideoPlayerControl
import com.imherrera.videoplayer.rememberVideoPlayerState
import helper.HotKeys

class VideoPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val path = intent.getStringExtra("path")
        val fileUri = Uri.parse(path)

        setContent {
            val playerState = rememberVideoPlayerState()

            VideoPlayer(playerState = playerState) {
                VideoPlayerControl(
                    state = playerState,
                    title = "Elephant Dream",
                )
            }

            LaunchedEffect(Unit) {
                playerState.player.setMediaItem(MediaItem.fromUri(fileUri))
               // playerState.player.setMediaItem(MediaItem.fromUri("https://stacklearn.ir/storage/video/IqEa3HErUN06koi1O32vST3E3N9pAmni6Et5zEZq.mp4"))
                playerState.player.prepare()
                playerState.player.playWhenReady = true
            }
        }
    }

    private val mIntentFilter = IntentFilter("data_received")
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, intent: Intent) {
            val position = when (intent.getStringExtra("data_received")) {
                HotKeys.RECEIVE_OPEN_FIRST_CAMERA -> 0
                HotKeys.RECEIVE_OPEN_SECOND_CAMERA -> 1
                else -> -1
            }

            if(position != -1) {
                startActivity(Intent(this@VideoPlayerActivity, CameraActivity::class.java).apply {
                    putExtra("position", position)
                })
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, mIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }
}