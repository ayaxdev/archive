package com.atani.nextgen.processor;

import com.atani.nextgen.feature.Manager;

import java.lang.reflect.InvocationTargetException;

public class ProcessorManager extends Manager<Processor> {

    private static volatile ProcessorManager processorManager;

    public static ProcessorManager getSingleton() {
        return processorManager == null ? processorManager = new ProcessorManager() : processorManager;
    }

    public ProcessorManager() {
        super(Processor.class);
    }

    @Override
    public void postMinecraftLaunch() throws InvocationTargetException, NoSuchMethodException, InstantiationException {
        super.postMinecraftLaunch();

        for(Processor processor : getFeatures()) {
            processor.init();
        }
    }
    
}
