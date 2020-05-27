package com.myrungo.speedball.utils

import android.graphics.SurfaceTexture
import android.view.TextureView

class CameraManager(private val textureView: TextureView) {

    private fun setupListener() {
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }
}