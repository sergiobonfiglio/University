package eccezioni;

public class MessageNotFoundException extends Exception{
    
    public MessageNotFoundException(){
        this("Messaggio non trovato");
    }
    
    public MessageNotFoundException(String str){
        super(str);
    }
    
}