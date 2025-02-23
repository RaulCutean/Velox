package org.lox;

import java.util.List;

public interface VeloxCallable {
    //ARITY DESCRIBES THE NUMBER OF ARGUMENTS A FUNCTION EXPECTS IF IT EXCEEDS IT WILL COMPILE BUT
    // WILL THROW A RUNTIMEERROR AND WON'T ANY JS BS :))
    int arity() ;
    Object call(Interpreter interpreter, List<Object> arguments);
}
