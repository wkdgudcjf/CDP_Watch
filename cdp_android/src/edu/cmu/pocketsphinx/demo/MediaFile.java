package edu.cmu.pocketsphinx.demo;

import java.io.File;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;

public class MediaFile {

    private final File file;
    private final ContentResolver contentResolver;
    private final Uri filesUri;
    private final Uri imagesUri;

    @SuppressLint("NewApi")
	public MediaFile(ContentResolver contentResolver, File file) {
        this.file = file;
        this.contentResolver = contentResolver;
        filesUri = MediaStore.Files.getContentUri("external");
        imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

	/**
     * Deletes the file. Returns true if the file has been successfully deleted or otherwise does not exist. This operation is not
     * recursive.
     */
    public boolean delete()
            throws IOException {
        if (!file.exists()) {
            return true;
        }

        boolean directory = file.isDirectory();
        if (directory) {
            // Verify directory does not contain any files/directories within it.
            String[] files = file.list();
            if (files != null && files.length > 0) {
                return false;
            }
        }

        String where = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[] { file.getAbsolutePath() };

        // Delete the entry from the media database. This will actually delete media files (images, audio, and video).
        contentResolver.delete(filesUri, where, selectionArgs);

        if (file.exists()) {
            // If the file is not a media file, create a new entry suggesting that this location is an image, even
            // though it is not.
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath());
            contentResolver.insert(imagesUri, values);

            // Delete the created entry, such that content provider will delete the file.
            contentResolver.delete(filesUri, where, selectionArgs);
        }

        return !file.exists();
    }
}