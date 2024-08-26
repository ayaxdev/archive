package net.jezevcik.argon.event.impl;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class RenderTextEvent {

    public Object text;

    public RenderTextEvent(OrderedText text) {
        this.text = text;
    }

    public RenderTextEvent(String text) {
        this.text = text;
    }
}
