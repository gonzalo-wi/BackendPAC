package com.el_jumillano.pac.integrations.minibank;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Parsea el XML que devuelve Minibank en una lista de MinibankDepositXml.
 * Aísla el dominio de cualquier detalle del formato XML de Minibank.
 */
@Component
public class MinibankXmlParser {

    public List<MinibankDepositXml> parse(String rawXml) {
        List<MinibankDepositXml> result = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Deshabilitar XXE
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(
                    new ByteArrayInputStream(rawXml.getBytes(StandardCharsets.UTF_8)));

            NodeList deposits = doc.getElementsByTagName("WSDepositsByDayDTO");
            for (int i = 0; i < deposits.getLength(); i++) {
                var node = deposits.item(i);
                MinibankDepositXml dto = new MinibankDepositXml();
                dto.setDepositId(getChildText(node, "depositId"));

                // dateTime: "2026-05-28T18:47:01"
                String dateTime = getChildText(node, "dateTime");
                if (dateTime != null && dateTime.contains("T")) {
                    dto.setDate(dateTime.substring(0, 10));
                    dto.setTime(dateTime.substring(11));
                }

                dto.setRouteNumber(parseRouteNumber(getChildText(node, "userName")));
                dto.setAmount(getFirstCurrencyAmount(node));
                dto.setRawXml(rawXml);
                result.add(dto);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al parsear XML de Minibank: " + e.getMessage(), e);
        }
        return result;
    }

    private String getChildText(org.w3c.dom.Node parent, String tagName) {
        var children = ((org.w3c.dom.Element) parent).getElementsByTagName(tagName);
        if (children.getLength() == 0) return null;
        return children.item(0).getTextContent();
    }

    /**
     * Extrae el número de reparto del campo userName de PIMS.
     * Formatos conocidos:
     *   "56, RTO 056"   → primer token numérico → "56"
     *   "RTO 263, 263"  → primer token numérico → "263"
     */
    private String parseRouteNumber(String userName) {
        if (userName == null || userName.isBlank()) return null;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+").matcher(userName);
        return m.find() ? m.group() : null;
    }

    /**
     * Obtiene el totalAmount del primer WSDepositCurrency dentro de currencies.
     */
    private String getFirstCurrencyAmount(org.w3c.dom.Node parent) {
        var amounts = ((org.w3c.dom.Element) parent).getElementsByTagName("totalAmount");
        if (amounts.getLength() == 0) return null;
        return amounts.item(0).getTextContent();
    }
}
