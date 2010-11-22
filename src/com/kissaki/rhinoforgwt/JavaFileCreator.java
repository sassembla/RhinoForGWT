package com.kissaki.rhinoforgwt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.kissaki.subFrame.Debug;

/**
 * クラス出力クラス
 * @author sassembla
 *
 */
public class JavaFileCreator implements CollectionType {
	Debug debug = null;
	
	
	
	
	String masterName;
	

	/**
	 * コンストラクタ
	 */
	public JavaFileCreator() {
		debug = new Debug(this);
	}
	
	
	
	/**
	 * 名称をセットする
	 * @param name
	 * @return
	 */
	private void setMasterName(String name) {
		debug.trace("name_"+name);
		this.masterName = name;
	}
	
	/**
	 * @return the name
	 */
	public String getMasterName() {
		return masterName;
	}
	
	
	private int phraseCount;//phrase {} のカウント  ()のカウントは頻出するため、特に作成しない
	
	
	/**
	 * 実行
	 * 与えられたコレクションノードから情報を引き出し、処理する。
	 * @param collectionNode 
	 * @param filePattern 拡張子
	 */
	public void procedure (CollectionNode collectionNode, String outputPath, String packageName, String imports) {
		
		setMasterName(collectionNode.getMainName());//メイン名称を設定
		
		debug.trace("before_"+collectionNode.getMethodNum());
		//匿名メソッドを取り除く
		collectionNode.removeMethodsByNamed(CollectionType.METHOD_NONANE);
		
		debug.trace("after_"+collectionNode.getMethodNum());
		
		/*
		 * この事により、クラスの名称が決定される。
		 * 
		 * インターフェース
		 *  getMasterName()+"Interface".java
		 *  
		 *  
		 * 実装クラス
		 * 	getMasterName()+"Implements".java
		 * 
		 * 
		 * クラスのコンストラクタメソッドの名称
		 * 	getMasterName()+"Implements"
		 */
		outputInterfaceFile(collectionNode, outputPath, packageName, imports);
		outputImplementsFile(collectionNode, outputPath, packageName, imports);
	}
	
