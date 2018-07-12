package com.example.annotationprocess;

import com.example.annotationinterface.MyInterFace;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class MyAnnotationProcess extends AbstractProcessor{
    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;
    private Filer mFiler;
    private Map<String, FactoryGroupedClasses> factoryClasses =
            new LinkedHashMap<String, FactoryGroupedClasses>();
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<String>();
        annotations.add(MyInterFace.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(MyInterFace.class)) {
                if (element.getKind() != ElementKind.CLASS) {
                    throw new ProcessingException(element, "this is not class", MyInterFace.class.getSimpleName());
                }
                FactoryAnnotatedClass factoryAnnotatedClass = new FactoryAnnotatedClass((TypeElement) element);
                FactoryGroupedClasses factoryGroupedClasses = factoryClasses.get(factoryAnnotatedClass.getQualifiedGroupName());
                if(factoryGroupedClasses==null){
                    factoryGroupedClasses = new FactoryGroupedClasses(factoryAnnotatedClass.getQualifiedGroupName());
                    factoryClasses.put(factoryAnnotatedClass.getQualifiedGroupName(),factoryGroupedClasses);
                }
                factoryGroupedClasses.add(factoryAnnotatedClass);
            }
            for(FactoryGroupedClasses factoryGroupedClasses : factoryClasses.values()){
                factoryGroupedClasses.generateCode(elementUtils,mFiler);
            }
            factoryClasses.clear();
        }catch (ProcessingException e){
            messager.printMessage(Diagnostic.Kind.ERROR,e.getMessage(),e.getElement());
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR,e.getMessage());
        }
        return true;
    }
}
