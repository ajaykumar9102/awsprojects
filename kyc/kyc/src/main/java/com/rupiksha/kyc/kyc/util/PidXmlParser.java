package com.rupiksha.kyc.kyc.util;

import com.rupiksha.kyc.kyc.dto.CaptureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class PidXmlParser {

    private static final Logger log = LoggerFactory.getLogger(PidXmlParser.class);

    public static CaptureResponse parse(String xml) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // 🔐 सुरक्षा (XXE attack protection)
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            Document doc = factory
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            doc.getDocumentElement().normalize();

            CaptureResponse cr = new CaptureResponse();

            // =========================
            // 🔹 Resp
            // =========================
            Element resp = getElement(doc, "Resp");

            cr.setErrCode(getAttr(resp, "errCode"));
            cr.setErrInfo(getAttr(resp, "errInfo"));

            cr.setFCount(getAttr(resp, "fCount"));
            cr.setFType(getAttr(resp, "fType"));

            // 🔥 FIX: blank aa raha tha → default "0"
            cr.setICount(defaultZero(getAttr(resp, "iCount")));
            cr.setIType(getAttr(resp, "iType"));

            cr.setPCount(getAttr(resp, "pCount"));
            cr.setPType(getAttr(resp, "pType"));

            cr.setNmPoints(getAttr(resp, "nmPoints"));
            cr.setQScore(getAttr(resp, "qScore"));

            // =========================
            // 🔹 DeviceInfo
            // =========================
            Element device = getElement(doc, "DeviceInfo");

            cr.setDpID(getAttr(device, "dpId"));
            cr.setRdsID(getAttr(device, "rdsId"));
            cr.setRdsVer(getAttr(device, "rdsVer"));
            cr.setDc(getAttr(device, "dc"));
            cr.setMi(getAttr(device, "mi"));
            cr.setMc(getAttr(device, "mc"));

            // =========================
            // 🔹 Skey
            // =========================
            Element skey = getElement(doc, "Skey");

            cr.setSessionKey(getText(skey));
            cr.setCi(getAttr(skey, "ci"));

            // =========================
            // 🔹 Hmac
            // =========================
            Element hmac = getElement(doc, "Hmac");
            cr.setHmac(getText(hmac));

            // =========================
            // 🔹 Data
            // =========================
            Element data = getElement(doc, "Data");

            cr.setPidDatatype(getAttr(data, "type"));
            cr.setPiddata(getText(data));

            // =========================
            // 🔍 DEBUG LOG (VERY IMPORTANT)
            // =========================
            log.info("📡 PID PARSED:");
            log.info("fType: {}", cr.getFType());
            log.info("iCount: {}", cr.getICount());
            log.info("qScore: {}", cr.getQScore());
            log.info("HMAC Present: {}", cr.getHmac() != null);

            return cr;

        } catch (Exception e) {
            log.error("❌ PID XML Parsing Failed", e);
            throw new RuntimeException("PID XML parsing failed", e);
        }
    }

    // =========================
    // 🔧 HELPER METHODS
    // =========================

    private static Element getElement(Document doc, String tag) {
        Node node = doc.getElementsByTagName(tag).item(0);
        return node != null ? (Element) node : null;
    }

    private static String getAttr(Element element, String attr) {
        if (element == null) return "";
        String val = element.getAttribute(attr);
        return val != null ? val : "";
    }

    private static String getText(Element element) {
        if (element == null) return "";
        return element.getTextContent();
    }

    // 🔥 blank → "0"
    private static String defaultZero(String val) {
        return (val == null || val.isBlank()) ? "0" : val;
    }
}