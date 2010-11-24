package com.kissaki.test;

import junit.framework.TestCase;

import com.kissaki.rhinoforgwt.CollectionNode;
import com.kissaki.rhinoforgwt.CollectionType;
import com.kissaki.rhinoforgwt.MethodNode;
import com.kissaki.rhinoforgwt.CollectionType.DEFINITION_ENUM;
import com.kissaki.rhinoforgwt.CollectionType.METHOD_TYPE;
import com.kissaki.rhinoforgwt.CollectionType.TYPE_ENUM;
import com.kissaki.subFrame.Debug;

/**
 * CollectonNodeのテスト
 * @author sassembla
 *
 */
public class CollectionNodeTest extends TestCase implements CollectionType {
	Debug debug = null;
	CollectionNode test = null;
	
	@Override
	protected void setUp() throws Exception {
		debug = new Debug(this);
		test = new CollectionNode();
		test.setMainName("");
	}
	
	@Override
	protected void tearDown() throws Exception {
		test = null;
		debug = null;
	}
	
	
	public void testIsMethodNameAlreadyExist () {
		test.insertMethod("メソッド名");
		
		boolean b = test.isMethodNameAlreadyExist("メソッド名");
		assertEquals(true, b);//存在するのでtrue
	}
	
	public void testIsMethodNameAlreadyExist2 () {
		boolean b = test.isMethodNameAlreadyExist("メソッド名");
		assertEquals(false, b);//何も存在しないのでfalse
	}
	
	public void testIsMethodNameAlreadyExist3 () {
		test.insertMethod("メソッド名");
		
		boolean b = test.isMethodNameAlreadyExist("メソッド名2");
		assertEquals(false, b);//該当するものが存在しないのでfalse
	}
	
	public void testHasParam () {
		test.insertMethod("メソッド名");
		test.insertParam("メソッド名", "パラメータ1", DEFINITION_ENUM.DEFINE_ARG);
		
		//このパラメータを持つメソッドが返ってくる
		MethodNode node = test.getMethodHasParam("パラメータ1");
		assertTrue("メソッド名が一致しない", node.getMethodName().equals("メソッド名"));
	}
	

	
	
	/**
	 * 現在フォーカスしているメソッドに対して、パラメータを追加するテスト
	 * 
	 * 現在フォーカスしているメソッドを取得し、
	 * そのノードに対して、パラメータを追加する　
	 * ノードのパラメータに追加できたか否かは、メソッドノードが扱う addpNodeParam メソッドで行う　既に或る場合エラーが帰ってくる
	 */
	public void testInsertParam () {
		test.insertMethod("メソッド名");
		test.insertParam("メソッド名", "パラメータ1", DEFINITION_ENUM.DEFINE_ARG);
		
		//メソッドが一つある
		assertEquals(1,test.getMethodNum());//一つのメソッドが有る
		
		//メソッドが一つのパラメータを持っている
		assertEquals(1, test.getNowMethodParameterNum("メソッド名"));
		
		//パラメータの名称がパラメータ名と同一
		String name [] = test.getNowMethodParameterNames("メソッド名");
		assertEquals("パラメータ1", name[0]);
		
	}
	
	
	
	/**
	 * 名称を元に型を設定する
	 */
	public void testChangeParamType2 () {
		test.insertMethod("メソッド名");
		test.insertParam("メソッド名", "パラメータ1", DEFINITION_ENUM.DEFINE_ARG);
		
		test.updateParamByName("パラメータ1", TYPE_ENUM.TYPE_STRING);//str_type型をセット
		TYPE_ENUM typeName [] = test.getNowMethodParameterTypes("メソッド名");
		assertEquals(TYPE_ENUM.TYPE_STRING, typeName[0]);//メソッドのパラメータの型が一致
	}
	
	
	/**
	 * パラメータ名として存在しない名称のパラメータへと、型入力が行われた際、
	 * 無名メソッドへとパラメータを追加する。
	 */
	public void testNotExistName () {
		test.insertMethod("メソッド名");
		test.insertParam("メソッド名", "パラメータ1", DEFINITION_ENUM.DEFINE_ARG);
		
		test.updateParamByName("パラメータ2", TYPE_ENUM.TYPE_STRING);//存在しない名称パラメータへとstr_type型をセット、グローバルとして扱われる。
		
		TYPE_ENUM typeName [] = test.getNowMethodParameterTypes("");//匿名メソッド
		assertEquals(TYPE_ENUM.TYPE_STRING, typeName[0]);//メソッドのパラメータの型が一致
		
	}
	
