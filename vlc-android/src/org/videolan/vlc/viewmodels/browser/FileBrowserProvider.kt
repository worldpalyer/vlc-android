package org.videolan.vlc.viewmodels.browser

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.text.TextUtils
import org.videolan.libvlc.util.AndroidUtil
import org.videolan.medialibrary.media.DummyItem
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.medialibrary.media.MediaWrapper
import org.videolan.vlc.R
import org.videolan.vlc.VLCApplication
import org.videolan.vlc.util.AndroidDevices
import org.videolan.vlc.util.FileUtils
import java.io.File

open class FileBrowserProvider(url: String?, private val filePicker: Boolean = false, showHiddenFiles: Boolean) : BrowserProvider(url, showHiddenFiles) {

    override fun browseRoot() {
        val internalmemoryTitle = VLCApplication.getAppResources().getString(R.string.internal_memory)
        val browserStorage = VLCApplication.getAppResources().getString(R.string.browser_storages)
        val quickAccess = VLCApplication.getAppResources().getString(R.string.browser_quick_access)
        val storages = AndroidDevices.getMediaDirectories()
        val devices = mutableListOf<MediaLibraryItem>()
        if (!filePicker) {
            devices.add(DummyItem(browserStorage))
        }
        for (mediaDirLocation in storages) {
            if (!File(mediaDirLocation).exists()) continue
            val directory = MediaWrapper(AndroidUtil.PathToUri(mediaDirLocation))
            directory.type = MediaWrapper.TYPE_DIR
            if (TextUtils.equals(AndroidDevices.EXTERNAL_PUBLIC_DIRECTORY, mediaDirLocation))
                directory.setDisplayTitle(internalmemoryTitle)
            else {
                val deviceName = FileUtils.getStorageTag(directory.title)
                if (deviceName != null) directory.setDisplayTitle(deviceName)
            }
            devices.add(directory)
        }
        // Set folders shortcuts
        if (!filePicker) {
            devices.add(DummyItem(quickAccess))
        }
        if (AndroidDevices.MediaFolders.EXTERNAL_PUBLIC_MOVIES_DIRECTORY_FILE.exists()) {
            val movies = MediaWrapper(AndroidDevices.MediaFolders.EXTERNAL_PUBLIC_MOVIES_DIRECTORY_URI)
            movies.type = MediaWrapper.TYPE_DIR
            devices.add(movies)
        }
        if (!filePicker) {
            if (AndroidDevices.MediaFolders.EXTERNAL_PUBLIC_MUSIC_DIRECTORY_FILE.exists()) {
                val music = MediaWrapper(AndroidDevices.MediaFolders.EXTERNAL_PUBLIC_MUSIC_DIRECTORY_URI)
                music.type = MediaWrapper.TYPE_DIR
                devices.add(music)
            }
            if (AndroidDevices.MediaFolders.EXTERNAL_PUBLIC_PODCAST_DIRECTORY_FILE.exists()) {
                val podcasts = MediaWrapper(AndroidDevices.MediaFolders.EXTERNAL_PUBLIC_PODCAST_DIRECTORY_URI)
                podcasts.type = MediaWrapper.TYPE_DIR
                devices.add(podcasts)
            }
            if (AndroidDevices.MediaFolders.EXTERNAL_PUBLIC_DOWNLOAD_DIRECTORY_FILE.exists()) {
                val downloads = MediaWrapper(AndroidDevices.MediaFolders.EXTERNAL_PUBLIC_DOWNLOAD_DIRECTORY_URI)
                downloads.type = MediaWrapper.TYPE_DIR
                devices.add(downloads)
            }
            if (AndroidDevices.MediaFolders.WHATSAPP_VIDEOS_FILE.exists()) {
                val whatsapp = MediaWrapper(AndroidDevices.MediaFolders.WHATSAPP_VIDEOS_FILE_URI)
                whatsapp.type = MediaWrapper.TYPE_DIR
                devices.add(whatsapp)
            }
        }
        dataset.value = devices
    }

    class Factory(val url: String?, private val showHiddenFiles: Boolean): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return FileBrowserProvider(url, false, showHiddenFiles) as T
        }
    }
}