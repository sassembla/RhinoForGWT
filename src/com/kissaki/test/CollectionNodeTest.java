package com.kissaki.test;

import junit.framework.TestCase;

import com.kissaki.rhinoforgwt.CollectionNode;
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
public class CollectionNodeTest extends TestCase {
	Debug debug = null;
	CollectionNode test = null;
	
	@Override
	protected void setUp() throws Exception {
		debug = new Debug(this);
		test = new CollectionNode("独自の名称をセットする");
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
		assertTrue("メソッド名が一致しない", node.getMethodName() == "メソッド名");
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
	 * パラメータ名として存在しない名称のパラメータへと、型入力が行われた際にエラーを出す
	 */
	public void testNotExistName () {
		test.insertMethod("メソッド名");
		test.insertParam("メソッド名", "パラメータ1", DEFINITION_ENUM.DEFINE_ARG);
		
		try {
			test.updateParamByName("パラメータ2", TYPE_ENUM.TYPE_STRING);//存在しない名称パラメータへとstr_type型をセット
			assertTrue("成立してはいけない_パラメータ名として存在しない名称のパラメータへと、型入力",false);
		} catch (Exception e) {
			
		}
	}
}