	/**
	 * インターフェースファイルの出力
	 * @param imports 
	 * @param packageName 
	 */
	private void outputInterfaceFile (CollectionNode collectionNode, String outputPath, String packageName, String imports) {
		
		StringBuffer sb = new StringBuffer();
		
		//package
		sb.append(getPackageDefinition(packageName));
		addCR(sb);
		addCR(sb);
		
		//import
		sb.append(getImportDefinitions(imports));
		addCR(sb);
		addCR(sb);
		

		
		//Interfaceヘッダー
		sb.append(createInterfaceHeader(getMasterName()));//Classヘッダーを追加
		addLeftPhrase(sb);//ClassのPhrase
		addCR(sb);
		addCR(sb);
		
		
//		//コンストラクタ　
//		sb.append("/*コンストラクタ*/");//コメント
//		addCR(sb);
//		sb.append(getConstructorHeader(getMasterName(), null, null)+";");
//		addCR(sb);
		
		
		addCR(sb);
		addCR(sb);
		
		//メソッド	public void a(A a, B b, C c);
		for (int i = 0; i < collectionNode.getMethodNum();i++) {
			sb.append(collectionNode.getMethodHeader(i) + ";");
			addCR(sb);//改行付き
			addCR(sb);//改行付き
		}
		addRightPhrase(sb);//Interfaceフッターを追加
		
		
		debug.assertTrue(getPhraseCount() == 0, "phraseCount != 0 _"+phraseCount);	
		
		
		
		//ファイル出力
		output(sb.toString(), outputPath, getMasterName()+STRING_FILEFOOTER_INTERFACE+"."+FILE_PATTERN);
	}
	
	
	/**
	 * 実装クラスのファイル出力
	 * @param imports 
	 * @param packageName 
	 */
	private void outputImplementsFile(CollectionNode collectionNode, String outputPath, String packageName, String imports) {
		StringBuffer sb = new StringBuffer();
		
		//package
		sb.append(getPackageDefinition(packageName));
		addCR(sb);
		addCR(sb);
		
		//import
		sb.append(getImportDefinitions(imports));
		addCR(sb);
		addCR(sb);
		
		//Classヘッダー
		sb.append(createClassHeader(getMasterName()));//Classヘッダーを追加
		addLeftPhrase(sb);//ClassのPhrase
		addCR(sb);
		addCR(sb);
		
		//JSObject JSNIを使って実行する際に、オブジェクトとして使用する。
		sb.append(generateJSObject(getMasterName()));//このクラス中でグローバルに使用するJSオブジェクトの定義を行う。　
		addCR(sb);
		addCR(sb);
		
		//JSObjectのセッターメソッド、ゲッターメソッドを追加する
		sb.append(generateJSObjectSetter_Getter(getMasterName()));
		addCR(sb);
		
		//JSObjectのJSNI初期化メソッドを追加する
		sb.append(
				generateNativeJavaScriptObjectMethod(getMasterName(), 
				collectionNode.getConstructorMethodBodyParametersTo(getMasterName()),
				collectionNode.getConstructorBodyParametersTo(getMasterName())
				));
		addCR(sb);
		addCR(sb);
		
		//内容　コンストラクタ
		sb.append(generateConstructorBlock(getMasterName(), 
				collectionNode.getConstructorMethodHeader(getMasterName()), 
				collectionNode.getConstructorMethodBody(getMasterName()))
				);
		addCR(sb);
		
		addCR(sb);
		addCR(sb);
		
		//内容　メソッド群の実装
		for (int i = 0; i < collectionNode.getMethodNum();i++) {
			
			/*
			 * Javaメソッド
			 */
			sb.append(collectionNode.getMethodHeader(i));//メソッドのヘッダー
			addLeftPhrase(sb);//MethodのPhrase
			addCR(sb);//改行付き
			sb.append(collectionNode.getMethodBody(i));
			addCR(sb);//改行付き
			addRightPhrase(sb);//MethodのPhrase
			addCR(sb);//改行付き
			
			
			/*
			 * JSNativeメソッド
			 */
			sb.append(collectionNode.getNativeMethodHeader(i));//Nativeのメソッドヘッダー
			addLeftPhrase(sb);//NativeMethodのPhrase
			addCR(sb);//改行付き
			sb.append(collectionNode.getNativeMethodBody(packageName, getImplementsName(getMasterName()), i));
			addCR(sb);//改行付き
			addRightPhrase(sb);//NativeMethodのPhrase
			sb.append(collectionNode.getNativeMethodFooter());//Nativeのメソッドフッター
			
			addCR(sb);//改行付き
			addCR(sb);//改行付き
			
		}
		addRightPhrase(sb);//Classフッターを追加
	
		debug.assertTrue(getPhraseCount() == 0, "phraseCount != 0 _"+phraseCount);
		
		//ファイル出力
		output(sb.toString(), outputPath, getMasterName()+STRING_FILEFOOTER_IMPLEMENTS+"."+FILE_PATTERN);
	}




	/**
	 * パッケージ宣言を記述する
	 * @param packageName
	 * @return
	 */
	private Object getPackageDefinition(String packageName) {
		StringBuffer sb = new StringBuffer();
		sb.append(STRING_PACKAGE_DEFINITION + " " + packageName + ";");
		return sb.toString();
	}


	/**
	 * インポート宣言を記述する
	 * @param imports
	 * @return
	 */
	private Object getImportDefinitions(String imports) {
		StringBuffer sb = new StringBuffer();
		sb.append(STRING_IMPORT_DEFINITION + " " + imports + ";");
		return sb.toString();
	}


