package com.github.conisdaw.ignoreinput.model;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;

@Tag("ignore-input")
public class IgnoreInput {
    @Attribute
    public String id = "";

    @Attribute
    public String name = "";

    @Attribute
    public int slot;

    @Attribute
    public long createdAt;

    @Tag
    public String code = "";

    public IgnoreInput() {
    }

    public IgnoreInput(String id, String name, String code, int slot, long createdAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.slot = slot;
        this.createdAt = createdAt;
    }
}
