package com.kissaki.rhinoforgwt;

import java.util.ArrayList;

import com.kissaki.subFrame.Debug;

/**
 * コレクションを収集する受け皿になるクラス
 * 
 * 枝葉であるNodeクラスを集積するのが役割。
 * 収集する条件の振り分けは行わない（＝匿名関数などは飛び込んでこない！ 渡す前に外側で処理する）
 * 
 * 匿名メソッドによって既存のメソッドの型が更新されない前提でやっているが、
 * あり得る事態なので、匿名メソッドの入力は、入力側の機構に任せられるようにしてある。
 * 
 * ただし、匿名メソッドはprivateとして実装する、かも。
 * （そもそも単独で呼ばれる想定でないだろうし。）
 * 
 * 
 * オブジェクトのメインクラスを返すための判別が非常に苦労しそう。うーーーーん。
 * ラップして実行する、というのが根本だからなあ。
 * @author sassembla
 *
 */
public class CollectionNode implements CollectionType {
	
	
	Debug debug = null;

	ArrayList<MethodNode> methodNodeArrayList;//メソッドのノード、下位にパラメータのノードがある。
	
	
	/**
	 * コンストラクタ
	 * @param string2 
	 */
	public CollectionNode (String string2) {
		debug = new Debug(this);
		debug.trace("名前がついています_"+string2);
		methodNodeArrayList = new ArrayList<MethodNode>();
	}

	
	static String mainName = null;
	
	/**
	 * メイン関数の名称を入力する
	 * @param name
	 */
	public void setMainName (String name) {
		mainName = name;
	}
	
	
	
	/**
	 * メイン関数の名称を返す。
	 *動判判定と入力は未完成、、もしかしたら無理かも。
	 * @return
	 */
	public String getMainName() {
		debug.assertTrue(mainName != null, "名前がNull メイン関数の名称取得に失敗、パース段階でエラーがでているかも");
		return mainName;
	}
	
	
	static String nowMethodName = "";//現在フォーカスしているメソッドの名称

	/**
	 * 現在フォーカスしているメソッド名のセットを行う
	 * @param string
	 */
	private void setNowMethodName(String string) {
		nowMethodName = string;
	}
	