	/**
	 * 外部にファイルとして出力するメソッド
	 * @param string
	 */
	public void output(String contents, String path, String name) {
		String fileName = path+name;
		File file = new File(fileName);
		FileWriter filewriter = null;
		
		try {
			filewriter = new FileWriter(file);
		} catch (Exception e) {
			debug.trace("output_error_"+e);
		}
		
		BufferedWriter bw = new BufferedWriter(filewriter);
		PrintWriter pw = new PrintWriter(bw);
		
		pw.println(contents);
		pw.close();
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
	 * @param name JSの初期化varの名称
	 * @return
	 */
	public String createClassHeader(String name) {
		
		StringBuffer sb = new StringBuffer();
		sb.append(STRING_KEY_PUBLIC + " " + STRING_KEY_CLASS + " " +getImplementsName(name) + " " +
				STRING_KEY_IMPLEMENTS + " " + getInterfaceName(name) + " ");
		return sb.toString();
	}
	
	/**
	 * インターフェース
	 * @param name
	 * @return
	 */
	public String createInterfaceHeader(String name) {
		StringBuffer sb = new StringBuffer();
		sb.append(STRING_KEY_PUBLIC + " " + STRING_KEY_INTERFACE + " " +getInterfaceName(name));
		return sb.toString();
	}
	
	/**
	 * インターフェースクラスの名称を返す
	 * @param name
	 * @return
	 */
	private String getInterfaceName(String name) {
		String interfaceName = name+STRING_FILEFOOTER_INTERFACE;
		return interfaceName;
	}

	/**
	 * 実装クラスの名称を返す
	 * @param name
	 * @return
	 */
	private String getImplementsName(String name) {
		String implementsName = name+STRING_FILEFOOTER_IMPLEMENTS;
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
	public String generateConstructorBlock (String name, String header, String body) {
		
		
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("/*コンストラクタ*/");//コメント
		addCR(sb);
		
		sb.append(header);
		

		addLeftPhrase(sb);//コンストラクタの括弧を追加
		addCR(sb);
		
		/*
		 * コンストラクタでの処理内容、
		 */
		sb.append(body);
		
		addCR(sb);
		addRightPhrase(sb);//コンストラクタフッターを追加
		
		return sb.toString();
	}
	

	



	/**
	 * ゲッターを出力するメソッド
	 * @param name
	 * @param definition
	 * @param parameter
	 */
	public String generateJSObjectSetter_Getter (String name) {
		StringBuffer sb = new StringBuffer();
		
//		//setter
//		sb.append(STRING_KEY_PRIVATE + " " + STRING_KEY_VOID + " " +getSetterMethodName(name)+" (");
//		define_parameterDescription(sb, definition, parameter);//型とパラメータ名
//		sb.append(") ");
//		addLeftPhrase(sb);// { を追加
//		addCR(sb);
//		
//		sb.append(instanceOfJSObject(name)+" = "+jSInitializerMethodName(name)+"(");
//		parameterDescription(sb, parameter);//パラメータ名
//		sb.append(");");
//		addCR(sb);
//		addRightPhrase(sb);//}
//		
//		
//		addCR(sb);
//		addCR(sb);

		
		//getter
		/**
		 * ゲッターは一意のメソッドとする(名称が変わるのがめんどう、、という訳ではないのだけれど、どちらがいいのか見当がつかなかったため。)
		 */
		sb.append(STRING_KEY_PRIVATE+ " " + STRING_KEY_JAVASCRIPTOBJECT + " " + CollectionType.TENPLATE_OBJECT);
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
	public String generateNativeJavaScriptObjectMethod (String name, String parameter, String executeParam) {
		StringBuffer sb = new StringBuffer();
		sb.append(STRING_KEY_PRIVATE+ " " + STRING_KEY_NATIVE + " " + STRING_DEFAULT_ARG_TYPE + " " +jSInitializerMethodName(name)+" (");
		sb.append(parameter);
		sb.append(")"+ " " + STRING_KEY_JSNI_HEADER);
		addLeftPhrase(sb);// { を追加
		addCR(sb);
		
		sb.append(STRING_KEY_RETURN + " "+ STRING_KEY_JAVASCRIPT_WINDOWOBJECT +"."+ name+"(");
		sb.append(executeParam);
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
		String methodName = STRING_KEY_SETJAVASCRIPTMETHOD_HEADER+ instanceOfJSObject(getMasterName());
		return methodName;
	}
	
	

	
	

//	汎用
//	----------------------------------------------------------------------------------

	/**
	 * クラス内で引き回されるJSObjectの名称を返す
	 * @param name
	 * @return
	 */
	public String instanceOfJSObject (String name) {
		return name+STRING_KEY_JSOBJECT;
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
		sb.append(System.getProperty("line.separator"));
		int max = getPhraseCount();
		for (int i = 0; i < max; i++) {
			sb.append("	");//タブ
		}
	}


}