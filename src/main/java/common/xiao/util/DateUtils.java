package common.xiao.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	/**
	 *      * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致      *       * @param
	 * nowTime 当前时间      * @param startTime 开始时间      * @param endTime 结束时间    
	 *  * @return      
	 */
	private static boolean belongCalendar(Date nowTime, Date startTime, Date endTime) {
		if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
			return true;
		}
		Calendar date = Calendar.getInstance();
		date.setTime(nowTime);

		Calendar begin = Calendar.getInstance();
		begin.setTime(startTime);

		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
		if (date.after(begin) && date.before(end)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 判断时间是否在[startTime, endTime]区间，注意时间格式要一致
	 * 
	 * @param nowTime
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean belongCalendar(String nowTime, String startTime, String endTime, String dateFormat) {
		DateFormat df = new SimpleDateFormat(dateFormat);
		Date nowDate = null;
		Date startDate = null;
		Date endDate = null;
		try {
			nowDate = df.parse(nowTime);
			startDate = df.parse(startTime);
			endDate = df.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (nowDate.getTime() == startDate.getTime() || nowDate.getTime() == endDate.getTime()) {
			return true;
		}

		Calendar date = Calendar.getInstance();
		date.setTime(nowDate);

		Calendar begin = Calendar.getInstance();
		begin.setTime(startDate);

		Calendar end = Calendar.getInstance();
		end.setTime(endDate);

		if (date.after(begin) && date.before(end)) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		String types = "Drought,hh,fz,gw,hl,dwyy,dch,lzs,hlf,sjf";
		Calendar cal = Calendar.getInstance();
		cal.setTime(cal.getTime());
		cal.add(2, -3);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		// 低温阴雨 2.1-3.20
		if ((month != 2 && month != 3) || (month == 3 && day > 20)) {
			types = types.replace(",dwyy", "");
		}
		// 倒春寒 3.21-4.30
		if ((month != 3 && month != 4) || (month == 3 && day < 21)) {
			types = types.replace(",dch", "");
		}
		// 龙舟水 5.21-6.20
		if ((month != 5 && month != 6) || (month == 5 && day < 21) || (month == 6 && day > 20)) {
			types = types.replace(",lzs", "");
		}
		// 寒露风 9.20-10.20
		if ((month != 9 && month != 10) || (month == 9 && day < 20) || (month == 10 && day > 20)) {
			types = types.replace(",hlf", "");
		}
		// 霜降风 10.21-11.20
		if ((month != 10 && month != 11) || (month == 10 && day < 21) || (month == 11 && day > 20)) {
			types = types.replace(",sjf", "");
		}
		System.out.println(cal.before(Calendar.getInstance()));
		System.out.println(types);
	}
}
