package ssis.ecc.reedSolomon;

/**
 * タイトル: RSコード・デコーダ
 *
 * @author Masayuki Miyazaki
 * http://sourceforge.jp/projects/reedsolomon/
 */
public class RsDecode {
	public static final int RS_PERM_ERROR = -1;
	public static final int RS_CORRECT_ERROR = -2;
	private static final Galois galois = Galois.getInstance();
	private int npar;

	public RsDecode(int npar) {
		this.npar = npar;
	}

	/**
	 * Modified Berlekamp-Massey
	 *
	 * @param sigma int[]
	 *		σ(z)格納用配列、最大npar/2 + 2個の領域が必要
	 * 		σ0,σ1,σ2, ... σ<jisu>
	 * @param omega int[]
	 *		ω(z)格納用配列、最大npar/2 + 1個の領域が必要
	 *		ω0,ω1,ω2, ... ω<jisu-1>
	 * @param syn int[]
	 *		シンドローム配列
	 * 		s0,s1,s2, ... s<npar-1>
	 * @return int
	 * 		>= 0: σの次数
	 * 		< 0: エラー
	 */
	public int calcSigmaMBM(int[] sigma, int[] omega, final int[] syn) {
		int[] sg0 = new int[npar];
		int[] sg1 = new int [npar];
		sg0[1] = 1;
		sg1[0] = 1;
		int jisu0 = 1;
		int jisu1 = 0;
		int m = -1;

		for(int n = 0; n < npar; n++) {
			// 判別式を計算
			int d = syn[n];
			for(int i = 1; i <= jisu1; i++) {
				d ^= galois.mul(sg1[i], syn[n - i]);
			}
			if(d != 0) {
				int logd = galois.toLog(d);
				int[] wk = new int[npar];
				for(int i = 0; i <= n; i++) {
					wk[i] = sg1[i] ^ galois.mulExp(sg0[i], logd);
				}
				int js = n - m;
				if(js > jisu1) {
					m = n - jisu1;
					jisu1 = js;
					if(jisu1 > npar / 2) {
						return -1;				// σの次数がnpar / 2を超えたらエラー
					}
					for(int i = 0; i <= jisu0; i++) {
						sg0[i] = galois.divExp(sg1[i], logd);
					}
					jisu0 = jisu1;
				}
				sg1 = wk;
			}
			System.arraycopy(sg0, 0, sg0, 1, Math.min(sg0.length - 1, jisu0));
			sg0[0] = 0;
			jisu0++;
		}
		galois.mulPoly(omega, sg1, syn);
		System.arraycopy(sg1, 0, sigma, 0, Math.min(sg1.length, sigma.length));
		return jisu1;
	}

	/**
	 * 最終エラー位置のセット
	 * @param pos
	 * 		誤り位置格納用初列
	 * @param n
	 * 		データ長
	 * @param last
	 * 		エラー位置
	 * @return
	 * 		0: 正常終了
	 * 		< 0: エラー
	 */
	private int setLastErrorPos(int[] pos, int n, int last) {
		if(galois.toLog(last) >= n) {
			return RS_CORRECT_ERROR;	// 範囲外なのでエラー
		}
		pos[0] = last;
		return 0;
	}

