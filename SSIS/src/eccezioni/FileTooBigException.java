package eccezioni;

public class FileTooBigException extends Exception{
    
    public FileTooBigException(){
        this("File troppo grande");
    }
    
    public FileTooBigException(String str){
        super(str);
    }
    
}
