package net.jezevcik.argon.repository;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.repository.params.RepositoryParams;
import net.jezevcik.argon.system.initialize.Initializable;
import net.jezevcik.argon.system.initialize.InitializeStage;
import net.jezevcik.argon.utils.reflection.ClassUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ElementRepository <T> extends ArrayList<T> implements Initializable {

    private final String name;
    private final RepositoryParams<T> repositoryParams;

    private final List<Class<? extends T>> foundClasses = new ArrayList<>();
    private final List<Field> foundFields = new ArrayList<>();

    public ElementRepository(String name, RepositoryParams<T> repositoryParams) {
        this.name = name;
        this.repositoryParams = repositoryParams;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(InitializeStage initializeStage) {
        if (initializeStage == InitializeStage.PRE_MINECRAFT) {
            if (repositoryParams.reflectClasses()) {
                for (Class<? extends T> klass : repositoryParams.parentType()) {
                    this.foundClasses.addAll(ParekClient.getInstance().reflections.getSubTypesOf(klass));
                }
            }

            if (repositoryParams.reflectFields()) {
                for (Object reflectionObject : repositoryParams.fieldReflectionObjects()) {
                    for (Field field : reflectionObject.getClass().getFields()) {
                        try {
                            final Object fieldObject = field.get(reflectionObject);

                            if (fieldObject.getClass().isInstance(repositoryParams.parentType()) && !fieldObject.getClass().equals(repositoryParams.parentType())) {
                                this.add((T) fieldObject);
                                foundFields.add(field);
                            }
                        } catch (Exception e) {
                            ParekClient.LOGGER.error("Failed to instantiate field {} in repository {}", field.getName(), name);
                        }
                    }
                }
            }

            ParekClient.LOGGER.info("Repository {} discovered {} classes and loaded {} fields", name, foundClasses.size(), foundFields.size());
        } else if (initializeStage == InitializeStage.POST_MINECRAFT) {
            this.foundClasses.forEach(klass -> {
                if (ClassUtils.hasParameterlessPublicConstructor(klass)) {
                    try {
                        this.add(klass.getDeclaredConstructor().newInstance());
                    } catch (Exception e) {
                        ParekClient.LOGGER.error("Failed to instantiate class {} in repository {}", klass.getName(), name, e);
                    }
                } else {
                    ParekClient.LOGGER.info("Skipping class {} in repository {} due to no valid constructor being present", klass.getName(), name);
                }
            });

            ParekClient.LOGGER.info("Repository {} loaded {} elements", name, size());
        }
    }

    @SuppressWarnings("unchecked")
    public <A extends T> A getClass(Class<A> klass) {
        return (A) this.stream().filter(clazz -> clazz.getClass().equals(klass)).findFirst().orElse(null);
    }

}
