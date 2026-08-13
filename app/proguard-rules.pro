# Add project specific ProGuard rules here.
# Keep kotlinx.serialization generated serializers
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class * { @kotlinx.serialization.Serializable <fields>; }