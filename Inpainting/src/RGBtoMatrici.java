import java.awt.image.*;



public class RGBtoMatrici
{
    int [][] MatrixR;
    int [][] MatrixG;
    int [][] MatrixB;
    BufferedImage imageR;
    BufferedImage imageG;
    BufferedImage imageB;
    int w;
    int h;
    
    public RGBtoMatrici(BufferedImage imageRGB)
    {
      int r;
      int g;
      int b;
      int colore;

       w=imageRGB.getWidth();
       h=imageRGB.getHeight();
       //Creazione Matrici  
        MatrixR=new int[w][h];
        MatrixG=new int[w][h];
        MatrixB=new int[w][h];
      //Immagini canale Rosso,Verde,Blu
        imageR=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        imageG=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        imageB=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        
      //Inizializzazione Matrici
        for(int y=0;y<h;y++)
          for (int x=0;x<w;x++)
            {
             colore=imageRGB.getRGB(x,y);
             r=((colore & 0x00FF0000)>>16);
             g=((colore & 0x0000FF00)>>8);
             b=(colore & 0x000000FF);
             //Scompongo l'immagine imageRGB nei suoi 3 Canali
             imageR.setRGB(x,y,((r<<16)|0xFF000000));
             imageG.setRGB(x,y,((g<<8)|0xFF000000));
             imageB.setRGB(x,y,((b)|0xFF000000));
             //Matrici delle Componenti
             MatrixR[x][y]=r;
             MatrixG[x][y]=g;
             MatrixB[x][y]=b;
            }
      
    }
    //Ritorno la Matrice del Rosso
    public int[][] getMatriceR()
    {
        return MatrixR;
    }
    //Ritorno la Matrice del Verde
    public int[][] getMatriceG()
    {
        return MatrixG;
    }
    //Ritorno la Matrice del Blue
    public int[][] getMatriceB()
    {
        return MatrixB;
    }
    //Ritorno BufferedImage del canale Rosso
    public BufferedImage getImageR()
    {
     return imageR;
    }
    //Ritorno BufferedImage del canale Verde
    public BufferedImage getImageG()
    {
     return imageG;
    }
    //Ritorno BufferedImage del canale Blue
    public BufferedImage getImageB()
    {
     return imageB;
    }
    //Stampa le Matrici delle componenti
    public void print()//Test 
    {
        System.out.println("-------------Matrix Rossa--------------");
        for(int y=0;y<h;y++)
        {
         System.out.println(" ") ; 
          for (int x=0;x<w;x++)   
             System.out.print("*"+MatrixR[x][y]) ;  
        }
        System.out.println(" ") ; 
        System.out.println("-------------Matrix Green--------------");
        for(int y=0;y<h;y++)
        {
         System.out.println(" ") ; 
          for (int x=0;x<w;x++)   
             System.out.print("*"+MatrixG[x][y]) ;  
        }
        System.out.println(" ") ; 
        System.out.println("-------------Matrix Blue--------------");
        for(int y=0;y<h;y++)
        {
         System.out.println(" ") ; 
          for (int x=0;x<w;x++)   
             System.out.print("*"+MatrixB[x][y]) ;  
        }
        
    }
}