package com.kissaki.rhinoforgwt;


/**
 * 型の定義がしてあるインターフェース
 * 型定義はEnum として実装してある。
 * 実装宣言順がより高い型が優先される。
 * 
 * 型の優先順位は残念ながら全体固定で、　Object << String << Integer << Double　
 * JavaScript　から、　Java への変換に際して、一番注意を払う部分。
 * @author sassembla
 *
 */
public interface CollectionType {
	
	/*
	 * 無名メソッドの定義
	 */
	public final String METHOD_NONANE = "";
	
	/*
	 * 出力ファイルのパターン
	 */
	String FILE_PATTERN = "java";

	/*
	 * 修飾子
	 */
	String STRING_PACKAGE_DEFINITION = "package";
	String STRING_IMPORT_DEFINITION = "import";
	
	
	String STRING_KEY_PUBLIC = "public"; 
	String STRING_KEY_PRIVATE = "private";
	
	String STRING_KEY_VOID = "void";
	String STRING_KEY_CLASS = "class ";
	
	String STRING_KEY_IMPLEMENTS = "implements";
	String STRING_KEY_INTERFACE = "interface";
	
	String STRING_FILEFOOTER_IMPLEMENTS = "Implements";
	String STRING_FILEFOOTER_INTERFACE = "Interface";

	String STRING_KEY_NATIVE = "native";
	String STRING_KEY_JAVASCRIPTOBJECT = "JavaScriptObject";
	String STRING_KEY_JAVASCRIPT_WINDOWOBJECT = "$wnd";
	String STRING_KEY_JSNI_HEADER = "/*-";
	String STRING_KEY_JSNI_FOOTER = "-*/;";

	String STRING_KEY_SETJAVASCRIPTMETHOD_HEADER = "setup";
	
	String STRING_KEY_INITIALJSOBJECT = "= null;";
	
	
	String STRING_JSOBJECT_COMEENT_HEADER = "//JavaScriptObject of ";
	String STRING_KEY_RETURN = "return";
	
	public final String TENPLATE_OBJECT = "getInstanceOfJSObject()";
	
	public final String TEMPLATE_OVERLOADED = "Overloaded_";
	
	
	
	/*
	 * システム固有
	 * Aと、_Aというメソッドが共存していて、かつ引数構成が同じ場合、困った事になる。
	 */
	public final String MIDDLE_METHODHEADER = "_";//中間メソッドの識別子
	
	
	
	public static final String TYPENAME_NULL = "null";
	public static final String TYPENAME_JAVASCRIPTOBJECT = "JavaScriptObject";
	public static final String TYPENAME_STRING = "String";
	public static final String TYPENAME_BOOLEAN = "boolean";
	public static final String TYPENAME_DOUBLE = "double";
	
	
	
	public final String STRING_KEY_JSOBJECT = "JSObject";
	
	String TypeString [] = {
			TYPENAME_NULL,
		//	TYPENAME_JAVASCRIPTOBJECT,
			TYPENAME_STRING, 
			TYPENAME_BOOLEAN, 
			TYPENAME_DOUBLE,
	};
	
	public enum TYPE_ENUM {
		TYPEENUM_NULL,
		//TYPEENUM_JAVASCRIPTOBJECT,
		TYPEENUM_STRING, 
		TYPEENUM_BOOLEAN, 
		TYPEENUM_DOUBLE,
		
		;
		
		/**
		 * 指定されているデフォルト型を取得する
		 */
		public static TYPE_ENUM getDefaultType () {
			for (int i = 0; i < TypeString.length; i++) {
				if (TypeString[i].equals(STRING_DEFAULT_ARG_TYPE)) {
					return getTypeEnum(i);
				}
			}
			
			return null;
		}
		
		
		
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
		
		
		
		/**
		 * 該当するEnumのString表現を返す
		 * @param number
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static String getTypeString(TYPE_ENUM expression) throws IllegalArgumentException {
			int i = 0;
			for (TYPE_ENUM d : values()) {
				if (expression.equals(d)) {
					return TypeString[i];
				}
				i++;
			}
			throw new IllegalArgumentException("該当する種類のTypeが設定されていない_"+expression);
		}

		static TYPE_ENUM STRING_DEFAULT_ENUM_TYPE = TYPEENUM_STRING;
		static String STRING_DEFAULT_ARG_TYPE = TYPENAME_STRING;

		public static TYPE_ENUM getDefaultTypeEnum() {
			return STRING_DEFAULT_ENUM_TYPE;
		}
		
		
	}
	
	/**
	 * argument/引数か、parameter/パラメータかを指定するEnum
	 * 引数か、内部パラメータか、無名関数の内容か
	 * @author sassembla
	 *
	 */
	public enum DEFINITION_ENUM {
		NO_DEFINITION,
		DEFINE_ARG,
		DEFINE_PARAM,
		DEFINE_OVERLOAD,
		DEFINE_UNKNOWN;
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
		 * 匿名メソッドの名称を返す
		 * @return
		 */
		public static String getMethodTypeString (TYPE_ENUM type) {
			return type.toString();
		}
	}
	
	public static final String NAME_NULL = "";//無名のパラメータ（JSには存在しない、、筈）
	
	
}
