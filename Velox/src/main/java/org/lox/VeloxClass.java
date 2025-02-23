package org.lox;
import java.nio.charset.StandardCharsets;
import java.util.* ;
public class VeloxClass implements VeloxCallable {
    final String name;
    private final Map<String , VeloxFunction> methods;
    VeloxClass(String name , Map<String , VeloxFunction> methods) {
        this.name = name;
        this.methods = methods ;
    }
    @Override
    public Object call(Interpreter interpreter , List<Object> arguments) {
        VeloxInstance instance = new VeloxInstance(this) ;
        VeloxFunction initializer = findMethod("init") ;
        if(initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance ;
    }
    VeloxFunction findMethod(String methodName) {
        if(methods.containsKey(methodName)) {
            return methods.get(methodName) ;
        }
        return null ;
    }
    @Override
    public int arity() {
        VeloxFunction initializer = findMethod("init") ;
        if(initializer == null) {
            return 0 ;
        }
        return initializer.arity();
    }
    @Override
    public String toString() {
        return name ;
    }
}
