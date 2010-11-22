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
	public CollectionNode () {
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
		
	}
	
	/**
	 * パラメータの要素をインデックスを元に更新する
	 * @param methodName
	 * @param paramIndex
	 * @param defineType
	 * @throws IllegalArgumentException
	 */
	public void insertParamByIndex(String methodName, int paramIndex, DEFINITION_ENUM defineType) throws IllegalArgumentException {
		
		if (methodName.equals(METHOD_NONANE)) return;
		
		MethodNode m = null;
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode mNode = methodNodeArrayList.get(i);
			
			if (mNode.getMethodName().equals(methodName)) {//メソッドノード確定、あとは放り込む
				if (m == null || m.getpNodeSize() < mNode.getpNodeSize()) {//パラメータ数がより多いものを選ぶ
					m = mNode;
				}
				
				//引数0のパラメータが指定された時、パラメータ数がすでに1のものがあったら帰る。
				if (paramIndex+1 == mNode.pNodeArrayList.size()) {//指定したインデックスの値が同じなので、オーバーロードする必然性は無いと判断
					//TODO　型でもオーバーロードする必要はあるのだが、まだそこまでやってない。
					return;
				}
			}
		}
		
		
		/**
		 * 同名のメソッドを探査した時点で、該当する引数のメソッドが存在しなければ、オーバーロードを開始する。
		 */
		if (m != null) {
			//オーバーロード確定。メソッドノード自体をコピーする。
			MethodNode mNode2 = m.getCopyWithoutParam();
			
			for (int j = 0; j < paramIndex+1; j++) {
				String paramName = mNode2.getMethodName() + TEMPLATE_OVERLOADED + j;
				
				//オーバーロードしたメソッドに、パラメータを追加する
				mNode2.addpNodeParam(paramName, TYPE_ENUM.TYPE_JAVASCRIPTOBJECT, defineType);
			}
			
			//オーバーロードしたメソッドをリストに追加する
			methodNodeArrayList.add(mNode2);
			
			return;
		}
		
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
	 * メソッド名と、設定されているパラメータの宣言文字列 void メソッド名(A a, B b, C c, ,,) を取得する
	 * TODO メソッドの型がすべてvoid型
	 * @param i
	 * @return
	 */
	public String getMethodHeader (int i) {
		MethodNode mNode = methodNodeArrayList.get(i);
		StringBuffer s = new StringBuffer();
		StringBuffer s2 = new StringBuffer();
		for (int j = 0; j < mNode.getpNodeSize(); j++) {
			if (j != 0) s2.append(", ");
			s2.append(mNode.pNodeArrayList.get(j).getParamString()+" "+mNode.pNodeArrayList.get(j).getParamName());//型とパラメータ名
		}
		
		
		//debug.trace("s2_"+s2);
		s.append(STRING_KEY_PUBLIC + " " +"void"+" "+mNode.methodName+ "("+s2.toString()+")");
		
		return s.toString();
		
	}
	

	/**
	 * メソッド内の記述、設定されているパラメータの実行文字列 メソッド名(a,b,c,,,); を取得する
	 * @param i
	 * @return
	 */
	public String getMethodBody(int i) {
		MethodNode mNode = methodNodeArrayList.get(i);
		
		StringBuffer s2 = new StringBuffer();
		for (int j = 0; j < mNode.getpNodeSize(); j++) {
			if (j != 0) s2.append(", ");
			s2.append(mNode.pNodeArrayList.get(j).getParamName());
		}
		
		StringBuffer s1 = new StringBuffer();
		s1.append(MIDDLE_METHODHEADER + mNode.getMethodName() + "("+ s2.toString() +");");
		return s1.toString();
	}
	
	/**
	 * Nativeメソッドのヘッダーを取得する
	 * @param i
	 * @return
	 */
	public String getNativeMethodHeader(int i) {
		
		MethodNode mNode = methodNodeArrayList.get(i);
		StringBuffer s = new StringBuffer();
		
		
		
		StringBuffer s2 = new StringBuffer();
		for (int j = 0; j < mNode.getpNodeSize(); j++) {
			if (j != 0) s2.append(", ");
			s2.append(mNode.pNodeArrayList.get(j).getParamString() + " " + mNode.pNodeArrayList.get(j).getParamName());
		}
		
		String returnType = CollectionType.STRING_KEY_JAVASCRIPTOBJECT;
		s.append(STRING_KEY_PRIVATE + " " + STRING_KEY_NATIVE + " " + returnType + " " + MIDDLE_METHODHEADER + mNode.methodName + "("+s2.toString()+")" + STRING_KEY_JSNI_HEADER);
		
		return s.toString();
	}

	public String getNativeMethodBody(String packages, String implementName, int i) {
		MethodNode mNode = methodNodeArrayList.get(i);
		StringBuffer s = new StringBuffer();
		
		StringBuffer s2 = new StringBuffer();
		for (int j = 0; j < mNode.getpNodeSize(); j++) {
			if (j != 0) s2.append(", ");
			s2.append(mNode.pNodeArrayList.get(j).getParamName());
		}
		
		s.append("this.@" + packages + "." + implementName + "::" + TENPLATE_OBJECT + "()." + mNode.methodName + "(" + s2.toString() + ");");//メソッドと、
		
		return s.toString();
	}
	
	
	
	/**
	 * Nativeメソッドのフッターをつける
	 * @return
	 */
	public String getNativeMethodFooter() {
		return STRING_KEY_JSNI_FOOTER;
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


	/**
	 * 特定の名称のメソッドをCollectionNodeから削除する
	 * @param methodNameShouldErase
	 */
	public void removeMethodsByNamed(String methodNameShouldErase) {
		int size;
		while (true) {
			size = getMethodNum();//サイズの更新
			//nodeのサイズが変わりながら、うごくはず。
			for (int i = 0; i < size; i++) {
				MethodNode m = methodNodeArrayList.get(i);
				if (m.getMethodName().equals(methodNameShouldErase)) {
					methodNodeArrayList.remove(i);
					break;
				}
			}
			if (getMethodNum() == size) {//開始時のメソッド数と変化していなければ、もう消すものがない、という事
				break;
			}
		}
	}


	/**
	 * 設定した文字列のメソッドが存在していれば、引数を設定したヘッダーを返す
	 * 存在しなければ、空の引数のヘッダーを返す
	 * @param constructorName
	 * @return
	 */
	public String getConstructorMethodHeader(String constructorName) {
		StringBuffer s2 = new StringBuffer();
		
		s2.append(getConstructorMethodBodyParametersTo(constructorName));
		
		String header = STRING_KEY_PUBLIC+" "+constructorName+STRING_FILEFOOTER_IMPLEMENTS+"(" + s2.toString() + ")";
		return header;
	}

	/**
	 * 該当するメソッド名から、パラメータ型、パラメータ名の文字列を取得する
	 * @param constructorName
	 * @return
	 */
	public String getConstructorMethodBodyParametersTo(String constructorName) {
		StringBuffer s2 = new StringBuffer();
		
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode m = methodNodeArrayList.get(i);
			
			if (constructorName.equals(m.getMethodName())) {
				//メソッドのパラメータ列を取得する
				for (int j = 0; j < m.getpNodeSize(); j++) {
					if (j != 0) s2.append(", ");
					s2.append(m.pNodeArrayList.get(j).getParamString() + " " + m.pNodeArrayList.get(j).getParamName());
				}
				break;
			}
		}
		return s2.toString();
	}



	/**
	 * 指定したメソッド名のコンストラクタのボディーを返す
	 * @param string
	 * @return
	 */
	public String getConstructorMethodBody(String constructorName) {
		StringBuffer s2 = new StringBuffer();
		
		s2.append(getConstructorBodyParametersTo(constructorName));
		
		String body = "	"+constructorName+STRING_KEY_JSOBJECT+" = "+STRING_KEY_SETJAVASCRIPTMETHOD_HEADER + constructorName+STRING_KEY_JSOBJECT+"(" + s2.toString() + ");";
		return body;
	}


	/**
	 * 該当するメソッドの引数の、名称部分だけを取得する
	 * @param s2
	 * @param constructorName
	 */
	public String getConstructorBodyParametersTo(String constructorName) {
		StringBuffer s2 = new StringBuffer();
		for (int i = 0; i < methodNodeArrayList.size(); i++) {
			MethodNode m = methodNodeArrayList.get(i);
			
			if (constructorName.equals(m.getMethodName())) {//public メソッド１ (JavaScriptObject パラメータ１)
				//メソッドのパラメータ列を取得する
				
				for (int j = 0; j < m.getpNodeSize(); j++) {
					if (j != 0) s2.append(", ");
					s2.append(m.pNodeArrayList.get(j).getParamName());
				}
				break;

			}
		}
		return s2.toString();
	}
	
}
