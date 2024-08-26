package lord.daniel.alexander.module.exceptions;

import lord.daniel.alexander.module.abstracts.AbstractModule;

/**
 * Written by Daniel. on 21/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class MissingAnnotationException extends RuntimeException {

    public MissingAnnotationException(Class<? extends AbstractModule> clazz) {
        super(String.format("Class %s is missing the CreateModule annotation!", clazz.getSimpleName()));
    }

}
