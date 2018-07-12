package com.example.annotationprocess;

import com.example.annotationinterface.MyInterFace;


import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

public class FactoryAnnotatedClass {
        private String mId;
        private Class mType;
        private TypeElement mTypeElement;
        private String mQualifiedGroupName;
        public FactoryAnnotatedClass(TypeElement typeElement){
            this.mTypeElement = typeElement;
            MyInterFace annotation = typeElement.getAnnotation(MyInterFace.class);
            mId = annotation.id();
            try {
                mType = annotation.type();
                mQualifiedGroupName = mType.getCanonicalName();
            }catch (MirroredTypeException mte){
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                mQualifiedGroupName = classTypeElement.getQualifiedName().toString();
            }

        }

    public String getId() {
        return mId;
    }

    public Class getType() {
        return mType;
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }

    public String getQualifiedGroupName() {
        return mQualifiedGroupName;
    }
}
