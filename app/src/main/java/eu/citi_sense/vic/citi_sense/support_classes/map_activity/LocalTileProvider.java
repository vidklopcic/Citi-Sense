package eu.citi_sense.vic.citi_sense.support_classes.map_activity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LocalTileProvider implements TileProvider {
    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    private static final int BUFFER_SIZE = 16 * 1024;

    private String currentPollutant = "CO";
    private Paint opacity = new Paint();
    private AssetManager mAssets;

    public LocalTileProvider(AssetManager assets) {
        mAssets = assets;
        opacity.setAlpha(255);
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        y = fixYCoordinate(y, zoom);
        return readTileImage(x, y, zoom);
    }

    private Tile readTileImage(int x, int y, int zoom) {
        InputStream in = null;
        ByteArrayOutputStream buffer = null;

        try {
            in = mAssets.open(getTileFilename(x, y, zoom));
            Bitmap image = BitmapFactory.decodeStream(in);
            image = adjustOpacity(image);

            buffer = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, buffer);

            byte[] byteArray = buffer.toByteArray();

            return new Tile(256, 256, byteArray);
        } catch (IOException e) {
            Log.d("asdfg", getTileFilename(x, y, zoom));
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) try { in.close(); } catch (Exception ignored) {}
            if (buffer != null) try { buffer.close(); } catch (Exception ignored) {}
        }
    }

    private Bitmap adjustOpacity(Bitmap bitmap)
    {
        Bitmap adjustedBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(adjustedBitmap);
        canvas.drawBitmap(bitmap, 0, 0, opacity);

        return adjustedBitmap;
    }

    private int fixYCoordinate(int y, int zoom) {
        int size = 1 << zoom; // size = 2^zoom
        return size - 1 - y;
    }

    public void setCurrentPollutant(String pollutant) {
        currentPollutant = pollutant;
    }

    public String getCurrentPollutant() {
        return currentPollutant;
    }

    private String getTileFilename(int x, int y, int zoom) {
        return currentPollutant+"/" + zoom + '/' + x + '/' + y + ".png";
    }
}