import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 请求权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
        }

        // 获取音乐文件
        val musicFiles = getMusicFiles(this)

        setContent {
            MusicApp(musicFiles)
        }
    }

    // 获取音乐文件的函数，和之前的类似
    private fun getMusicFiles(context: Context): List<String> {
        val musicList = mutableListOf<String>()

        val projection = arrayOf(
            MediaStore.Audio.Media.DATA, 
            MediaStore.Audio.Media.DISPLAY_NAME, 
            MediaStore.Audio.Media.DURATION 
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        cursor?.use {
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val durationIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val path = it.getString(dataIndex)
                val name = it.getString(nameIndex)
                val duration = it.getLong(durationIndex)

                if (duration > 0) {
                    musicList.add("$name - ${duration / 1000}s")
                }
            }
        }

        return musicList
    }
}

@Composable
fun MusicApp(musicFiles: List<String>) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        MusicList(musicFiles)
    }
}

@Composable
fun MusicList(musicFiles: List<String>) {
    LazyColumn {
        items(musicFiles) { music ->
            Text(text = music)
        }
    }
}
