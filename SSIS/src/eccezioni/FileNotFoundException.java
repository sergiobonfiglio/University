package eccezioni;

public class FileNotFoundException extends Exception{
    
    public FileNotFoundException(){
        this("File non trovato");
    }
    
    public FileNotFoundException(String str){
        super(str);
    }
    
}
