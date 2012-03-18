
package ssis.ecc;

public interface ECCEncoder extends ECC{

    public boolean[] encode(String msg);

    public boolean[] encode(boolean[] bitMsg);
}