	/**
	 * 引数のないメソッドを取得するテスト
	 * 
	 */
	public void testNoArgMethod () {
		test.insertMethod("メソッド名");
		String s = test.getMethodBody(0);
		String s2 = MIDDLE_METHODHEADER + "メソッド名"+"();";
		assertEquals(s2, s);
	}
	
	
	/**
	 * パラメータを加えたメソッドのテスト
	 * 期待した値が入っているか？
	 */
	public void testInsertParam2 () {
		test.insertMethod("メソッド名");
		test.insertParam("メソッド名", "パラメータ名１", DEFINITION_ENUM.DEFINE_ARG);//引数がある
		test.updateParamByName("パラメータ名１", TYPE_ENUM.TYPE_STRING);
		
		String s = test.getMethodBody(0);
		String s2 = MIDDLE_METHODHEADER + "メソッド名"+"(パラメータ名１);";
		
		assertEquals(s2, s);
		
	}
	
	/**
	 * パラメータを複数加えたメソッドのテスト
	 * 期待した値が入っているか？
	 */
	public void testInsertParam3 () {
		test.insertMethod("メソッド名");
		test.insertParam("メソッド名", "パラメータ名１", DEFINITION_ENUM.DEFINE_ARG);//引数がある
		test.updateParamByName("パラメータ名１", TYPE_ENUM.TYPE_STRING);
		
		test.insertParam("メソッド名", "パラメータ名２", DEFINITION_ENUM.DEFINE_ARG);//引数がある
		test.updateParamByName("パラメータ名２", TYPE_ENUM.TYPE_STRING);
		
		
		String s = test.getMethodBody(0);
		String s2 = MIDDLE_METHODHEADER + "メソッド名"+"(パラメータ名１, パラメータ名２);";
		
		assertEquals(s2, s);
		
	}
	
	/**
	 * 可変長引数を求められた場合の処理をテストする
	 * 
	 * メソッドのオーバーロードになるので、メソッド自体が増えるのを確認する
	 * メソッドの引数が増えるのを確認する
	 * 
	 * その後、メソッドの引数の型が更新されるのを確認する
	 */
	public void testInsertParamByIndex () {
		test.insertMethod("メソッド名");
		test.insertParam("メソッド名", "パラメータ名０", DEFINITION_ENUM.DEFINE_ARG);//引数がある
		
		test.insertParamByIndex("メソッド名", 1, DEFINITION_ENUM.DEFINE_OVERLOAD);//オーバーロードからの可能性がある入力
		
		//メソッド数が増えているはず
		assertTrue("メソッド数が2つではない", test.getMethodNum() == 2);
		
		String s3 = test.getMethodBody(1);
		String s4 = MIDDLE_METHODHEADER + "メソッド名"+"(メソッド名"+TEMPLATE_OVERLOADED+"0, メソッド名"+TEMPLATE_OVERLOADED+"1);";
		
		assertEquals(s4, s3);
	}
	
	/**
	 * 引数を一つも持っていないメソッドに対する、オーバーロードのテスト
	 */
	public void testInsertParamByIndexNoParam () {
		test.insertMethod("メソッド名");
		test.insertParamByIndex("メソッド名", 0, DEFINITION_ENUM.DEFINE_OVERLOAD);//オーバーロードからの可能性がある入力
		
		//メソッド数が増えているはず
		assertTrue("メソッド数が2つではない", test.getMethodNum() == 2);
		
		String s3 = test.getMethodBody(1);
		String s4 = MIDDLE_METHODHEADER + "メソッド名"+"(メソッド名"+TEMPLATE_OVERLOADED+"0);";
		
		assertEquals(s4, s3);
	}
	
