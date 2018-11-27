package io.github.mthli.knife;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import static android.content.ContentValues.TAG;

public class KnifeImageGetter implements ImageGetter {

    private final TextView textView;
    private final Drawable placeholder;


    KnifeImageGetter(TextView textView, Drawable placeholder) {
        this.textView = textView;
        this.placeholder = placeholder;
    }


    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();
        d.addLevel(0,0, placeholder);
        d.setBounds(0,0, placeholder.getIntrinsicWidth(), placeholder.getIntrinsicHeight());
        new LoadImage().execute(source, d);
        return d;
    }

    @SuppressLint("StaticFieldLeak")
    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            Log.d(TAG, "doInBackground " + source);


            try {
                return Picasso.get().load(source).get();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

            @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "onPostExecute drawable " + mDrawable);
            Log.d(TAG, "onPostExecute bitmap " + bitmap);
            if (bitmap != null) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int MAX_WIDTH = 700;
                int MAX_HEIGHT = 1000;
                if (width > MAX_WIDTH || height > MAX_HEIGHT) {
                    int scale = width / MAX_WIDTH;
                    bitmap = getResizedBitmap(bitmap, MAX_WIDTH, height / scale);
                }
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setLevel(1);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());

                // refresh text view now that the image is loaded
                CharSequence t = textView.getText();
                textView.setText(t);
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
