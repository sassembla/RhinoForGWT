package com.kissaki.test;

import junit.framework.TestCase;

import com.kissaki.rhinoforgwt.MethodNode;
import com.kissaki.rhinoforgwt.CollectionType.DEFINITION_ENUM;
import com.kissaki.rhinoforgwt.CollectionType.METHOD_TYPE;
import com.kissaki.rhinoforgwt.CollectionType.TYPE_ENUM;
import com.kissaki.subFrame.Debug;

public class MethodNodeTest extends TestCase {
	Debug debug = null;
	MethodNode test = null;
	
	@Override
	protected void setUp() throws Exception {
		debug = new Debug(this);
		test = new MethodNode();
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		test = null;
		debug = null;
	}
	

	
	public void testSetMethodName() {
		test.setMethodName("メソッド名前");
		assertEquals("メソッド名前", test.getMethodName());
	}
	
	
	public void testSetMethodType(String methodType) {
		test.setMethodType(METHOD_TYPE.METHOD_NONAME);
		assertEquals(METHOD_TYPE.METHOD_NONAME, test.getMethodType());
	}	
	
	
	/**
	 * サイズを取得する
	 * @return the pNode size
	 */
	public void testGetpNodeSize() {
		for (int i = 0; i < 10; i++) {
			test.addpNodeParam("メソッド名_"+i, TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
		}
		
		assertEquals(10, test.getpNodeSize());
	}
	
	
	public void testAddpNodeParam () {
		
		test.addpNodeParam("テスト用のパラメータ名", TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);//成立する
		
		/**
		 * チェックの優先順位はどうする？
		 * 名前の重複は、regの重複にならない場合別物で同じ名称のargがあるとして、エラーをはく
		 * 
		 * 同一名称以外でのregの重複はエラーを返す
		 * 
		 * 型の重複は見ない
		 */
		try {
			test.addpNodeParam("テスト用のパラメータ名", TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
			assertTrue("成立してはいけない_名称が同じでregが異なるケース",false);
		} catch (Exception e) {
			
		}
	}
	
	
	public void testAddpNodeParam3 () {
		test.addpNodeParam("テスト用のパラメータ名1", TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
		
		try {
			test.addpNodeParam("テスト用のパラメータ名1", TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
			assertTrue("成立してはいけない_名称とregが同じケース",false);
		} catch (Exception e) {
			
		}
	}
	
	
	/**
	 * メソッド内のパラメータ名一覧を返す（確認用）
	 * @return
	 */
	public void testGetAllParamName () {
		
		String name [] = {
			"パラメータ１",
			"パラメータその２",
			"No3パラメータ",
			
		};

		
		TYPE_ENUM type [] = {
				TYPE_ENUM.TYPE_STRING,
				TYPE_ENUM.TYPE_BOOLEAN,
				TYPE_ENUM.TYPE_DOUBLE,
		};
		
		for (int i = 0; i < name.length; i++) {
			test.addpNodeParam(name[i], type[i], DEFINITION_ENUM.DEFINE_ARG);
		}
		
		
		String named [] = test.getAllParamName();
		
		for (int i = 0; i < name.length; i++) {
			assertEquals(name[i], named[i]);
		}
		
		TYPE_ENUM types [] = test.getAllParamType();
		
		for (int i = 0; i < name.length; i++) {
			assertEquals(type[i], types[i]);
		}
		
		
	}
	
	
	
	public void testIsParamAlerdyUseF0 () {
		//パラメータがセットされていないケース
		boolean b = test.isParamAlerdyUse("パラメータ名0");
		assertEquals(false, b);//まだ何も無いのでfalseが帰ってくる筈 0
	}
	
	public void testIsParamAlerdyUseT1 () {
		test.addpNodeParam("パラメータ名1", TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
		boolean b = test.isParamAlerdyUse("パラメータ名1");
		assertEquals(true, b);//同じパラメータ名の型、regナンバーなので、既に或るという意味でtrue 1
	}
	

	
    
    public void testHasParam() {
    	test.addpNodeParam("パラメータ",  TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
		
    	boolean b = false;
    	if (test.hasParam("パラメータ") != null) {
    		b = true;
    	}
    	assertTrue("パラメータが有る事になってません",b);
	}
    
}
