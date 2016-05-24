package wiwiwi.io.wearwithweather.network;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Kadir on 19.03.2016.
 */
public final class VolleyApplication {

    private static VolleyApplication instance = null;

    public static final VolleyApplication getInstance(){
        if (instance == null){
            instance = new VolleyApplication();
        }
        return instance;
    }

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private boolean initialized = false;

    private VolleyApplication(){

    }

    public void init(final Context context){
        if (initialized){
            return;
        }

        requestQueue = Volley.newRequestQueue(context);
        int memory = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024*1024*memory / 8;

        imageLoader =new ImageLoader(requestQueue, new BitmapLruCache(cacheSize));
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue == null){
            throw  new RuntimeException("Init first");
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader(){
        if (imageLoader == null){
            throw new RuntimeException("Init first");
        }
        return imageLoader;
    }

    //INNER Class for BitmapLruCaching..
    public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

        public BitmapLruCache(int maxSize){
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap value){
            return value.getRowBytes() * value.getHeight();
        }

        @Override
        public Bitmap getBitmap(String url){
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap){
            put(url, bitmap);
        }
    }

}
