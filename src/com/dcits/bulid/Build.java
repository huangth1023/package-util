package com.dcits.bulid;

import com.dcits.bean.TranFieldBean;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: huangth tel:13246649002
 * Date: 2020/9/2 17:44
 * @Version:
 * @Description:
 */
public class Build {

    private static Logger log = Logger.getLogger(Build.class);

    public void build(String path) {

        try {

            System.out.println("========================当前正在解析的文件为：" + path);
            log.info("========================当前正在解析的文件为：" + path);
            Workbook wb = new XSSFWorkbook(new FileInputStream(path));
            Sheet index = wb.getSheet("INDEX");
            int lastRow = index.getLastRowNum();

            for (int i = 1; i <= lastRow; i++) {   //去掉第一行

                //请求
                List<TranFieldBean> reqList = new ArrayList<>();
                //响应
                List<TranFieldBean> respList = new ArrayList<>();

                Row indexRow = index.getRow(i);
                //获取第一列的接口ID
                String tranId = getCellValue(indexRow.getCell(0));

                //根据接口id查找到对应sheet页
                Sheet sheet = wb.getSheet(tranId);

                int last = sheet.getLastRowNum();
                int num = 0;//“输出”所在行标

                System.out.println("========================正在读取第" + i + "个数据，当前对应sheet页为：" + tranId + "===========================");
                System.out.println("总行数为：" + last );

                log.info("========================正在读取第" + i + "个数据，当前对应sheet页为：" + tranId + "===========================");

                //遍历sheet页
                for (int j = 8; j <= last; j++) {//第九行开始读请求字段
                    try {
                        String out = getCellValue(sheet.getRow(j).getCell(0));
                        if ("输出".equals(out)) {
                            num = j;
                            break;
                        }
                        setBean(reqList, sheet, j);
                    } catch (Exception e) {
                        log.error(tranId + "对应的sheet页字段名为空，请修改！");
                        new Exception(tranId + "对应的sheet页字段名为空，请修改！").printStackTrace();
                    }
                }

                for (int j = num+1; j <= last; j++) {
                    setBean(respList, sheet, j);
                }

                //生成服务定义文件
                serviceIdentify(tranId , reqList, respList);

                //c端拆包
                consumerUnpacked(tranId , reqList);

                //p端组包
                providerPackage(tranId , reqList);

                //p端拆包
                providerUnpacked(tranId , respList);

                //c端组包
                consumerPackage(tranId, respList);

                //元数据
                metadata(reqList, respList);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //元数据
    private void metadata(List<TranFieldBean> reqList, List<TranFieldBean> respList) {

        String path = System.getProperty("user.dir") + "/file/metadata.xml";

        try {

            SAXReader reader = new SAXReader();
            Document doc = reader.read(path);

            //遍历
            setBean(reqList, doc);
            setBean(respList, doc);

            writeXML(path, doc);

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private void setBean(List<TranFieldBean> respList, Document doc) {
        for (TranFieldBean bean: respList) {
            if (bean != null) {
                String name = bean.getRtFieldNm();
                //读取原有的元数据
                Element root = doc.getRootElement();

                String len = bean.getRtFieldLen() + "";
                if ("array".equalsIgnoreCase(bean.getRtFieldType())) {
                    len = "";
                }

                if (root.element(name) == null) {
                    root.addElement(name).addAttribute("metadataid", bean.getRtFieldNm()).addAttribute("chinese_name", bean.getRtFieldZhNm()).addAttribute("type", bean.getRtFieldType()).addAttribute("length", len).addAttribute("isPin", "false");
                }
            }
        }
    }

    //c端组包
    private void consumerPackage(String tranId, List<TranFieldBean> respList) {

        Document doc = DocumentHelper.createDocument();
        Element json = doc.addElement("Json").addAttribute("package_type", "json");

        //head
        Element head = json.addElement("head").addAttribute("is_struct", "true");
        head.addElement("code").addAttribute("metadataid", "code").addAttribute("chinese_name", "响应码");
        head.addElement("msg").addAttribute("metadataid", "msg").addAttribute("chinese_name", "响应信息");

        //result
        Element result = json.addElement("result").addAttribute("is_struct", "true");
        for (TranFieldBean bean: respList) {
            if (bean != null) {
                result.addElement(bean.getRtFieldNm()).addAttribute("metadataid", bean.getRtFieldNm()).addAttribute("chinese_name", bean.getRtFieldZhNm());
            }
        }

        String path = System.getProperty("user.dir") + "/config/cons_" + tranId + "V1_to_JSON.xml";
//        String path = System.getProperty("user.dir") + "/config/" + tranId + "/cons_" + tranId + "V1_to_JSON.xml";
        writeXML(path, doc);

    }

    //p端拆包
    private void providerUnpacked(String tranId, List<TranFieldBean> respList) {

        Document doc = DocumentHelper.createDocument();
        Element json = doc.addElement("Json").addAttribute("package_type", "json");

        //SYS_HEAD
        Element sys_head = json.addElement("SYS_HEAD");
        Element ret = sys_head.addElement("RET");
        ret.addElement("RET_CODE").addAttribute("metadataid", "code").addAttribute("chinese_name", "交易信息码");
        ret.addElement("RET_MSG").addAttribute("metadataid", "msg").addAttribute("chinese_name", "交易返回信息");

        //BODY
        Element body = json.addElement("BODY");
        for (TranFieldBean bean: respList) {
            if (bean != null) {
                body.addElement(bean.getLfFieldNm()).addAttribute("metadataid", bean.getRtFieldNm()).addAttribute("chinese_name", bean.getRtFieldZhNm());
            }
        }

        String path = System.getProperty("user.dir") + "/config/prvd_" + tranId + "V1_from_JSON.xml";
//        String path = System.getProperty("user.dir") + "/config/" + tranId + "/prvd_" + tranId + "V1_from_JSON.xml";
        writeXML(path, doc);

    }

    //p端组包
    private void providerPackage(String tranId, List<TranFieldBean> reqList) {

        Document doc = DocumentHelper.createDocument();
        Element json = doc.addElement("Json").addAttribute("package_type", "json");

        //SYS_HEAD
        Element sys_head = json.addElement("SYS_HEAD").addAttribute("is_struct", "true");
        sys_head.addElement("SERVICE_CODE").addAttribute("metadataid", "service_code").addAttribute("chinese_name", "服务代码").addAttribute("expression", "");
        sys_head.addElement("MESSAGE_TYPE").addAttribute("metadataid", "message_type").addAttribute("chinese_name", "报文类型").addAttribute("expression", "");
        sys_head.addElement("MESSAGE_CODE").addAttribute("metadataid", "message_code").addAttribute("chinese_name", "报文代码").addAttribute("expression", "");
        sys_head.addElement("TRAN_DATE").addAttribute("metadataid", "tran_date").addAttribute("chinese_name", "交易日期").addAttribute("expression", "ff:substring(${/sdoroot/syshead/timestamp},0,8)");
        sys_head.addElement("TRAN_TIMESTAMP").addAttribute("metadataid", "tran_timestamp").addAttribute("chinese_name", "交易时间").addAttribute("expression", "ff:substring(${/sdoroot/syshead/timestamp},8,17)");
        sys_head.addElement("SOURCE_TYPE").addAttribute("metadataid", "source_type").addAttribute("chinese_name", "渠道号").addAttribute("expression", "OPEN");
        sys_head.addElement("TRAN_MODE").addAttribute("metadataid", "tran_mode").addAttribute("chinese_name", "交易模式").addAttribute("expression", "ONLINE");
        sys_head.addElement("USER_LANG").addAttribute("metadataid", "user_lang").addAttribute("chinese_name", "操作员语言").addAttribute("expression", "CHINESE");
        sys_head.addElement("PLATFORM_ID").addAttribute("metadataid", "platform_id").addAttribute("chinese_name", "平台号").addAttribute("expression", "ff:get('platform_id',${/sdoroot/syshead/platform_id})");
        sys_head.addElement("PLATFORM_USER_ID").addAttribute("metadataid", "platform_user_id").addAttribute("chinese_name", "平台用户id").addAttribute("expression", "");
        sys_head.addElement("SEQ_NO").addAttribute("metadataid", "seq_no").addAttribute("chinese_name", "渠道流水号").addAttribute("expression", "");
        sys_head.addElement("USER_ID").addAttribute("metadataid", "user_id").addAttribute("chinese_name", "银行用户号").addAttribute("expression", "");
        sys_head.addElement("BRANCH_ID").addAttribute("metadataid", "branch_id").addAttribute("chinese_name", "交易机构").addAttribute("expression", "");
        sys_head.addElement("BANK_USER_ID").addAttribute("metadataid", "bank_user_id").addAttribute("chinese_name", "行内用户号").addAttribute("expression", "");
        sys_head.addElement("COMPANY").addAttribute("metadataid", "company").addAttribute("chinese_name", "法人").addAttribute("expression", "BOJJ");
        sys_head.addElement("PROGRAM_ID").addAttribute("metadataid", "program_id").addAttribute("chinese_name", "交易屏幕标识").addAttribute("expression", "");
        sys_head.addElement("GLOBAL_SEQ_NO").addAttribute("metadataid", "global_seq_no").addAttribute("chinese_name", "全局流水号").addAttribute("expression", "ff:getSeqNo()");

        //BODY
        Element body = json.addElement("BODY").addAttribute("is_struct", "true");
        for (TranFieldBean bean: reqList) {
            if (bean != null) {
                body.addElement(bean.getLfFieldNm()).addAttribute("metadataid", bean.getRtFieldNm()).addAttribute("chinese_name", bean.getRtFieldZhNm());
            }
        }

        String path = System.getProperty("user.dir") + "/config/prvd_" + tranId + "V1_to_JSON.xml";
//        String path = System.getProperty("user.dir") + "/config/" + tranId + "/prvd_" + tranId + "V1_to_JSON.xml";
        writeXML(path, doc);

    }

    //c端拆包
    private void consumerUnpacked(String tranId, List<TranFieldBean> reqList) {

        Document doc = DocumentHelper.createDocument();
        Element json = doc.addElement("Json").addAttribute("package_type", "json");

        //head
        Element head = json.addElement("head");
        setHead(head);

        //request
        Element request = json.addElement("request");
        for (TranFieldBean bean: reqList) {
            if (bean != null) {
                request.addElement(bean.getRtFieldNm()).addAttribute("metadataid", bean.getRtFieldNm()).addAttribute("chinese_name", bean.getRtFieldZhNm());

            }
        }

        String path = System.getProperty("user.dir") + "/config/cons_" + tranId + "V1_from_JSON.xml";
//        String path = System.getProperty("user.dir") + "/config/" + tranId + "/cons_" + tranId + "V1_from_JSON.xml";
        writeXML(path, doc);

    }

    /*private void dealArray(Element element, ArrayField arrayField) {

        List<TranFieldBean> list = arrayField.getList();
        for (TranFieldBean bean: list) {
            boolean arrayField1 = bean.isArrayField();
            if (arrayField1) {
                String rtFieldNm = bean.getRtFieldNm();
                element = element.addElement(rtFieldNm);
                element.addAttribute("metadataid", bean.getRtFieldNm()).addAttribute("chinese_name", bean.getRtFieldZhNm());
                dealArray(element, arrayField);
            }
        }
    }*/

    private void setHead(Element head) {
        head.addElement("appid").addAttribute("metadataid", "appid").addAttribute("chinese_name", "应用id");
        head.addElement("token").addAttribute("metadataid", "token").addAttribute("chinese_name", "应用授权令牌");
        head.addElement("version").addAttribute("metadataid", "version").addAttribute("chinese_name", "api版本号");
        head.addElement("timestamp").addAttribute("metadataid", "timestamp").addAttribute("chinese_name", "请求时间");
        head.addElement("ip_addr").addAttribute("metadataid", "ip_addr").addAttribute("chinese_name", "客户端ip地址");
        head.addElement("platform_user_id").addAttribute("metadataid", "platform_user_id").addAttribute("chinese_name", "平台用户ID");
        head.addElement("seq_no").addAttribute("metadataid", "seq_no").addAttribute("chinese_name", "系统流水号");
    }

    //生成服务定义文件
    private void serviceIdentify(String tranId, List<TranFieldBean> reqList, List<TranFieldBean> respList) {

        Document doc = DocumentHelper.createDocument();
        Element service = doc.addElement("service");

        //请求
        Element reqroot = service.addElement("request").addElement("sdoroot");
        setHead(reqroot.addElement("SysHead"));

        Element reqBody = reqroot.addElement("Body");
        for (TranFieldBean bean: reqList) {
            if (bean != null) {
                reqBody.addElement(bean.getRtFieldNm()).addAttribute("metadataid", bean.getRtFieldNm()).addAttribute("chinese_name", bean.getRtFieldZhNm());
            }
        }

        //响应
        Element resproot = service.addElement("response").addElement("sdoroot");
        Element respSysHead = resproot.addElement("SysHead");
        respSysHead.addElement("code").addAttribute("metadataid", "code").addAttribute("chinese_name", "返回码");
        respSysHead.addElement("msg").addAttribute("metadataid", "msg").addAttribute("chinese_name", "返回信息");

        Element respBody = resproot.addElement("Body");
        for (TranFieldBean bean: respList) {
            if (bean != null) {
                respBody.addElement(bean.getRtFieldNm()).addAttribute("metadataid", bean.getRtFieldNm()).addAttribute("chinese_name", bean.getRtFieldZhNm());
            }
        }

        String path = System.getProperty("user.dir") + File.separator + "config" + /*File.separator + tranId +*/ File.separator + "service_" + tranId + "V1.xml";
        writeXML(path, doc);

    }

    private void writeXML(String path, Document doc) {

        try {
            OutputFormat format = OutputFormat.createPrettyPrint();

            XMLWriter writer = new XMLWriter(new FileOutputStream(path), format);
            writer.write(doc);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getCellValue(Cell cell) {
        return cell.getStringCellValue().trim();
    }

    //转化为bean对象
    private void setBean(List<TranFieldBean> list, Sheet sheet, int j) {

        int i = j + 1;

        try {

            TranFieldBean bean = new TranFieldBean();
            Row row = sheet.getRow(j);

            //左边字段
            bean.setLfFieldNm(getCellValue(row.getCell(0)));
            bean.setLfFieldZhNm(getCellValue(row.getCell(1)));

            //右边字段
            bean.setRtFieldNm(getCellValue(row.getCell(7)));
            bean.setRtFieldZhNm(getCellValue(row.getCell(8)));

            //处理数据类型和长度(仅支持String类型、Arrary类型和Double类型)
            String value = getCellValue(row.getCell(9));
            String[] split = value.replace("(", "-").replace(")", "-").replace("（", "-").
                    replace("）", "-").replace(",", "-").replace("，", "-").split("-");
            if (split.length == 1 && "Array".equalsIgnoreCase(split[0])) {//Array
                bean.setRtFieldType("Array");
            } else if (split.length == 2 && "String".equalsIgnoreCase(split[0])) {//String
                bean.setRtFieldType("String");
                bean.setRtFieldLen(Integer.parseInt(split[1]));
            } else if (split.length == 3 && "Double".equalsIgnoreCase(split[0])) {//Double类型转为String，长度为
                bean.setRtFieldType("String");
                bean.setRtFieldLen(Integer.parseInt(split[1]) + Integer.parseInt(split[2]));
            } else {
                log.error(sheet.getSheetName() + "页的第" + i + "行数据类型错误，请修正！");
                new Exception(sheet.getSheetName() + "页的第" + i + "行数据类型错误，请修正！").printStackTrace();
            }

            list.add(bean);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            log.error(sheet.getSheetName() + "页的第" + i + "行有空数据，请修正！");
            new Exception(sheet.getSheetName() + "页的第" + i + "行有空数据，请修正！").printStackTrace();
        }

    }

}
