package com.kissaki.rhinoforgwt;

import java.util.ArrayList;

import com.kissaki.rhinoforgwt.CollectionType.TYPE_ENUM;
import com.kissaki.subFrame.Debug;


/**
 * 
 * メソッドの情報を保持するノードリスト
 * メソッド名、
 * パラメータのリストを持つ
 * 
 * 
 * 
 * @author sassembla
 *
 */
public class MethodNode implements CollectionType {
	Debug debug;
	
	String methodName;//メソッド名
	METHOD_TYPE methodType;//メソッドの返り型
	ArrayList<ParameterNode> pNodeArrayList;
	
	/**
	 * コンストラクタ
	 * @param nodeName
	 */
	public MethodNode () {
		debug = new Debug(this);
		pNodeArrayList = new ArrayList<ParameterNode>();
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	
	
	/**
	 * @return the methodType
	 */
	public METHOD_TYPE getMethodType() {
		return methodType;
	}

	/**
	 * @param methodType the methodType to set
	 */
	public void setMethodType(METHOD_TYPE methodType) {
		this.methodType = methodType;
	}

	
	
	
	/**
	 * メソッド中のパラメータ数を取得する
	 * @return the pNode size
	 */
	public int getpNodeSize() {
		return pNodeArrayList.size();
	}

	/**
	 * メソッドへのパラメータ情報の入力
	 * 重複した入力がある場合、エラーを返す
	 * @param paramName
	 * @param regNum
	 * @param paramType
	 * @param defineType 
	 * @throws IllegalArgumentException
	 */
	public void addpNodeParam(String paramName, TYPE_ENUM paramType, DEFINITION_ENUM defineType) throws IllegalArgumentException{
		//リストに既に含まれている場合、を含むかなあ。
		if (isParamAlerdyUse(paramName)) {//パラメータ名が既に含まれているのなら、、、帰る。
			debug.trace("すでに存在するパラメータなので、登録しない_"+paramName);
			return;//throw new IllegalArgumentException("addpNodeParam_error_同じパラメータ名か同じreg、あるは両方が既に存在している_");
		}
		
		debug.trace("セットするパラメータ_"+paramName);
		
		ParameterNode newParameterNode = new ParameterNode();
		newParameterNode.setParamName(paramName);
		newParameterNode.setParamType(paramType);
		newParameterNode.setDefineType(defineType);
		
		pNodeArrayList.add(newParameterNode);
	}
	
	
	
	/**
	 * メソッド内のパラメータ名一覧を返す（確認用）
	 * @return
	 */
	public String [] getAllParamName () {
		
		
		if (0 < pNodeArrayList.size()) {
			String names [] = new String[pNodeArrayList.size()];
			for (int i = 0; i < pNodeArrayList.size(); i++) {
				ParameterNode node = pNodeArrayList.get(i);
				names[i] = node.getParamName();//i+"_/paramName_"+node.getParamName()+"/reg_"+node.getRegNumber()+"/paramType_"+node.getParamType();
			}
			return names;
		} else {
			return null;
		}
		
	}

	public TYPE_ENUM[] getAllParamType() {
		if (0 < pNodeArrayList.size()) {
			TYPE_ENUM names [] = new TYPE_ENUM[pNodeArrayList.size()];
			for (int i = 0; i < pNodeArrayList.size(); i++) {
				ParameterNode node = pNodeArrayList.get(i);
				names[i] = node.getParamType();
			}
			return names;
		} else {
			return null;
		}
	}


	/**
	 * 入力された内容のpNodeが存在していれば、対応する結果を返す
	 * 詳しくはテスト参照
	 * @param paramName
	 * @param regNum
	 * @return
	 */
	public boolean isParamAlerdyUse(String paramName) {
		if (pNodeArrayList.size() == 0) return false;//何も登録されていないので何でもok
		
		for (int i = 0; i < pNodeArrayList.size(); i++) {
			ParameterNode pNodeCheck = pNodeArrayList.get(i);
			if (pNodeCheck.isParamNameAlerdyUse(paramName)) {//同じ名称がある
				return true;
			}
		}
		
		return false;
	}

	
	/**
	 * パラメータの名称から該当するpNodeを割り出し、pNodeのtypeをparamTypeとして設定する
	 * 
	 * @param paramName
	 * @param paramType
	 * @throws IllegalArgumentException
	 */
	public void addParamTypeByName(String paramName, TYPE_ENUM paramType) throws IllegalArgumentException {
		if (pNodeArrayList.size() == 0) return;
		
		for (int i = 0; i < pNodeArrayList.size(); i++) {

			String name = pNodeArrayList.get(i).getParamName();
			if (name.matches(paramName)) {//パラメータ名の一致を見て、そのパラメータのタイプを入力する
				ParameterNode pNode = pNodeArrayList.get(i);
				pNode.setParamType(paramType);
				return;
			}
		}
		
		debug.trace("存在しないparamNameのタイプを更新している_"+paramName+"/paramType_"+paramType);
		
		
		throw new IllegalArgumentException("addParamTypeByName_存在しないparamNameのタイプを更新している_"+paramName+"/paramType_"+paramType);
	}

	/**
	 * このメソッドノードが該当するパラメータを持っていればtrue
	 * @param paramName
	 * @return
	 */
	public ParameterNode hasParam(String paramName) {
		
		for (int i = 0; i < pNodeArrayList.size(); i++) {

			String name = pNodeArrayList.get(i).getParamName();
			if (name.matches(paramName)) {//パラメータ名の一致を見て、そのパラメータのタイプを入力する
				return pNodeArrayList.get(i);
			}
		}
		return null;
	}

	/**
	 * 特定のパラメータのタイプを返す、そのパラメータを持っていなければ、nullが返る。
	 * @param string
	 * @return
	 */
	public TYPE_ENUM getParamType(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	
}
