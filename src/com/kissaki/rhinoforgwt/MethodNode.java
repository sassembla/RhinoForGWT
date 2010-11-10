package com.kissaki.rhinoforgwt;

import java.util.ArrayList;

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
	public void addpNodeParam(String paramName, int regNum, TYPE_ENUM paramType, DEFINITION_ENUM defineType) throws IllegalArgumentException{
		//リストに既に含まれている場合、を含むかなあ。
		if (isParamAlerdyUse(paramName,regNum)) {//パラメータ名が既に含まれていて、regの値が異なる　or　regの値が一緒 ParameterNodeの仕様
			debug.trace("addpNodeParam_error_同じパラメータ名か同じreg、あるは両方が既に存在している_paramName_"+paramName+"/	regNum_"+regNum);
			throw new IllegalArgumentException("addpNodeParam_error_同じパラメータ名か同じreg、あるは両方が既に存在している_");
		}
		
		ParameterNode newParameterNode = new ParameterNode();
		newParameterNode.setParamName(paramName);
		newParameterNode.setRegNumber(regNum);
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
	 * レジスタの数値を返す
	 * テスト用
	 * @return
	 */
	public int[] getAllParamReg() {
		if (0 < pNodeArrayList.size()) {
			int regs [] = new int[pNodeArrayList.size()];
			for (int i = 0; i < pNodeArrayList.size(); i++) {
				ParameterNode node = pNodeArrayList.get(i);
				regs[i] = node.getRegNumber();
			}
			return regs;
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
	public boolean isParamAlerdyUse(String paramName, int regNum) {
		if (pNodeArrayList.size() == 0) return false;//何も登録されていないので何でもok
		
		for (int i = 0; i < pNodeArrayList.size(); i++) {
			ParameterNode pNodeCheck = pNodeArrayList.get(i);
			if (pNodeCheck.isParamNameAlerdyUse(paramName)) {//同じ名称がある
				return true;
			}
			
			if (pNodeCheck.isParamRegAlerdyUse(regNum)) {//同じregが使用されている
				return true;
			}
		}
		
		return false;
	}

	
	
	
	/**
	 * レジスタの番号から該当するpNodeを割り出し、該当する番号のレジスタを持つpNodeのtypeをparamTypeとして設定する
	 * 
	 * @param regNum
	 * @param paramType
	 * @throws IllegalArgumentException
	 */
	public String addParamTypeByReg(int regNum, TYPE_ENUM paramType) throws IllegalArgumentException {
		if (pNodeArrayList.size() == 0) return null;
			
		for (int i = 0; i < pNodeArrayList.size(); i++) {

			int regs = pNodeArrayList.get(i).getRegNumber();
			if (regs == regNum) {//レジスタ番号の一致を見て、そのパラメータのタイプを入力する
				ParameterNode pNode = pNodeArrayList.get(i);
				String paramName = pNode.setParamType(paramType);
				return paramName;
			}
		}
		
		debug.trace("addParamTypeByReg_存在しないregのタイプを更新している_"+regNum+"/paramType_"+paramType);
		//ここは、追加すればよさそう。後回し。
		throw new IllegalArgumentException("addParamTypeByReg_存在しないregのタイプを更新している_"+regNum+"/paramType_"+paramType);
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
	
	
	
	
	
	
}
