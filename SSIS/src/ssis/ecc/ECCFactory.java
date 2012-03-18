/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ssis.ecc;

import ssis.ecc.combined_H_RS.CombinedHRSDecoder;
import ssis.ecc.combined_H_RS.CombinedHRSEncoder;
import ssis.ecc.combined_H_RS.CombinedHammRS;
import ssis.ecc.hamming.HammingECCDecoder;
import ssis.ecc.hamming.HammingECCEncoder;
import ssis.ecc.reedSolomon.RsBinaryDecode;
import ssis.ecc.reedSolomon.RsBinaryEncode;

public abstract class ECCFactory implements ECCEncoder, ECCDecoder {

    public static ECCDecoder getDecoderInstance(int tipoECC) {
        ECCDecoder decoder = null;
        if (tipoECC == ECC.ECC_HAMMING) {
            decoder = new HammingECCDecoder();
        } else if (tipoECC == ECC.ECC_REED_SOLOMON) {
            decoder = new RsBinaryDecode();
        } else if (tipoECC == ECC_COMBINED) {
            decoder = new CombinedHRSDecoder();
        }
        return decoder;
    }

    public static ECCDecoder getDecoderInstance(int n, int m, int tipoECC) {
        ECCDecoder decoder = null;
        if (tipoECC == ECC.ECC_HAMMING) {
            decoder = new HammingECCDecoder(n, m);
        } else if (tipoECC == ECC.ECC_REED_SOLOMON) {
            decoder = new RsBinaryDecode(n, m);
        } else if (tipoECC == ECC_COMBINED) {
            decoder = new CombinedHRSDecoder(n, m);
        }
        return decoder;
    }

    public static ECCDecoder getDecoderInstance(int n, int m, int iterazioni, int tipoECC) {
        ECCDecoder decoder = null;
        if (tipoECC == ECC.ECC_HAMMING) {
            decoder = new HammingECCDecoder(n, m, iterazioni);
        } else if (tipoECC == ECC.ECC_REED_SOLOMON) {
            decoder = new RsBinaryDecode(n, m, iterazioni);
        } else if (tipoECC == ECC_COMBINED) {
            decoder = new CombinedHRSDecoder(n, m, iterazioni);
        }
        return decoder;
    }

    public static ECCEncoder getEncoderInstance(int tipoECC) {
        ECCEncoder encoder = null;
        if (tipoECC == ECC.ECC_HAMMING) {
            encoder = new HammingECCEncoder();
        } else if (tipoECC == ECC.ECC_REED_SOLOMON) {
            encoder = new RsBinaryEncode();
        } else if (tipoECC == ECC_COMBINED) {
            encoder = new CombinedHRSEncoder();
        }
        return encoder;
    }

    public static ECCEncoder getEncoderInstance(int n, int m, int tipoECC) {
        ECCEncoder encoder = null;
        if (tipoECC == ECC.ECC_HAMMING) {
            encoder = new HammingECCEncoder(n, m);
        } else if (tipoECC == ECC.ECC_REED_SOLOMON) {
            encoder = new RsBinaryEncode(n, m);
        } else if (tipoECC == ECC_COMBINED) {
            encoder = new CombinedHRSEncoder(n, m);
        }
        return encoder;
    }

    public static ECCEncoder getEncoderInstance(int n, int m, int iterazioni, int tipoECC) {
        ECCEncoder encoder = null;
        if (tipoECC == ECC.ECC_HAMMING) {
            encoder = new HammingECCEncoder(n, m, iterazioni);
        } else if (tipoECC == ECC.ECC_REED_SOLOMON) {
            encoder = new RsBinaryEncode(n, m, iterazioni);
        } else if (tipoECC == ECC_COMBINED) {
            encoder = new CombinedHRSEncoder(n, m, iterazioni);
        }
        return encoder;
    }
}
