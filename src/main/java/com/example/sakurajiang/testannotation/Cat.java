package com.example.sakurajiang.testannotation;

import com.example.annotationinterface.MyInterFace;

@MyInterFace(
        id = "cat",
        type = Animal.class
)
public class Cat implements Animal{
    @Override
    public String getName() {
        return "Cat";
    }
}
