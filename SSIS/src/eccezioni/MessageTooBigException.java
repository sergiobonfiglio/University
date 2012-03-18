package eccezioni;

public class MessageTooBigException extends Exception{
    
    public MessageTooBigException(){
        this("Messaggio troppo lungo");
    }
    
    public MessageTooBigException(String str){
        super(str);
    }
    
}
