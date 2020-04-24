package common.xiao.util;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
 
/**
 * 坐标转换为WKT
 * @author luhaiyou
 * @sate 2019-02-21 10:06
 * @version 1.0
 *
 */
public class CoordinateUtil {
 
  public static String convert2Wkt(String type, String coordinates) {
 
    //"Point", "MultiPoint", "LineString", "MultiLineString", "Polygon", "MultiPolygon"
    StringBuffer wkt = new StringBuffer();
 
    JSONArray jsonArray = JSONObject.parseArray(coordinates);
 
    type = type.toUpperCase();
    switch (type) {
      case "POINT":
        //[100.0, 0.0]
        wkt.append("POINT(").append(jsonArray.getString(0)).append(" ")
            .append(jsonArray.getString(1)).append(")");
        break;
      case "MULTIPOINT":
        //[
        //        [100.0, 0.0],
        //        [101.0, 1.0]
        //    ]
        wkt.append("MULTIPOINT(");
        for (int i = 0; i < jsonArray.size(); i++) {
          JSONArray point = jsonArray.getJSONArray(i);
          wkt.append(point.getString(0)).append(" ").append(point.get(1)).append(",");
        }
        wkt.deleteCharAt(wkt.length() - 1);
        wkt.append(")");
        break;
      case "LINESTRING":
        //和multipoint结构一样
        //[
        //                [101.0, 0.0],
        //                [102.0, 1.0]
        //            ]
        wkt.append("LINESTRING").append(wktLineString(jsonArray));
        break;
      case "MULTILINESTRING":
        //[
        //        [
        //            [100.0, 0.0],
        //            [101.0, 1.0]
        //        ],
        //        [
        //            [102.0, 2.0],
        //            [103.0, 3.0]
        //        ]
        //    ]
        wkt.append("MULTILINESTRING(");
        for (int i = 0; i < jsonArray.size(); i++) {
          wkt.append("").append(wktLineString(jsonArray.getJSONArray(i))).append("").append(",");
        }
        wkt.deleteCharAt(wkt.length() - 1);
        wkt.append(")");
        break;
      case "POLYGON":
        //POLYGON((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2))
        //"coordinates": [
        //            [
        //                [-180.0, 10.0],
        //                [20.0, 90.0],
        //                [180.0, -5.0],
        //                [-30.0, -90.0]
        //            ]
        //        ]
        wkt.append("POLYGON").append(wktPolygon(jsonArray));
        break;
      case "MULTIPOLYGON":
        //解析为多个polygon
        wkt.append("MULTIPOLYGON(");
        for (int i = 0; i < jsonArray.size(); i++) {
          wkt.append(wktPolygon(jsonArray.getJSONArray(i))).append(",");
        }
        wkt.deleteCharAt(wkt.length() - 1);
        wkt.append(")");
        break;
    }
 
    return wkt.toString();
  }
 
  private static String wktLineString(JSONArray jsonArray) {
 
    StringBuffer wkt = new StringBuffer();
 
    wkt.append("(");
    for (int i = 0; i < jsonArray.size(); i++) {
      JSONArray point = jsonArray.getJSONArray(i);
      wkt.append(point.getString(0)).append(" ").append(point.get(1)).append(",");
    }
    wkt.deleteCharAt(wkt.length() - 1);
    wkt.append(")");
 
    return wkt.toString();
  }
 
  private static String wktPolygon(JSONArray jsonArray) {
 
    StringBuffer wkt = new StringBuffer();
 
    wkt.append("(");
    for (int i = 0; i < jsonArray.size(); i++) {
      //是一组组的LineString
      wkt.append("(");
      JSONArray lineString = jsonArray.getJSONArray(i);
      for (int j = 0; j < lineString.size(); j++) {
        JSONArray pointArray = lineString.getJSONArray(j);
        wkt.append(pointArray.getString(0)).append(" ").append(pointArray.getString(1))
            .append(",");
      }
      if (wkt.length() > 0) {
        //删除最后一个逗号
        wkt.deleteCharAt(wkt.length() - 1);
      }
      wkt.append("),");
    }
    if (wkt.length() > 0) {
      wkt.deleteCharAt(wkt.length() - 1);
    }
    wkt.append(")");
 
    return wkt.toString();
  }
 
  public static void main(String[] args) {
 
		String str = "[[\r\n" + "          [\r\n" + "            [\r\n" + "              112.928466796875,\r\n"
				+ "              24.856534339310674\r\n" + "            ],\r\n" + "            [\r\n"
				+ "              114.158935546875,\r\n" + "              24.856534339310674\r\n" + "            ],\r\n"
				+ "            [\r\n" + "              114.158935546875,\r\n" + "              25.730632525531913\r\n"
				+ "            ],\r\n" + "            [\r\n" + "              112.928466796875,\r\n"
				+ "              25.730632525531913\r\n" + "            ],\r\n" + "            [\r\n"
				+ "              112.928466796875,\r\n" + "              24.856534339310674\r\n" + "            ]\r\n"
				+ "          ]\r\n" + "        ],\r\n" + "        [\r\n" + "          [\r\n" + "            [\r\n"
				+ "              111.4013671875,\r\n" + "              22.933100746980394\r\n" + "            ],\r\n"
				+ "            [\r\n" + "              112.8460693359375,\r\n" + "              22.933100746980394\r\n"
				+ "            ],\r\n" + "            [\r\n" + "              112.8460693359375,\r\n"
				+ "              24.14174098050432\r\n" + "            ],\r\n" + "            [\r\n"
				+ "              111.4013671875,\r\n" + "              24.14174098050432\r\n" + "            ],\r\n"
				+ "            [\r\n" + "              111.4013671875,\r\n" + "              22.933100746980394\r\n"
				+ "            ]\r\n" + "          ]\r\n" + "        ]]";

 
		String wkt = convert2Wkt("MULTIPOLYGON", str);
		System.out.println(wkt);
 
  }
 
}