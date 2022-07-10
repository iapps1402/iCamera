package ir.stackcode.videoproject.view

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import com.google.android.exoplayer2.MediaItem
import com.imherrera.videoplayer.VideoPlayer
import com.imherrera.videoplayer.VideoPlayerControl
import com.imherrera.videoplayer.rememberVideoPlayerState

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
}