package com.kissaki.rhinoforgwt;

import java.util.ArrayList;

import com.kissaki.rhinoforgwt.CollectionType.DEFINITION_ENUM;
import com.kissaki.rhinoforgwt.CollectionType.TYPE_ENUM;
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
 * ただし、匿名メソッドは読み込み対象としない。
 * （そもそも単独で呼ばれる想定でないだろうし。）
 * @author sassembla
 *
 */
public class CollectionNode implements CollectionType {

	
	Debug debug = null;

	ArrayList<MethodNode> methodNodeArrayList;//メソッドのノード、下位にパラメータのノードがある。
	
	
	/**
	 * コンストラクタ
	 * @param string
	 */
	public CollectionNode (String string) {
		debug = new Debug(this);
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
	
	


	
	
	/**
	 * メソッドとして検出された文字列をメソッド名として保存する
	 * @param string
	 */
	public void insertMethod(String string) {
		
//		debug.trace("insertMethod_"+string);
//		debug.trace("before_"+getNowMethodName());
		if (!isMethodNameAlreadyExist(string)) {//未登録のメソッドを登録する、
			debug.trace("new_Method_"+string);
			MethodNode mNode = new MethodNode();
			mNode.setMethodName(string);
			methodNodeArrayList.add(mNode);
		}
		
	}
	
	
	/**
	 * メソッド名に対して、パラメータを追加する
	 * パラメータを保持しているメソッドが存在しなければ、グローバル上の型をアップデートする。
	 * パラメータが存在しなければ
	 */
	public void insertParam(String methodName, String paramName, DEFINITION_ENUM defineType) throws IllegalArgumentException {
		
		//debug.trace("nowMethodName_"+getNowMethodName()+"/insertParam_"+paramName+"/reg_"+regNum+"/defineType_"+defineType);
		//現在のメソッドについて、既にパラメータ名がセットされていないか、チェックを行う
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			
			if (mNode.getMethodName().equals(methodName)) {//メソッドノード確定、あとは放り込む
				mNode.addpNodeParam(paramName, TYPE_ENUM.TYPE_JAVASCRIPTOBJECT, defineType);
			}
		}
		
		//どのメソッドも持っていないパラメータであれば、グローバルに追加する。
		//TODO
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
			if (mNode.hasParam(paramName) != null) {
				mNode.updateParam(paramName, paramType);
				return;
			}
		}
		
		//どのメソッドも引数として所有していない場合、匿名メソッドの物として処理する
		//グローバルパラメータ
		insertParamToGlobal(paramName, paramType);
	}


	/**
	 * グローバルな空間(無名、"")のメソッドを作成し、その中にargではなくparamとして放り込む
	 * @param paramName
	 * @param paramType
	 */
	private void insertParamToGlobal(String paramName, TYPE_ENUM paramType) {
		insertMethod("");
		insertParam("", paramName, DEFINITION_ENUM.DEFINE_PARAM);
		updateParamByName(paramName, paramType);
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
			if (mNode.getMethodName().equals(string2)) return true;
		}
		
		return false;
	}
	
	


	/**
	 * 現在フォーカスしているメソッドが持っているパラメータ数を返す
	 * @param methodName 
	 * @return
	 */
	public int getNowMethodParameterNum(String methodName) {
		if (methodNodeArrayList.size() == 0) return 0;//メソッドが一つもなかったら0
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			if (mNode.getMethodName().equals(methodName)) {
				return mNode.getpNodeSize();
			}
		}
		
		return 0;
	}


	/**
	 * メソッドが持っているパラメータの名称一覧を返す
	 * @param methodName 
	 * @return
	 */
	public String [] getNowMethodParameterNames(String methodName) {
		if (methodNodeArrayList.size() == 0) return null;//メソッドが一つもなかったら0
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			if (mNode.getMethodName().equals(methodName)) {
				return mNode.getAllParamName();
			}
		}
		return null;
	}


	/**
	 * このメソッドにセットされている引数の型一覧を返す
	 * @param methodName 
	 * @return
	 */
	public TYPE_ENUM[] getNowMethodParameterTypes(String methodName) {
		if (methodNodeArrayList.size() == 0) return null;//メソッドが一つもなかったら0
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			if (mNode.getMethodName().equals(methodName)) {
				return mNode.getAllParamType();
			}
		}
		return null;
	}






	/**
	 * 現在フォーカスされているメソッドの匿名/有名タイプを指定する。
	 * @param メソッド名 
	 * @param タイプ
	 */
	public void setMethodType(String methodName, METHOD_TYPE methodType) {
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			
			if (mNode.methodName == methodName) {//メソッドノード確定、あとは放り込む
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
		
		
		//debug.trace("s2_"+s2);
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
		s1.append("	"+ TENPLATE_OBJECT +mNode.getMethodName()+"("+ s2.toString() +");");
		return s1.toString();
	}




	/**
	 * 指定したパラメータを持っているメソッドを返す
	 * @param paramName
	 * @return
	 */
	public MethodNode getMethodHasParam(String paramName) {
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			if (mNode.hasParam(paramName) != null) {
				return mNode;
			}
		}
		return null;
	}



	
}
