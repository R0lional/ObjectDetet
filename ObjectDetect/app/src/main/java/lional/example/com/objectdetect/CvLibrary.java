package lional.example.com.objectdetect;

/**
 * Created by lional on 2018/2/8.
 */

public class CvLibrary {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native double[] getData(int line);
}