	/**
	 * チェン探索により誤り位置を求める
	 *		σ(z) = 0の解を探索する
	 *		ただし、探索はデータ長以内の解のみで
	 *		jisu個の解が見つからなければ、エラーとする
	 * @param pos int[]
	 * 		誤り位置格納用配列、jisu個の領域が必要
	 * @param n int
	 * 		データ長
	 * @param jisu int
	 * 		σの次数
	 * @param sigma int[]
	 * 		σ0,σ1,σ2, ... σ<jisu>
	 * @return int
	 *		0: 正常終了
	 *		< 0: エラー
	 */
	private int chienSearch(int[] pos, int n, int jisu, final int[] sigma) {
		/*
		 * σ(z) = (1-α^i*z)(1-α^j*z)(1-α^k*z)
		 *       = 1 + σ1z + σ2z^2 +...
		 * σ1 = α^i + α^j + α^k
		 * つまりσ1は全ての解の合計となっている。上記の性質を利用して、プチ最適化
		 * last = σ1から、見つけた解を次々と引いていくことにより、最後の解はlastとなる
		 */
		int last = sigma[1];
		if(jisu == 1) {
			// 次数が1ならばlastがその解である
			return setLastErrorPos(pos, n, last);
		}
		int posIdx = jisu - 1;		// 誤り位置格納用インデックス
		for(int i = 0; i < n; i++) {
			/*
			 * σ(z)の計算
			 * w を1(0乗の項)に初期化した後、残りの項<1..jisu>を加算
			 * z = 1/α^i = α^Iとすると
			 * σ(z) = 1 + σ1α^I + σ2(α^I)^2 + σ3(α^I)^3 + ... + σ<jisu>/(α^I)^<jisu>
			 *      = 1 + σ1α^I + σ2α^(I*2) + σ3α^(I*3) + ... + σ<jisu>α^(I*<jisu>)
			 */
			int z = 255 - i;					// z = 1/α^iのスカラー
			int wk = 1;
			for(int j = 1; j <= jisu; j++) {
				wk ^= galois.mulExp(sigma[j], (z * j) % 255);
			}
			if(wk == 0) {
				int pv = galois.toExp(i);		// σ(z) = 0の解
				last ^=  pv;					// lastから今見つかった解を引く
				pos[posIdx--] = pv;
				if(posIdx == 0) {
					// 残りが一つならば、lastがその解である
					return setLastErrorPos(pos, n, last);
				}
			}
		}
		// 探索によりデータ長以内に、jisu個の解が見つからなかった
		return RS_CORRECT_ERROR;
	}

	/**
	 * Forney法で誤り訂正を行う
	 *		σ(z) = (1-α^i*z)(1-α^j*z)(1-α^k*z)
	 *		σ'(z) = α^i * (1-α^j*z)(1-α^k*z)...
	 *			   + α^j * (1-α^i*z)(1-α^k*z)...
	 *			   + α^k * (1-α^i*z)(1-α^j*z)...
	 *		ω(z) = (E^i/(1-α^i*z) + E^j/(1-α^j*z) + ...) * σ(z)
	 *			  = E^i*(1-α^j*z)(1-α^k*z)...
	 *			  + E^j*(1-α^i*z)(1-α^k*z)...
	 *			  + E^k*(1-α^i*z)(1-α^j*z)...
	 *		∴ E^i = α^i * ω(z) / σ'(z)
	 * @param data int[]
	 *		入力データ配列
	 * @param length int
	 *		入力データ長さ
	 * @param jisu int
	 *		σの次数
	 * @param pos int[]
	 *		誤り位置配列
	 * @param sigma int[]
	 *		σ0,σ1,σ2, ... σ<jisu>
	 * @param omega int[]
	 *		ω0,ω1,ω2, ... ω<jisu-1>
	 */
	private void doForney(int[] data, int length, int jisu, int[] pos, int[] sigma, int[] omega) {
		for(int i = 0; i < jisu; i++) {
			int ps = pos[i];
			int zlog = 255 - galois.toLog(ps);					// zのスカラー

			// ω(z)の計算
			int ov = omega[0];
			for(int j = 1; j < jisu; j++) {
				ov ^= galois.mulExp(omega[j], (zlog * j) % 255);		// ov += ωi * z^j
			}

			// σ'(z)の値を計算(σ(z)の形式的微分)
			int dv = sigma[1];
			for(int j = 2; j < jisu; j += 2) {
				dv ^= galois.mulExp(sigma[j + 1], (zlog * j) % 255);	// dv += σ<j+1> * z^j
			}

			/*
			 * 誤り訂正 E^i = α^i * ω(z) / σ'(z)
			 * 誤り位置の範囲はチェン探索のときに保証されているので、
			 * ここではチェックしない
			 */
			data[galois.toPos(length, ps)] ^= galois.mul(ps, galois.div(ov, dv));
		}
	}

