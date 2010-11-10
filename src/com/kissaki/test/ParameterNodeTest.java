package com.kissaki.test;

import junit.framework.TestCase;

import com.kissaki.rhinoforgwt.CollectionType;
import com.kissaki.rhinoforgwt.ParameterNode;
import com.kissaki.subFrame.Debug;

/**
 * ノードテスト
 * やっぱ先にテストクラス書くべきだな
 * @author sassembla
 *
 */
public class ParameterNodeTest extends TestCase implements CollectionType {
	Debug debug = null;
	ParameterNode test = null;
	
	
	@Override
	protected void setUp() throws Exception {
		debug = new Debug(this);
		test = new ParameterNode();
	}
	
	@Override
	protected void tearDown() throws Exception {
		test = null;
		debug = null;
	}
	
	/**
	 * パラメータ名が既に使用されているか否かをチェックするメソッド
	 */
	public void testIsParamNameAlerdyUse () {
		
		
		boolean b = test.isParamNameAlerdyUse("パラメータ名");
		assertEquals(false, b);
		
		
		test.setParamName("パラメータ名2");
		b = test.isParamNameAlerdyUse("パラメータ名2");
		assertEquals(true, b);
		
		
		test.setParamName("パラメータ名3");
		b = test.isParamNameAlerdyUse("パラメータ名2");
		assertEquals(false, b);
		
	}
	
	public void testIsParamRegAlreadyUse () {
		boolean b = test.isParamRegAlerdyUse(100);
		assertEquals(false, b);
		
		test.setRegNumber(101);
		b = test.isParamRegAlerdyUse(101);
		assertEquals(true, b);
		
		
		test.setRegNumber(102);
		b = test.isParamRegAlerdyUse(100);
		assertEquals(false, b);
	}
	
	/**
	 * タイプ型へのセットのテスト、型の転落を防ぐ（より下位のクラスに落ち着くようにする）
	 */
	public void testSetTypeParam () {
		
		test.setParamType(TYPE_ENUM.TYPE_JAVASCRIPTOBJECT);//弱
		test.setParamType(TYPE_ENUM.TYPE_STRING);//強
		
		TYPE_ENUM nowType = test.getParamType();
		
		assertEquals(TYPE_ENUM.TYPE_STRING, nowType);
	}
	/**
	 * タイプ型へのセットのテスト、型の転落を防ぐ（より下位のクラスに落ち着くようにする）
	 */
	public void testSetTypeParam2 () {
		
		test.setParamType(TYPE_ENUM.TYPE_STRING);//強
		test.setParamType(TYPE_ENUM.TYPE_JAVASCRIPTOBJECT);//弱
		
		TYPE_ENUM nowType = test.getParamType();
		
		assertEquals(TYPE_ENUM.TYPE_STRING, nowType);
	}
	
	
}
