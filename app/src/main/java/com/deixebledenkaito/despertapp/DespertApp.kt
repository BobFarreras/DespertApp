package com.deixebledenkaito.despertapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp  // 👈 Aquesta anotació és crucial
class DespertApp : Application()