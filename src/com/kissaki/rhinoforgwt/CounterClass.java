package com.kissaki.rhinoforgwt;

/**
 * 単なるカウントアップ用のクラス
 * @author sassembla
 *
 */
public class CounterClass {
	int count = 0;
	
	/**
	 * コンストラクタ
	 */
	public CounterClass () {
		
	}
	
	/**
	 * カウントを返す
	 * @return
	 */
	public int addCount () {
		return count++;
	}
	
}
