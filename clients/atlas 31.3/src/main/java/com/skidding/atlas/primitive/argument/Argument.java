package com.skidding.atlas.primitive.argument;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Argument {
    public final String name;
    public final int index;
    public final Class<?> type;
}