package dagger.reflect;

import kotlin.Metadata;
import kotlinx.metadata.KmClass;
import kotlinx.metadata.jvm.KotlinClassHeader;
import kotlinx.metadata.jvm.KotlinClassMetadata;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

class KotlinMetadata {
    private static final ConcurrentHashMap<Class<?>, KmClass> cache = new ConcurrentHashMap<>();

    @Nullable
    static KmClass getForClass(final Class<?> cls) {
        if (cache.containsKey(cls)) {
            return cache.get(cls);
        }

        KmClass metadata = readMetadata(cls);
        if (metadata != null) {
            cache.put(cls, metadata);
        }
        return metadata;
    }

    @Nullable
    private static KmClass readMetadata(final Class<?> cls) {
        Metadata annotation = cls.getAnnotation(Metadata.class);
        if (annotation == null) {
            return null;
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
            return null;
        }
        if (metadata instanceof KotlinClassMetadata.Class) {
            return ((KotlinClassMetadata.Class) metadata).toKmClass();
        } else {
            // Unsupported
            return null;
        }
    }
}
