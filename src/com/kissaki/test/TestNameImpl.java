package com.kissaki.test;

import junit.framework.TestCase;

import com.kissaki.rhinoforgwt.CollectionNode;
import com.kissaki.rhinoforgwt.JavaFileCreator;
import com.kissaki.rhinoforgwt.CollectionType.DEFINITION_ENUM;
import com.kissaki.rhinoforgwt.CollectionType.TYPE_ENUM;
import com.kissaki.subFrame.Debug;

public class TestNameImpl extends TestCase {
	Debug debug = null;
	JavaFileCreator test = null;
	
	
	protected void setUp() throws Exception {
		debug = new Debug(this);
		test = new JavaFileCreator();
	}
	
	
	protected void tearDown() throws Exception {
		debug = null;
		test = null;
	}
	
	
	
	public void testCreateClassHeader () {
		String name = "Classヘッダーの定義名";
		debug.trace("testCreateClassHeader_"+test.createClassHeader(name));
	}
	
	/**
	 * 初期化ブロックを作成
	 */
	public void testGenerateInitBlock () {
		
		String name = "テストクラス";
		String definition [] = {
			"Integer", "Double", "Element",
		};
		
		String parameter [] = {
			"100", "0.6", "aElement",
		};
		debug.trace("generateJSObject_"+test.generateJSObject(name));
		debug.trace("testGenerateInitBlock_"+test.generateConstructorBlock(name, definition, parameter));
	}
	
	public void testGenerateJSObjectSetter_Getter () {
		String name = "テストクラス";
		String definition [] = {
			"Integer", "Double", "Element",
		};
		
		String parameter [] = {
			"100", "0.6", "aElement",
		};
		
		debug.trace("testGenerateJSObjectSetter_Getter_"+test.generateJSObjectSetter_Getter(name,definition,parameter));
	}
	
	
	public void testGenerateNativeJavaScriptObject () {
		String name = "ネイティブなJSOの生成テスト";
		String definition [] = {
			"コンストラクタの引数１", "Double", "Element",
		};
		
		String parameter [] = {
			"100", "0.6", "aElement",
		};
		
		debug.trace("testGenerateNativeJavaScriptObject"+test.generateNativeJavaScriptObjectMethod(name, definition, parameter));
	}
	
	
	public void testGenerateNormalMethod () {
		String name = "ネイティブなJSOの生成テスト";
		
		String methodType = "voidとか？ケース分け居るかもだが、最低でもBoolを返してる気がする";
		
		
		String definition [] = {
			"コンストラクタの引数１", "Double", "Element",
		};
		
		String parameter [] = {
			"100", "0.6", "aElement",
		};
		
		debug.trace("testGenerateNormalMethod_"+test.generateNormalMethod(name, methodType, definition, parameter));
	}
	
	
	public void testProceed () {
		/**
		 * 名前、
		 * 	メソッドのタイプ、JavaScriptObjectで固定
		 * 	メソッド引数のdefinition、メソッド引数のパラメータ名
		 * 
		 * その他メソッド数、
		 * 	メソッドのタイプ、
		 * 	メソッド引数のdefinition、メソッド引数のパラメータ名
		 */
		
		/*
		 * methodListは、どんな内容がいいのかな。要件としては、
		 * 情報を保存しておき、下記の数値はすぐにアクセスできる必要がある
		 * メインメソッド名(String)
		 * メインメソッド型（String、"JavaScriptObject"で固定）
		 * 
		 * 全メソッド数（int） メインメソッドは数に入れない。
		 * 
		 * メソッドごとの名称(String)
		 * メソッドごとの型(String)
		 * メソッドごとの引数の型(String)//0~複数があり得る 可変長配列のStringクラスってあったっけ。
		 * メソッドごとの引数の名称(String)//0~複数があり得る
		 * 
		 * こんな所か。
		 * なので、要件を満たすリストのようなモノの実装は、
		 * Nodeのような形になるのか。やっぱり。
		 * 面倒だなあ。。。
		 * 
		 * inputとoutputの両方があり得るから、データストアの構造が参考になるかもだわ。
		 * 深度があればいいから、JSONでもいいんだわ。
		 * まあ無理にJSONで作る事も無いから、CollectionNodeクラスと、その中に各カテゴリの情報を入れた物、クラス自体を所持するクラスを
		 * 作っておしまいなのだわ。
		 */
		CollectionNode collectionNode = new CollectionNode("テスト用");//初期化
		collectionNode.insertMethod("メソッド名");
		collectionNode.insertParam("パラメータ名", 100, DEFINITION_ENUM.DEFINE_ARG);
		collectionNode.updateParamByReg(100, TYPE_ENUM.TYPE_JAVASCRIPTOBJECT);
		collectionNode.updateParamByName("パラメータ名", TYPE_ENUM.TYPE_BOOLEAN);
		
//		debug.trace("メソッド数_"+collectionNode.getMethodNum());
		
//		test.procedure(collectionNode);
		
	}
}
