package com.movtery.cubernlauncher.utils.anim

import com.movtery.anim.AnimPlayer

interface SlideAnimation {
    fun slideIn(animPlayer: AnimPlayer)
    fun slideOut(animPlayer: AnimPlayer)
}