# Xposed
-keepclassmembers class icu.nullptr.hidemyapplist.MyApp {
    boolean isHooked;
}

# Enum class
-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep,allowoptimization class * extends androidx.preference.PreferenceFragmentCompat
-keepclassmembers class com.google.android.hmal.databinding.**  {
    public <methods>;
}
