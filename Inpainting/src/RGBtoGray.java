import java.awt.image.*;


public class RGBtoGray
{
    int [][] MatrixGray;
    int w;
    int h;
    int r;
    int g;
    int b;
    int colore;
    int graycolor;
    
    public RGBtoGray(BufferedImage imageRGB)
    {
       w=imageRGB.getWidth();
       h=imageRGB.getHeight();
       //Creazione Matrici  
        MatrixGray=new int[w][h];
        
      //Inizializzazione Matrici
        for(int y=0;y<h;y++)
          for (int x=0;x<w;x++)
            {
             colore=imageRGB.getRGB(x,y);
             r=((colore & 0x00FF0000)>>16);
             g=((colore & 0x0000FF00)>>8);
             b=(colore & 0x000000FF);
             graycolor=(int) ((0.29*r)+(0.58*g)+(0.11*b));
             MatrixGray[x][y]=r;//graycolor;
            }
      
    }
    //Ritorno la Matrice a scala di grigi
    public int[][] getMatriceGray()
    {
        return MatrixGray;
    }
  //Ritorno la Matrice a scala di grigi
    public void setMatriceGray(int x,int y,int color)
    {
        r=((color & 0x00FF0000)>>16);
        g=((color & 0x0000FF00)>>8);
        b=(color & 0x000000FF);
        graycolor=(int) ((0.3*r)+(0.6*g)+(0.1*b));
        MatrixGray[x][y]=r;//graycolor;
    }
    //Stampa le Matrici delle componenti
    public void print()//Test 
    {
        System.out.println("-------------Matrix Rossa--------------");
        for(int y=0;y<h;y++)
        {
         System.out.println(" ") ; 
          for (int x=0;x<w;x++)   
             System.out.print("*"+MatrixGray[x][y]) ;  
        }
    }
}