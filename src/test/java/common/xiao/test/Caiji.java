package common.xiao.test;

import common.xiao.util.HttpClientUtil;

public class Caiji {
	public static void main(String[] args) {

		for (int i = 0; i <= 9; i++) {
			String doGet = HttpClientUtil.doGet(
					"http://10.148.10.210:7003/collect?servic=customCollect&action=getQPE&dateTime=202001070" + i
							+ "000000");
			System.out.println(i + "---" + doGet);
		}

	}
}