	/**
	 * RSコードのデコード
	 *
	 * @param data int[]
	 *		入力データ配列
	 * @param length int
	 * 		パリティを含めたデータ長
	 * @param noCorrect boolean
	 * 		チェックのみで訂正は行わない
	 * @return int
	 * 		0: エラーなし
	 * 		> 0: 戻り値個の誤りを訂正した
	 * 		< 0: 訂正不能
	 */
	public int decode(int[] data, int length, boolean noCorrect) {
		if(length < npar || length > 255) {
			return RS_PERM_ERROR;
		}
		// シンドロームを計算
		int[] syn = new int[npar];
		if(galois.calcSyndrome(data, length, syn)) {
			return 0;		// エラー無し
		}
		// シンドロームよりσとωを求める
		int[] sigma = new int[npar / 2 + 2];
		int[] omega = new int[npar / 2 + 1];
		int jisu = calcSigmaMBM(sigma, omega, syn);
		if(jisu <= 0) {
			return RS_CORRECT_ERROR;
		}
		// チェン探索により誤り位置を求める
		int[] pos = new int[jisu];
		int r = chienSearch(pos, length, jisu, sigma);
		if(r < 0) {
			return r;
		}
		if(!noCorrect) {
			// 誤り訂正
			doForney(data, length, jisu, pos, sigma, omega);
		}
		return jisu;
	}

	public int decode(int[] data, int length) {
		return decode(data, length, false);
	}

	public int decode(int[] data) {
		return decode(data, data.length, false);
	}

	public static void main(String[] args) {
		int[] data = new int[] {
				194, 220, 12, 121, 15, 0, 0, 0, 0, 0,
				 139, 196, 157, 211, 219, 234, 63, 10, 200, 70, 254, 132, 14, 192, 72, 248, 205, 236, 127, 19, 134, 80, 218, 84, 27, 155, 195, 197, 44, 175, 125, 45, 206, 127, 135, 245, 166, 221, 34, 78, 206, 204, 7, 1, 145, 9, 100, 170, 177, 233, 194, 50, 120, 20, 169, 200, 150, 219, 245, 68, 39, 178, 168, 100, 98, 98, 205, 43, 69, 51, 250, 121, 249, 94, 156, 250, 23, 63, 66, 164, 135, 129, 13, 18, 48, 184, 6, 243, 79, 145, 28, 108, 180, 123, 184, 24, 64, 179, 65, 140, 10, 93, 137, 92, 115, 153, 172, 39, 52, 227, 1, 35, 36, 146, 130, 139, 180, 211, 74, 114, 69, 230, 97, 201, 217, 80, 49, 195, 181, 92, 77, 238, 126, 125, 46, 99, 134, 92, 141, 31, 34, 126, 132, 100, 107, 135, 106, 149, 237, 117, 239, 250, 60, 1, 155, 105, 117, 36, 203, 152, 22, 205, 118, 100, 152, 216, 56, 189, 46, 190, 242, 250, 38, 21, 197, 107, 32, 56, 196, 78, 185, 246, 68, 68, 22, 106, 164, 190, 110, 107, 6, 236, 119, 237, 123, 30, 164, 152, 85, 242, 218, 23, 190, 11, 7, 128, 0, 67, 246, 250, 115, 18, 190, 112, 50, 233, 37, 247, 98, 218, 94, 35, 186, 15, 65, 156, 83, 230, 85, 160, 213, 94, 122, 95, 100, 207, 45, 108, 59, 115, 6, 148, 41, 84, 174
		};
		RsDecode dec = new RsDecode(255-10);
		int r = dec.decode(data);
		System.out.println("r=" + r);
		//System.out.println(java.util.Arrays.toString(data));
		for(int i = 0; i < 245/2; i++) {
			data[i] ^= (int) (Math.random() * 256);
		}
		System.out.println("CORRUPTED:");
		System.out.println(java.util.Arrays.toString(data));
		r = dec.decode(data);
		System.out.println("r=" + r);
		System.out.println(java.util.Arrays.toString(data));
	}

}
