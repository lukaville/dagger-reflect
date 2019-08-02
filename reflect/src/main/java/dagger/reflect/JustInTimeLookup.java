package dagger.reflect;

import com.jakewharton.obelisk.Key;
import java.lang.annotation.Annotation;
import org.jetbrains.annotations.Nullable;

final class JustInTimeLookup {
  final @Nullable Annotation scope;
  final Binding binding;

  JustInTimeLookup(@Nullable Annotation scope, Binding binding) {
    this.scope = scope;
    this.binding = binding;
  }

  interface Factory {
    @Nullable
    JustInTimeLookup create(Key key);

    Factory NONE = key -> null;
  }
}