	/**
	 * 引数0からindex10のパラメータを参照された場合、などのテスト
	 */
	public void testInsertParamByIndexParamAt10 () {
		test.insertMethod("メソッド名");
		
		test.insertParamByIndex("メソッド名", 10, DEFINITION_ENUM.DEFINE_OVERLOAD);//オーバーロードからの可能性がある入力
		
		assertTrue("メソッド数が2ではない", test.getMethodNum() == 2);
		
		String s3 = test.getMethodBody(1);
		String s4 = MIDDLE_METHODHEADER + "メソッド名"+"("+
		"メソッド名"+TEMPLATE_OVERLOADED+"0,"+
		" メソッド名"+TEMPLATE_OVERLOADED+"1,"+
		" メソッド名"+TEMPLATE_OVERLOADED+"2,"+
		" メソッド名"+TEMPLATE_OVERLOADED+"3,"+
		" メソッド名"+TEMPLATE_OVERLOADED+"4,"+
		" メソッド名"+TEMPLATE_OVERLOADED+"5,"+
		" メソッド名"+TEMPLATE_OVERLOADED+"6,"+
		" メソッド名"+TEMPLATE_OVERLOADED+"7,"+
		" メソッド名"+TEMPLATE_OVERLOADED+"8,"+
		" メソッド名"+TEMPLATE_OVERLOADED+"9,"+
		" メソッド名"+TEMPLATE_OVERLOADED+"10"+");";
		
		assertEquals(s4, s3);
	}
	
	/**
	 * 特定の名称のメソッドを、メソッドノードから削除する
	 */
	public void testRemoveMethodsByNamed () {
		test.insertMethod("メソッド名");
		
		test.removeMethodsByNamed("メソッド名");
		
		assertTrue("メソッド個数が0にならない", test.getMethodNum() == 0);
	}
	
	/**
	 * 特定の名称のメソッドを、メソッドノードから削除する
	 * オーバーロードで複数が存在する場合
	 */
	public void testRemoveMethodsByNamedOverloaded () {
		test.insertMethod("メソッド名");
		
		test.insertParamByIndex("メソッド名", 0, DEFINITION_ENUM.DEFINE_OVERLOAD);//オーバーロードからの可能性がある入力
		
		test.removeMethodsByNamed("メソッド名");
		
		assertTrue("メソッド個数が0にならない", test.getMethodNum() == 0);
	}
	
	
	
	/**
	 * コンストラクタのセッティングテスト、
	 * コンストラクタの名称をマスター名からセットし、
	 * 内部で同名のメソッドを探す。
	 * 
	 * メソッドが確認できた場合、
	 * 	コンストラクタの引数にメソッドの引数をセットする
	 * 	返り値をJSObjectにセットする
	 * 
	 * で、それをFileCreateから実践するには、コンストラクタのヘッダを返すメソッドと、
	 * コンストラクタの内部を返すメソッドがあればOK。
	 */
	public void testConstructor () {
		test.insertMethod("メソッド１");
		
		test.insertParam("メソッド１", "パラメータ１", DEFINITION_ENUM.DEFINE_ARG);
		
		//コンストラクタの引数がセットされているのをチェック
		String header = test.getConstructorMethodHeader("メソッド１");
		
		
		/**
		 * public ProcessingJS_Implements(){
			ProcessingJS_JSObject = setupProcessingJS_JSObject();
			}
		 */
		
		//public メソッド１ (JavaScriptObject パラメータ１)
		assertEquals("public メソッド１Implements(JavaScriptObject パラメータ１)", header);
		
		//コンストラクタの文章内容がセットされているかチェック
		String body = test.getConstructorMethodBody("メソッド１");
		assertEquals("メソッド１JSObject = setupメソッド１JSObject(パラメータ１);", body);
	}
	
	
	public void testConstructorNotFound () {
		test.insertMethod("メソッド１");
		
		test.insertParam("メソッド１", "パラメータ１", DEFINITION_ENUM.DEFINE_ARG);
		
		//コンストラクタの引数がセットされているのをチェック
		String header = test.getConstructorMethodHeader("メソッド２");
		
		
		/**
		 * public ProcessingJS_Implements(){
			ProcessingJS_JSObject = setupProcessingJS_JSObject();
			}
		 */
		
		//public メソッド１ (JavaScriptObject パラメータ１)
		assertEquals("public メソッド２Implements()", header);
		
		//コンストラクタの文章内容がセットされているかチェック
		String body = test.getConstructorMethodBody("メソッド２");
		assertEquals("メソッド２JSObject = setupメソッド２JSObject();", body);
	}
	
}
