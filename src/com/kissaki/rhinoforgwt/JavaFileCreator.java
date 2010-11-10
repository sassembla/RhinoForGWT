package com.kissaki.rhinoforgwt;

import com.kissaki.subFrame.Debug;

/**
 * クラス出力クラス
 * @author sassembla
 *
 */
public class JavaFileCreator {
	Debug debug = null;
	
	String name;//全体で使用する名前
	
	String STRING_KEY_PUBBLIC = "public"; 
	String STRING_KEY_VOID = "void";
	String STRING_KEY_CLASS = "class ";
	
	String STRING_KEY_PRIVATE = "private";
	
	
	String STRING_KEY_IMPLEMENTS = "implements";
	String STRING_KEY_INTERFACE = "Interface";
	
	String STRING_FOOTER_IMPLEMENTS = "Implements";
	

	String STRING_KEY_NATIVE = "native";
	String STRING_KEY_JAVASCRIPTOBJECT = "JavaScriptObject";
	String STRING_KEY_JAVASCRIPT_WINDOWOBJECT = "$wnd";
	String STRING_KEY_JSNI_HEADER = "/*-";
	String STRING_KEY_JSNI_FOOTER = "-*/;";

	String STRING_KEY_SETJAVASCRIPTMETHOD_HEADER = "setup";
	
	String STRING_KEY_INITIALJSOBJECT = "= null;";
	
	
	String STRING_JSOBJECT_COMEENT_HEADER = "//JavaScriptObject of ";
	String STRING_KEY_RETURN = "return";
	
	public static final String STRING_KEY_JS_FOOTER = "JS";//JSのラッパーメソッドのに使われる拡張子、みたいなもの
	String STRING_DEFAULT_ARG_TYPE = STRING_KEY_JAVASCRIPTOBJECT;
	

