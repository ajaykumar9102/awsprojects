public class PidXmlParser {

    public static CaptureResponse parse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            Element resp = (Element) doc.getElementsByTagName("Resp").item(0);
            Element device = (Element) doc.getElementsByTagName("DeviceInfo").item(0);
            Element skey = (Element) doc.getElementsByTagName("Skey").item(0);
            Element hmac = (Element) doc.getElementsByTagName("Hmac").item(0);
            Element data = (Element) doc.getElementsByTagName("Data").item(0);

            CaptureResponse cr = new CaptureResponse();

            // Resp
            cr.setErrCode(resp.getAttribute("errCode"));
            cr.setErrInfo(resp.getAttribute("errInfo"));
            cr.setFCount(resp.getAttribute("fCount"));
            cr.setFType(resp.getAttribute("fType"));
            cr.setNmPoints(resp.getAttribute("nmPoints"));
            cr.setQScore(resp.getAttribute("qScore"));

            // Device
            cr.setDpID(device.getAttribute("dpId"));
            cr.setRdsID(device.getAttribute("rdsId"));
            cr.setRdsVer(device.getAttribute("rdsVer"));
            cr.setDc(device.getAttribute("dc"));
            cr.setMi(device.getAttribute("mi"));
            cr.setMc(device.getAttribute("mc"));

            // Skey
            cr.setCi(skey.getAttribute("ci"));
            cr.setSessionKey(skey.getTextContent());

            // HMAC
            cr.setHmac(hmac.getTextContent());

            // Data
            cr.setPidDatatype(data.getAttribute("type"));
            cr.setPiddata(data.getTextContent());

            return cr;

        } catch (Exception e) {
            throw new RuntimeException("PID XML parse failed", e);
        }
    }
}