package helper

import android.content.Context
import android.os.Environment
import java.io.File

class HelperFile {

    companion object {
        fun getFileDir(): String {

//            if(!isSdCardPresent())
//                return File(context.getFilesDir(), "CameraTech")

            return Environment.getExternalStorageDirectory().absolutePath + File.separator + "CameraTech" + File.separator
        }

        fun isSdCardPresent() =
            Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)

    }
}