	/**
	 * 名称をセットする
	 * @param name
	 * @return
	 */
	public void setName(String name) {
		debug.trace("name_"+name);
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	
	private int phraseCount;//phrase {} のカウント  ()のカウントは頻出するため、特に作成しない
	
	/**
	 * コンストラクタ
	 */
	public JavaFileCreator() {
		debug = new Debug(this);
		
	}
	
	
	
	/**
	 * 実行
	 * 与えられたコレクションノードから情報を引き出し、処理する。
	 * @param collectionNode 
	 */
	public void procedure (CollectionNode collectionNode) {
		
		setName(collectionNode.getMainName());//メイン名称をノードから取得している。
		/*
		 * この事により、決定される事項は下記
		 * クラスの名称
		 * 	getName()+"Implements"
		 * 
		 * クラスのコンストラクタメソッドの名称
		 * 	getName()+"Implements"
		 * 
		 * クラス内で引き回されるJSObjectの名称
		 * 	getName()+"JSObject"
		 */
		
		StringBuffer sb = new StringBuffer();
		
		//Classヘッダー
		sb.append(createClassHeader(getName()));//Classヘッダーを追加
		addLeftPhrase(sb);//ClassのPhrase
		addCR(sb);
		
		//JSObject JSNIを使って実行する際に、オブジェクトとして使用する。
		sb.append(generateJSObject(getName()));//このクラス中でグローバルに使用するJSオブジェクトの定義を行う。　
		addCR(sb);
		
		//JSObjectのセッターメソッド、ゲッターメソッドを追加する
		sb.append(generateJSObjectSetter_Getter(getName(), null, null));
		addCR(sb);
		//JSObjectのJSNI初期化メソッドを追加する
		sb.append(generateNativeJavaScriptObjectMethod(getName(), null, null));
		addCR(sb);
		//内容　コンストラクタ
		sb.append(generateConstructorBlock(getName(), null, null));//コンストラクト時に引数を伴う場合、ここで使用する必要がある筈。　まだ。
		addCR(sb);
		
		
		addCR(sb);
		addCR(sb);
		
		//内容　メソッド群の実装
		for (int i = 0; i < collectionNode.getMethodNum();i++) {
			//collectionNode.removeMethodsByMethodType(CollectionType.METHOD_TYPE.METHOD_NONAME);
		
	
			//一つずつメソッドを書き出すか。 インデックスが必要。A~順とか欲しい？　本来APIはそうなってるとうれしい物だけれど。
			sb.append(collectionNode.getMethodNode(i));
			addLeftPhrase(sb);//ClassのPhrase
			addCR(sb);//改行付き
			sb.append(collectionNode.getMethodExec(i));
			addCR(sb);//改行付き
			addRightPhrase(sb);//ClassのPhrase
			
			addCR(sb);//改行付き
			addCR(sb);//改行付き
			if (i == 10) break;
		}

		
		
		
		addRightPhrase(sb);//Classフッターを追加
		
		
		debug.assertTrue(getPhraseCount() == 0, "phraseCount != 0 _"+phraseCount);
		debug.trace("procedure_sb_"+sb.toString());//最終出力
	}
	
	/**
	 * 現在の行で、必要なタブの個数を返す
	 * =かっこの数
	 * @return
	 */
	private int getPhraseCount() {
		return phraseCount;
	}
	
	

	/**
	 * クラスヘッダーを作成する
	 * 
	 * インターフェースとは同じ場所に置かれる前提なので、importは用意していない。
	 * コメントが必要な場合は、別途Classコメントを用意する事。
	 * @param name JSの初期化varの名称
	 * @return
	 */
	public String createClassHeader(String name) {
		
		StringBuffer sb = new StringBuffer();
		sb.append(STRING_KEY_PUBBLIC + " " + STRING_KEY_CLASS + " " +getImplementsName(name) + " " +
				STRING_KEY_IMPLEMENTS + " " + getInterfacename(name) + " ");
		return sb.toString();
	}
	
	/**
	 * インターフェースクラスの名称を返す
	 * @param name
	 * @return
	 */
	private String getInterfacename(String name) {
		String interfaceName = name+STRING_KEY_INTERFACE;
		return interfaceName;
	}

	/**
	 * 実装クラスの名称を返す
	 * @param name
	 * @return
	 */
	private String getImplementsName(String name) {
		String implementsName = name+STRING_FOOTER_IMPLEMENTS;
		return implementsName;
	}

	/**
	 * JSObjectのインスタンス定義を返す
	 * @param name
	 * @return
	 */
	public String generateJSObject (String name) {
		StringBuffer sb = new StringBuffer();
		
		//JSObjectの定義
		sb.append(STRING_KEY_PRIVATE + " " + STRING_KEY_JAVASCRIPTOBJECT + " " + instanceOfJSObject(name) + " " + STRING_KEY_INITIALJSOBJECT + " " + STRING_JSOBJECT_COMEENT_HEADER + name);//JSObjectのジェネレート
		
		return sb.toString();
	}
	

	/**
	 * 初期化用のJSObject、コンストラクタ、ゲッター、JSコンストラクタを生成する
	 * 
	 * @param name　JSの初期化varの名称
	 * @param definition 可変長　引数の型　長さはパラメータと同様
	 * @param parameter　可変長　長さはパラメータの数
	 * @return
	 */
	public String generateConstructorBlock (String name, String definition [], String parameter []) {
		
		
		
		StringBuffer sb = new StringBuffer();
		
		//コンストラクタの作成
		sb.append(STRING_KEY_PUBBLIC+" "+getImplementsName(name)+"(");
		define_parameterDescription(sb, definition, parameter);//型とパラメータを記述
		sb.append(") ");
		addLeftPhrase(sb);//コンストラクタヘッダーを追加
		addCR(sb);
		
		sb.append(getSetterMethodName(name)+"(");
		parameterDescription(sb,parameter);//パラメータを記述
		sb.append(");");
		
		addCR(sb);
		addRightPhrase(sb);//コンストラクタフッターを追加
		
		return sb.toString();
	}
	

	
	

	/**
	 * セッター、ゲッターを出力するメソッド
	 * @param name
	 * @param definition
	 * @param parameter
	 */
	public String generateJSObjectSetter_Getter (String name, String definition [], String parameter []) {
		StringBuffer sb = new StringBuffer();
		
		//setter
		sb.append(STRING_KEY_PUBBLIC+ " " + STRING_KEY_VOID + " " +getSetterMethodName(name)+" (");
		define_parameterDescription(sb, definition, parameter);//型とパラメータ名
		sb.append(") ");
		addLeftPhrase(sb);// { を追加
		addCR(sb);
		
		sb.append(getName()+" = "+jSInitializerMethodName(name)+"(");
		parameterDescription(sb, parameter);//パラメータ名
		sb.append(");");
		addCR(sb);
		addRightPhrase(sb);//}
		
		
		addCR(sb);
		addCR(sb);

		//getter
		sb.append(STRING_KEY_PUBBLIC+ " " + STRING_KEY_JAVASCRIPTOBJECT + " " +getGetterMethodName(name)+" ()");
		addLeftPhrase(sb);// { を追加
		addCR(sb);
		
		sb.append(STRING_KEY_RETURN + " " +instanceOfJSObject(name)+";");
		
		addCR(sb);
		addRightPhrase(sb);//}
		
		addCR(sb);
		addCR(sb);
		return sb.toString();
	}
	
	
	/**
	 * JSNIで、JSObjectの初期化を行うメソッドを出力する
	 * 
	 * @param name
	 * @param definition
	 * @param parameter
	 * @return
	 */
	public String generateNativeJavaScriptObjectMethod (String name, String definition [], String parameter []) {
		StringBuffer sb = new StringBuffer();
		sb.append(STRING_KEY_PRIVATE+ " " + STRING_KEY_NATIVE + " " + STRING_KEY_JAVASCRIPTOBJECT + " " +jSInitializerMethodName(name)+" (");
		define_parameterDescription(sb, definition, parameter);//型情報、パラメータ名
		sb.append(")"+ " " + STRING_KEY_JSNI_HEADER);
		addLeftPhrase(sb);// { を追加
		addCR(sb);
		
		sb.append(STRING_KEY_RETURN + " "+ STRING_KEY_JAVASCRIPT_WINDOWOBJECT +"."+ name+"(");
		parameterDescription(sb, parameter);
		sb.append(");");
		
		addCR(sb);
		addRightPhrase(sb);//}
		sb.append(STRING_KEY_JSNI_FOOTER);
		addCR(sb);
		
		return sb.toString();
	}
	

	/**
	 * JSObjectを初期化する際に使用するJSNIメソッドの名称を取得するメソッド
	 */
	private String jSInitializerMethodName(String name2) {
		String methodName = STRING_KEY_SETJAVASCRIPTMETHOD_HEADER+ instanceOfJSObject(name);
		return methodName;
	}
	
	

//	通常関数出力用
//	----------------------------------------------------------------------------------
	
	

	/**
	 * JSのnameメソッドを呼び出すJavaメソッド
	 * 外部に露出するのはこのメソッドの名称になる。
	 */
	public String generateNormalMethod (String name, String methodType, String definition [], String parameter []) {
		StringBuffer sb = new StringBuffer();
		
		
		//Javaメソッド
		sb.append(STRING_KEY_PUBBLIC + " " + methodType + " " + name +" (");
		define_parameterDescription(sb, definition, parameter);//型名とパラメータ名
		sb.append(") ");
		addLeftPhrase(sb);// { を追加
		addCR(sb);
		
		sb.append(STRING_KEY_RETURN + " " + name + STRING_KEY_JS_FOOTER + "(");
		
		
		String definition2 [] = new String[definition.length+1];
		definition2[0] = STRING_DEFAULT_ARG_TYPE;//引数の型の初期値を設定
		for (int i = 0; i < definition.length; i++) {
			definition2[i+1] = definition[i];
		}
		
		String parameter2 [] = new String[parameter.length+1];
		parameter2[0] = getGetterMethodName(name)+"()";//メソッド名を設定
		for (int i = 0; i < parameter.length; i++) {
			parameter2[i+1] = parameter[i];
		}
		
		/*
		 * のちのNativeメソッドで使用するために、Native内で記述する用のparameter3も用意しておく。
		 */
		String parameter3 [] = new String[parameter.length+1];
		parameter3[0] = name+"Jso";
		for (int i = 0; i < parameter.length; i++) {
			parameter3[i+1] = parameter[i];
		}
		
		parameterDescription(sb, parameter2);//パラメータ,最初の一つはメソッドgetGetterMethodNameで取得できるメソッド名文字列()
		sb.append(");");
		
		addCR(sb);
		addRightPhrase(sb);//}
		
		addCR(sb);
		
		
		
		
		
		//JavaScriptメソッド
		sb.append(STRING_KEY_PRIVATE+ "" + STRING_KEY_NATIVE + " " + methodType + " " + name + STRING_KEY_JS_FOOTER + " (");
		define_parameterDescription(sb, definition2, parameter3);//型とパラメータ
		sb.append(") "+STRING_KEY_JSNI_HEADER);
		addLeftPhrase(sb);//{
		addCR(sb);
		
		sb.append(STRING_KEY_RETURN + " " + parameter3[0]+"."+name+"(");
		parameterDescription(sb, parameter);//パラメータ
		sb.append(");");
		
		addCR(sb);
		addRightPhrase(sb);//}
		sb.append(STRING_KEY_JSNI_FOOTER);
		
		return sb.toString();
	}
	
	

//	汎用
//	----------------------------------------------------------------------------------

	/**
	 * クラス内で引き回されるJSObjectの名称を返す
	 * @param name
	 * @return
	 */
	public String instanceOfJSObject (String name) {
		return name+"JSObject";
	}
	
	
	

//	パラメータ関連記述用
//	----------------------------------------------------------------------------------

	/**
	 * パラメータ名を連続で記述する
	 * @param sb
	 * @param parameter
	 */
	private void parameterDescription(StringBuffer sb, String[] parameter) {
		if (parameter != null) {
			for (int i = 0; i < parameter.length; i++) {
				if (1 <= i) {
					sb.append(", ");
				}
				sb.append(parameter[i]);
			}
		}
	}

	/**
	 * パラメータを型名とパラメータ名の連続で記述する
	 * @param sb
	 * @param definition
	 * @param parameter
	 */
	private void define_parameterDescription(
			StringBuffer sb, 
			String[] definition,
			String[] parameter
			) {
		if (definition != null && parameter != null) {
			for (int i = 0; i < definition.length; i++) {
				if (1 <= i) {
					sb.append(", ");
				}
				sb.append(definition[i]+" "+parameter[i]);
			}
		}
	}
	
	
	
	/**
	 * セッターメソッドの出力を行う
	 * @param name
	 * @return
	 */
	private String getSetterMethodName(String name) {
		String setterMethodName = "set" + instanceOfJSObject(name);
		return setterMethodName;
	}
	

	/**
	 * ゲッターメソッドの出力を行う
	 * @param name
	 * @return
	 */
	private String getGetterMethodName(String name) {
		String getterMethodName = "get" + instanceOfJSObject(name);
		return getterMethodName;
	}
	
	
//	ユーティリティ
//	----------------------------------------------------------------------------------
	/**
	 * 右括弧を足す {
	 * 左括弧との個数チェックに使用する。
	 * 
	 * インデントもそのうち行う。
	 */
	private void addLeftPhrase(StringBuffer sb) {
		sb.append("{");
		
		phraseCount++;//この数字分だけ、プログラム全体にtabを加えてインデントしたい。
	}

	/**
	 * 左括弧を足す }
	 * 右括弧との個数チェックに使用する。
	 * @param sb
	 */
	private void addRightPhrase(StringBuffer sb) {
		phraseCount--;
		//一つtabを減らす
		sb.deleteCharAt(sb.length()-1);
		sb.append("}");
		debug.assertTrue(0 <= phraseCount, "phraseCountが不足、}が多い");
	}
	

	/**
	 * CR(改行)を加える
	 * 改行に加えて、タブが必要な場合はタブを加える。
	 * @param sb
	 */
	private void addCR(StringBuffer sb) {
		debug.trace("pharese_"+phraseCount);
		sb.append(System.getProperty("line.separator"));
		int max = getPhraseCount();
		for (int i = 0; i < max; i++) {
			sb.append("	");//タブ
		}
	}


}