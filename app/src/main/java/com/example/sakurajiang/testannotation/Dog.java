package com.example.sakurajiang.testannotation;

import com.example.annotationinterface.MyInterFace;

@MyInterFace(
        id = "dog",
        type = Animal.class
)
public class Dog implements Animal {
    @Override
    public String getName() {
        return "Dog";
    }
}
