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
			test.addpNodeParam("メソッド名_"+i, 100+i, TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
		}
		
		assertEquals(10, test.getpNodeSize());
	}
	
	
	public void testAddpNodeParam () {
		
		test.addpNodeParam("テスト用のパラメータ名", 0, TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);//成立する
		
		/**
		 * チェックの優先順位はどうする？
		 * 名前の重複は、regの重複にならない場合別物で同じ名称のargがあるとして、エラーをはく
		 * 
		 * 同一名称以外でのregの重複はエラーを返す
		 * 
		 * 型の重複は見ない
		 */
		try {
			test.addpNodeParam("テスト用のパラメータ名",10, TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
			assertTrue("成立してはいけない_名称が同じでregが異なるケース",false);
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 同じレジスタへの入力が別パラメータで存在する場合、エラーを出す
	 * →出さない状態に変更
	 */
//	public void testAddpNodeParam2 () {
//		test.addpNodeParam("テスト用のパラメータ名1", 10, TYPE_LIST.TYPE_STRING);
//		
//		try {
//			test.addpNodeParam("テスト用のパラメータ名2", 10, TYPE_LIST.TYPE_STRING);
//			assertTrue("成立してはいけない_名称が違ってregが同じケース",false);
//		} catch (Exception e) {
//			
//		}
//	}
	
	public void testAddpNodeParam3 () {
		test.addpNodeParam("テスト用のパラメータ名1", 10, TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
		
		try {
			test.addpNodeParam("テスト用のパラメータ名1", 10, TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
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
		
		int reg [] = {
			100,
			200,
			300
		};
		
		TYPE_ENUM type [] = {
				TYPE_ENUM.TYPE_STRING,
				TYPE_ENUM.TYPE_BOOLEAN,
				TYPE_ENUM.TYPE_DOUBLE,
		};
		
		for (int i = 0; i < name.length; i++) {
			test.addpNodeParam(name[i], reg[i], type[i], DEFINITION_ENUM.DEFINE_ARG);
		}
		
		
		String named [] = test.getAllParamName();
		
		for (int i = 0; i < name.length; i++) {
			assertEquals(name[i], named[i]);
		}
		
		TYPE_ENUM types [] = test.getAllParamType();
		
		for (int i = 0; i < name.length; i++) {
			assertEquals(type[i], types[i]);
		}
		
		int regs [] = test.getAllParamReg();
		
		for (int i = 0; i < name.length; i++) {
			assertEquals(reg[i], regs[i]);
		}
		
	}
	
	/**
	 * ケース分けで言うと、regが同じケース(2)、名称が同じケース(2)、　で、４パターン
	 * 4パターンに対して、結果は、trueが2つ、falseが2つ。
	 * 
	 * 設定値無し		f0
	 * 
	 * 				t2
	 * reg	あり	なし
	 * name	あり	なし
	 * 		t1	f3	t4
	 */
	public void testIsParamAlerdyUseF0 () {
		//パラメータがセットされていないケース
		boolean b = test.isParamAlerdyUse("パラメータ名0",100);
		assertEquals(false, b);//まだ何も無いのでfalseが帰ってくる筈 0
	}
	
	public void testIsParamAlerdyUseT1 () {
		test.addpNodeParam("パラメータ名1", 100, TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
		boolean b = test.isParamAlerdyUse("パラメータ名1",100);
		assertEquals(true, b);//同じパラメータ名の型、regナンバーなので、既に或るという意味でtrue 1
	}
	
	public void testIsParamAlerdyUseT2 () {
		test.addpNodeParam("パラメータ名2", 100, TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
		boolean b = test.isParamAlerdyUse("パラメータ名2", 101);
		assertEquals(true, b);//同じパラメータ名の型、異なるregナンバーなので、true t2
	}
	
	public void testIsParamAlerdyUseF3() {
		test.addpNodeParam("パラメータ名3", 100, TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
		boolean b = test.isParamAlerdyUse("パラメータ名3ダッシュ", 101);
		assertEquals(false, b);//異なるパラメータ名の型、異なるregナンバーなので、false f3
	}
	
	public void testIsParamAlerdyUseF4() {
		test.addpNodeParam("パラメータ名4", 100, TYPE_ENUM.TYPE_STRING, DEFINITION_ENUM.DEFINE_ARG);
		boolean b = test.isParamAlerdyUse("パラメータ名4ダッシュ", 100);
		assertEquals(true, b);//異なるパラメータ名の型、同じregナンバーなのだが、ありえるので、true t4
	}
	
	
    
    
    
}
