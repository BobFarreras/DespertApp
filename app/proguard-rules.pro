# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Evita que es ofusquin les classes implicades en la deserialització

# Manté totes les dades per a Gson i reflexió
# ------------------------
# Gson - mantenir models i anotacions
# ------------------------
-keepattributes Signature
-keepattributes *Annotation*
# Mantenir les classes de dades (model) que es serialitzen/deserialitzen amb Gson
-keep class com.deixebledenkaito.despertapp.** { *; }
# Mantenir camps amb @SerializedName (si utilitzes)
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# Manté l'ús de TypeToken
-keep class com.google.gson.reflect.TypeToken


# ------------------------
# Hilt i Inject
# ------------------------
# Mantenir anotacions i classes de Hilt i inject
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# ------------------------
# ViewModel (AndroidX)
# ------------------------
-keep class androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
# ------------------------
# AndroidX - evitar que se minifiqui classes essencials
# ------------------------
-keep class androidx.lifecycle.** { *; }
-keep class androidx.activity.** { *; }
-keep class androidx.fragment.app.** { *; }
# ------------------------
# Classes que poden ser invocades per reflexió
# ------------------------
-keepclassmembers class * {
    public <init>(android.content.Context);
    public <init>(android.app.Application);
}
# ------------------------
# Manté anotacions i signatures per Gson i Hilt
# ------------------------
-keepattributes *Annotation*

# ------------------------
# Logs (opcional: eliminar logs en release)
# ------------------------
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
}

# Cultura
-keep class com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.cultura.CulturaQuestion{ *; }
-keep class com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.cultura.CulturaQuestionSet { *; }

# Anime
-keep class com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.anime.AnimeQuestion { *; }
-keep class com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.anime.AnimeQuestionSet{ *; }

# Anglès
-keep class com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.angles.AnglesQuestion { *; }
-keep class com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.angles.AnglesQuestionSet { *; }

# Interfície base per totes les preguntes
-keep interface com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.ChallengeQuestion { *; }


# CustomAlarmSound (ja ho tens)
-keep class com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds.CustomAlarmSound { *; }