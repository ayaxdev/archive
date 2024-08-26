package net.jezevcik.argon.repository.params;

public record RepositoryParams<T>(boolean reflectClasses, boolean reflectFields, Object[] fieldReflectionObjects, Class<? extends T>[] parentType) {

    public static <A> RepositoryParamsBuilder<A> builder() {
        return new RepositoryParamsBuilder<>();
    }

    public static class RepositoryParamsBuilder<T> {
        private boolean reflectClasses, reflectFields;
        private Object[] fieldReflectionObjects;
        private Class<? extends T>[] parentType;

        public RepositoryParamsBuilder<T> reflectFields(boolean reflectFields) {
            this.reflectFields = reflectFields;
            return this;
        }

        public RepositoryParamsBuilder<T> reflectClasses(boolean reflectClasses) {
            this.reflectClasses = reflectClasses;
            return this;
        }

        public RepositoryParamsBuilder<T> fieldReflectionObjects(Object[] fieldReflectionObjects) {
            this.fieldReflectionObjects = fieldReflectionObjects;
            return this;
        }

        @SafeVarargs
        public final RepositoryParamsBuilder<T> parentType(Class<? extends T>... parentType) {
            this.parentType = parentType;
            return this;
        }

        public RepositoryParams<T> build() {
            return new RepositoryParams<>(reflectClasses, reflectFields, fieldReflectionObjects, parentType);
        }
    }

}
