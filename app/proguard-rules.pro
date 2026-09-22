# Stack traces legibles en Play Vitals
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# kotlinx-serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.burixer85.aipedia.**$$serializer { *; }
-keepclassmembers class com.burixer85.aipedia.** {
    *** Companion;
}
-keepclasseswithmembers class com.burixer85.aipedia.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor
-keepclassmembers class io.ktor.** {
    volatile <fields>;
}
-dontwarn org.slf4j.**
-dontwarn io.ktor.network.sockets.**
