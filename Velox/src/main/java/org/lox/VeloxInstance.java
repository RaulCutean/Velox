package org.lox;
import java.util.*;
public class VeloxInstance {
    private VeloxClass klass ;
    private final Map<String , Object> fields = new HashMap<>();
    VeloxInstance(VeloxClass klass) {
        this.klass = klass;
    }
    public Object get(Token name) {
        if(fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme) ;
        }
        VeloxFunction method = klass.findMethod(name.lexeme) ;
        if(method != null) {
            return method.bind(this);
        }
        throw new RuntimeError(name , "Undefined property '" + name.lexeme + "'.");
    }
    @Override
    public String toString() {
        return klass.name + " instance";
    }

    public void set(Token name , Object value) {
        fields.put(name.lexeme , value);
    }
}
