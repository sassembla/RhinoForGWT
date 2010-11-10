package com.kissaki.rhinoforgwt;


/**
 * 型の定義がしてあるインターフェース
 * 型定義はEnum として実装してある。
 * 実装宣言順がより高い型が優先される。
 * 
 * 優先順位は　Object << String << Integer << Double　
 * JavaScript　から、　Java への変換に際して、一番注意を払う部分。
 * @author sassembla
 *
 */
public interface CollectionType {
	
	public enum TYPE_ENUM {
		TYPE_NULL,
		TYPE_JAVASCRIPTOBJECT,
		TYPE_STRING, 
		TYPE_BOOLEAN, 
		TYPE_DOUBLE,//
		
		;
		/**
		 * 入力されたEnum A,Bを比較し、より高位の方を返す
		 */
		public static TYPE_ENUM getHighType (TYPE_ENUM typeA, TYPE_ENUM typeB) {
			//該当するEnumで無ければ、エラーを返したいところ
			int i = 0;
			int a = 0;
			int b = 0;
			for (TYPE_ENUM types : values()) {
				if (types == typeA) {
					a = i;
				}
				if (types == typeB) {
					b = i;
				}
				
				i++;
			}
			
			int c = Math.max(a, b);
			
			return getTypeEnum(c);
		}

		/**
		 * 番号のEnum要素を返す
		 * 
		 * @param enumNumber
		 * @return
		 */
		public static TYPE_ENUM getTypeEnum(int number) throws IllegalArgumentException {
			int i = 0;
			for (TYPE_ENUM d : values()) {
				if (number == i) {
					return d;
				}
				i++;
			}
			
			throw new IllegalArgumentException("該当するナンバーのTypeが設定されていない_"+number);
		}
		
		
	}
	
	/**
	 * argument/引数か、parameter/パラメータかを指定するEnum
	 * 
	 * 優先順位は存在しないので、初期値は残らない。
	 * @author sassembla
	 *
	 */
	public enum DEFINITION_ENUM {
		NO_DEFINITION,
		DEFINE_ARG,
		DEFINE_PARAM;
	}
	/**
	 * メソッドの帰り値
	 * @author sassembla
	 *
	 */
	public enum METHOD_TYPE {
		METHOD_NONAME,
		METHOD_NORMAL;
		/**
		 * メソッド名を入力すると、用意しておいた無名メソッド名との干渉を感知し、無名メソッド名を返す
		 * どこで行い、どこに値を保存するか、が問題か。固定しておけるといいんだけれど、保存は出来ないからなあ。どこかに乱数を返すクラスを作るか？
		 * →
		 * @param typeA
		 * @return
		 */
//		public static METHOD_TYPE getHighType (TYPE_ENUM typeA) {
//			return null;
//		}
		
		/**
		 * 匿名メソッドの名称を返す
		 * @return
		 */
		public static String getMethodTypeString (TYPE_ENUM type) {
			return type.toString();
		}
	}
	
	public static final String METHOD_NAME_NULL = "noNameMethod_";//入力時に同名のメソッドがある可能性は　無いわけでは無い。。。　→匿名関数に付与される名称なのだが、先に名称リストを取得しておいた方がいいかもね。　その機会もあるし。 一致してしまう場合、避けるように、匿名名称を返すクラスを作ろう。
	public static final String NAME_NULL = "";//無名のパラメータ（JSには存在しない、、筈）
	
}
