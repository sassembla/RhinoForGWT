package com.kissaki.rhinoforgwt;



/**
 * メソッドのパラメータ一つずつに設定するノード
 * 
 * パラメータ/レジスタ名/引数の型、の３つを入力するStringの配列　
 * @author sassembla
 *
 */
public class ParameterNode implements CollectionType {
	String paramName;//パラメータ名
	TYPE_ENUM paramType;//パラメータ型 初期値は
	DEFINITION_ENUM defineType;//	arg/引数　か、param/パラメータか
	
	/**
	 * コンストラクタ
	 */
	public ParameterNode () {
		setParamName(NAME_NULL);//無名として登録
		setParamType(CollectionType.TYPE_ENUM.TYPEENUM_NULL);//無名として登録
		setDefineType(CollectionType.DEFINITION_ENUM.NO_DEFINITION);//型が無い状態で登録　argかparam
	}

	/**
	 * @return the paramName
	 */
	public String getParamName() {
		return paramName;
	}



	/**
	 * @return the paramType
	 */
	public TYPE_ENUM getParamType() {
		return paramType;
	}
	
	/**
	 * argの型を文字列で返す
	 * @return
	 */
	public String getParamString() {
		return CollectionType.TYPE_ENUM.getTypeString(getParamType());
	}

	/**
	 * @param paramName the paramName to set
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}


	

	/**
	 * @return the defineType
	 */
	public DEFINITION_ENUM getDefineType() {
		return defineType;
	}

	/**
	 * @param defineType the defineType to set
	 */
	public void setDefineType(DEFINITION_ENUM defineType) {
		this.defineType = defineType;
	}
	
	
	

	/**
	 * パラメータへの型入力を行う
	 * @param paramType the paramType to set
	 */
	public TYPE_ENUM setParamType(TYPE_ENUM checkType) {
		this.paramType = CollectionType.TYPE_ENUM.getHighType(getParamType(), checkType);
		return this.paramType;
	}
	

	/**
	 * このノード内にStringと同様の名称のパラメータがあればtrue、なければfalseを返す
	 * @param string
	 * @return
	 */
	public boolean isParamNameAlerdyUse(String string) {
		return paramName.equals(string);
	}


}
