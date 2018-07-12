package com.example.annotationprocess;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

public class FactoryGroupedClasses {
    private static final String SUFFIX = "Factory";
    private String mQualifiedClassName;
    private Map<String, FactoryAnnotatedClass> itemsMap =
            new LinkedHashMap<String, FactoryAnnotatedClass>();
    public FactoryGroupedClasses(String qualifiedClassName){
        this.mQualifiedClassName = qualifiedClassName;
    }
    public void add(FactoryAnnotatedClass factoryAnnotatedClass) throws ProcessingException{
        if(itemsMap.get(factoryAnnotatedClass.getId())!=null){
            throw new ProcessingException(factoryAnnotatedClass.getTypeElement(),"is already exist");
        }
        itemsMap.put(factoryAnnotatedClass.getId(),factoryAnnotatedClass);
    }
    public void generateCode(Elements elementUtils, Filer filer) throws IOException {
        TypeElement superClassName = elementUtils.getTypeElement(mQualifiedClassName);
        String factoryClassName = superClassName.getSimpleName() + SUFFIX;
        PackageElement pkg = elementUtils.getPackageOf(superClassName);
        String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();

        MethodSpec.Builder method = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "id")
                .returns(TypeName.get(superClassName.asType()));

        // check if id is null
        method.beginControlFlow("if (id == null)")
                .addStatement("throw new IllegalArgumentException($S)", "id is null!")
                .endControlFlow();

        // Generate items map

        for (FactoryAnnotatedClass item : itemsMap.values()) {
            method.beginControlFlow("if ($S.equals(id))", item.getId())
                    .addStatement("return new $L()", item.getTypeElement().getQualifiedName().toString())
                    .endControlFlow();
        }

        method.addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ");

        TypeSpec typeSpec = TypeSpec.classBuilder(factoryClassName).addMethod(method.build()).build();

        // Write file
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
    }
}
