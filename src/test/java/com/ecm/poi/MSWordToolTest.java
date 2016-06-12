package com.ecm.poi;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-6-1
 * Time: 上午10:56
 * To change this template use File | Settings | File Templates.
 */
public class MSWordToolTest {
    @Test
    public void bookMarksTest() {
        long startTime = System.currentTimeMillis();
        MSWordTool changer = new MSWordTool();
        changer.setTemplate("d:\\temp\\Word模版.docx");
        Map<String,String> content = new HashMap<String,String>();
        content.put("Principles", "格式规范、标准统一、利于阅览");
        content.put("Purpose", "规范会议操作、提高会议质量");
        content.put("Scope", "公司会议、部门之间业务协调会议");

        content.put("customerName", "**有限公司");
        content.put("address", "机场路2号");
        content.put("userNo", "3021170207");
        content.put("tradeName", "水泥制造");
        content.put("price1", "1.085");
        content.put("price2", "0.906");
        content.put("price3", "0.433");
        content.put("numPrice", "0.675");

        content.put("company_name", "**有限公司");
        content.put("company_address", "机场路2号");
        changer.replaceBookMark(content);


        //替换表格标签
        List<Map<String ,String>> content2 = new ArrayList<Map<String, String>>();
        Map<String, String> table1 = new HashMap<String, String>();

        table1.put("MONTH", "*月份");
        table1.put("SALE_DEP", "75分");
        table1.put("TECH_CENTER", "80分");
        table1.put("CUSTOMER_SERVICE", "85分");
        table1.put("HUMAN_RESOURCES", "90分");
        table1.put("FINANCIAL", "95分");
        table1.put("WORKSHOP", "80分");
        table1.put("TOTAL", "85分");

        for(int i = 0; i < 3; i++){
            content2.add(table1);
        }
        changer.fillTableAtBookMark("Table" ,content2);
        changer.fillTableAtBookMark("month", content2);

        //表格中文本的替换
        Map<String, String> table = new HashMap<String, String>();
        table.put("CUSTOMER_NAME", "**有限公司");
        table.put("ADDRESS", "机场路2号");
        table.put("USER_NO", "3021170207");
        table.put("tradeName", "水泥制造");
        table.put("PRICE_1", "1.085");
        table.put("PRICE_2", "0.906");
        table.put("PRICE_3", "0.433");
        table.put("NUM_PRICE", "0.675");
        changer.replaceText(table,"Table2");

        //保存替换后的WORD
        changer.saveAs("D:\\temp\\msWordTest1.docx");
        System.out.println("time==" + (System.currentTimeMillis() - startTime));

    }

}