	/**
	 * 現在フォーカスしているメソッド名を返す
	 * @return
	 */
	public String getNowMethodName() {
		return nowMethodName;
	}
	
	
	/**
	 * メソッドとして検出された文字列をメソッド名として保存する
	 * @param string
	 */
	public void insertMethod(String string) {
		
//		debug.trace("insertMethod_"+string);
//		debug.trace("before_"+getNowMethodName());
		if (!isMethodNameAlreadyExist(string)) {//未登録のメソッドを登録する、
			
			setNowMethodName(string);
			//debug.trace("new Method_"+getNowMethodName());
			MethodNode mNode = new MethodNode();
			mNode.setMethodName(string);
			methodNodeArrayList.add(mNode);
		}
		
	}
	
	
	/**
	 * 現在フォーカスしているメソッド名に対して、パラメータとレジスタを追加する
	 * パラメータ名、レジスタが既に使用されていたらエラーを返す
	 * @param string
	 * @param i
	 */
	public void insertParam(String paramName, int regNum, DEFINITION_ENUM defineType) throws IllegalArgumentException {
		
		//debug.trace("nowMethodName_"+getNowMethodName()+"/insertParam_"+paramName+"/reg_"+regNum+"/defineType_"+defineType);
		//現在のメソッドについて、既にパラメータ名がセットされていないか、チェックを行う
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			
			if (mNode.getMethodName().matches(getNowMethodName())) {//メソッドノード確定、あとは放り込む
				mNode.addpNodeParam(paramName, regNum, TYPE_ENUM.TYPE_JAVASCRIPTOBJECT, defineType);
			}
		}
		
	}
	
	
	
	/**
	 * 型情報のセット1 レジスタを使った型判別の入力
	 * 
	 * 既に定義されている型に対しては、JavaScriptObject < その他の型、の優先順位で優先する。
	 * 順位が下回る場合は型情報を更新しない。
	 * 
	 * 登場していないParamの入力が発生したら、IllegalArgumentException
	 * @param regNum レジスタ番号
	 * @param paramType　型名
	 */
	public void updateParamByReg(int regNum, TYPE_ENUM paramType) throws IllegalArgumentException{
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			
			if (mNode.getMethodName().matches(getNowMethodName())) {//メソッドノード確定、あとは放り込む
				String paramName = mNode.addParamTypeByReg(regNum, paramType);//レジスタ番号を指定して、引数のtypeを設定する
				//debug.trace("nowMethodName_2_"+getNowMethodName()+"/updateParamByReg_"+paramName+"/reg_"+regNum+"/type_"+paramType);
				
				return;
			}
		}
		
		throw new IllegalArgumentException("updateParamByReg_今まで存在しないパラメータをアップデートしようとしている_"+"名称不明、この時点では或る筈なので後で読もう"+"/reg_"+regNum+"/type_"+paramType);
	}
	
	
	
	/**
	 * 型情報のセット2 文字列の使われ方から推測した方判別の入力
	 * 
	 * 既に定義されている型に対しては、JavaScriptObject < その他の型、の優先順位で優先する。
	 * 順位が下回る場合は型情報を更新しない。
	 * 
	 * 登場していないParamの入力が発生したら、IllegalArgumentException
	 * @param paramName パラメータ名
	 * @param paramType パラメータ型名
	 * @throws IllegalArgumentException
	 */
	public void updateParamByName(String paramName, TYPE_ENUM paramType) throws IllegalArgumentException{

		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			
			if (mNode.getMethodName().matches(getNowMethodName())) {//メソッドノード確定、あとは放り込む
				mNode.addParamTypeByName(paramName, paramType);//レジスタ番号を指定して、引数のtypeを設定する
				//debug.trace("nowMethodName_3_"+getNowMethodName()+"/updateParamByName_"+paramName+"/type_"+paramType);
				return;
			}
		}

		throw new IllegalArgumentException("updateParamByName_今まで存在しないパラメータをアップデートしようとしている_"+paramType);
	}

	
	/**
	 * 登録されているメソッド名を返す
	 * メソッド名にメインのインスタンス作成メソッドは含まれない。
	 * @return
	 */
	public int getMethodNum() {
		return methodNodeArrayList.size();
	}
	

	
	

	/**
	 * 保持しているメソッド名がmethodNodeArrayListに含まれていればtrue、いなければfalseを返す
	 * 空ならfalseを返す
	 * @param string2
	 * @return
	 */
	public boolean isMethodNameAlreadyExist(String string2) {
		if (methodNodeArrayList.size() == 0) return false;
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			if (mNode.getMethodName().matches(string2)) return true;
		}
		
		return false;
	}


	/**
	 * 現在フォーカスしているメソッドが持っているパラメータ数を返す
	 * @return
	 */
	public int getNowMethodParameterNum() {
		if (methodNodeArrayList.size() == 0) return 0;//メソッドが一つもなかったら0
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			if (mNode.getMethodName().matches(getNowMethodName())) {
				return mNode.getpNodeSize();
			}
		}
		
		return 0;
	}


	/**
	 * メソッドが持っているパラメータの名称一覧を返す
	 * @return
	 */
	public String [] getNowMethodParameterNames() {
		if (methodNodeArrayList.size() == 0) return null;//メソッドが一つもなかったら0
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			if (mNode.getMethodName().matches(getNowMethodName())) {
				return mNode.getAllParamName();
			}
		}
		return null;
	}


	/**
	 * このメソッドにセットされている引数の型一覧を返す
	 * @return
	 */
	public TYPE_ENUM[] getNowMethodParameterTypes() {
		if (methodNodeArrayList.size() == 0) return null;//メソッドが一つもなかったら0
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			if (mNode.getMethodName().matches(getNowMethodName())) {
				return mNode.getAllParamType();
			}
		}
		return null;
	}






	/**
	 * 現在フォーカスされているメソッドの匿名/有名タイプを指定する。
	 * @param methodNoname
	 */
	public void setMethodType(METHOD_TYPE methodType) {
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			
			if (mNode.methodName == getNowMethodName()) {//メソッドノード確定、あとは放り込む
				mNode.setMethodType(methodType);
			}
		}
	}
	
	/**
	 * メソッドの入ったノードを取得する
	 * 
	 * @param i
	 * @return
	 */
	public String getMethodNode (int i) {
		MethodNode mNode = methodNodeArrayList.get(i);
		StringBuffer s = new StringBuffer();
		StringBuffer s2 = new StringBuffer();
		for (int j = 0; j < mNode.getpNodeSize(); j++) {
			if (j != 0) s2.append(", ");
			s2.append(mNode.pNodeArrayList.get(j).getParamType()+" "+mNode.pNodeArrayList.get(j).getParamName());
		}
		
		
		debug.trace("s2_"+s2);
		s.append("void"+" "+mNode.methodName+ " ("+s2.toString()+")");
		
		return s.toString();
		
	}



	public String getMethodExec(int i) {
		MethodNode mNode = methodNodeArrayList.get(i);
		
		StringBuffer s2 = new StringBuffer();
		for (int j = 0; j < mNode.getpNodeSize(); j++) {
			if (j != 0) s2.append(", ");
			s2.append(mNode.pNodeArrayList.get(j).getParamName());
		}
		
		StringBuffer s1 = new StringBuffer();
		s1.append("	getIstanceOfJSObject()."+mNode.getMethodName()+"("+ s2.toString() +");");
		return s1.toString();
	}
	
	
}
