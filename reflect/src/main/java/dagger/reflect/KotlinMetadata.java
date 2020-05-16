package dagger.reflect;

import kotlin.Metadata;
import kotlinx.metadata.KmClass;
import kotlinx.metadata.jvm.KotlinClassHeader;
import kotlinx.metadata.jvm.KotlinClassMetadata;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class KotlinMetadata {
    private static final ConcurrentHashMap<Class<?>, Optional<KmClass>> cache = new ConcurrentHashMap<>();

    static Optional<KmClass> getForClass(final Class<?> cls) {
        if (cache.containsKey(cls)) {
            return cache.get(cls);
        }

        Optional<KmClass> metadata = readMetadata(cls);
        cache.put(cls, metadata);
        return metadata;
    }

    private static Optional<KmClass> readMetadata(final Class<?> cls) {
        Metadata annotation = cls.getAnnotation(Metadata.class);
        if (annotation == null) {
            return Optional.empty();
        }
        KotlinClassHeader header =
            new KotlinClassHeader(
                    annotation.k(),
                    annotation.mv(),
                    annotation.bv(),
                    annotation.d1(),
                    annotation.d2(),
                    annotation.xs(),
                    annotation.pn(),
                    annotation.xi()
            );
        KotlinClassMetadata metadata = KotlinClassMetadata.read(header);
        if (metadata == null) {
            // Should only happen on Kotlin < 1.0 (i.e. metadata version < 1.1)
            return Optional.empty();
        }
        if (metadata instanceof KotlinClassMetadata.Class) {
            return Optional.of(((KotlinClassMetadata.Class) metadata).toKmClass());
        } else {
            // Unsupported
            return Optional.empty();
        }
    }
}
