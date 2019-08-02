package dagger.reflect;

import com.jakewharton.obelisk.Key;
import dagger.MembersInjector;
import dagger.android.AndroidInjector;
import java.lang.annotation.Annotation;
import java.util.Set;

final class ReflectiveAndroidInjector<T> implements AndroidInjector<T> {
  private final MembersInjector<T> membersInjector;

  private ReflectiveAndroidInjector(MembersInjector<T> membersInjector) {
    this.membersInjector = membersInjector;
  }

  @Override
  public void inject(T instance) {
    membersInjector.injectMembers(instance);
  }

  static final class Factory<T> implements AndroidInjector.Factory<T> {
    private final Scope parent;
    private final Class<?>[] moduleClasses;
    private final Class<T> instanceClass;
    private final Set<Annotation> annotations;

    Factory(
        Scope parent,
        Class<?>[] moduleClasses,
        Class<T> instanceClass,
        Set<Annotation> annotations) {
      this.parent = parent;
      this.moduleClasses = moduleClasses;
      this.instanceClass = instanceClass;
      this.annotations = annotations;
    }

    @Override
    public AndroidInjector<T> create(T instance) {
      Scope scope =
          ComponentScopeBuilder.create(moduleClasses, new Class<?>[0], annotations, parent)
              .get()
              .addInstance(Key.of(null, instanceClass), instance)
              .build();

      MembersInjector<T> membersInjector = ReflectiveMembersInjector.create(instanceClass, scope);
      return new ReflectiveAndroidInjector<>(membersInjector);
    }
  }
}
