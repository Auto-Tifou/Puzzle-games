package mobapplication.pt_game.Utils;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2019/9/20 0020.
 */

public class ImagePiece {
    private int index;
    private Bitmap bitmap;

    public ImagePiece(){
        this.index = index;
        this.bitmap = bitmap;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "ImagePiece{" +
                "index=" + index +
                ", bitmap=" + bitmap +
                '}';
    }
}
