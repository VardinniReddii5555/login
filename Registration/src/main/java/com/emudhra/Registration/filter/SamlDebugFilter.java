package com.emudhra.Registration.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class SamlDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (request.getRequestURI().contains("/login/saml2/sso/emudhra")
                || request.getRequestURI().contains("/login/saml2/sso/keycloak")) {

            String samlResponse = request.getParameter("SAMLResponse");

            if (samlResponse != null) {

                try {
                    String xml = new String(
                            Base64.getDecoder().decode(samlResponse),
                            StandardCharsets.UTF_8);

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(
                            "{http://xml.apache.org/xslt}indent-amount",
                            "4");
                    StringWriter writer = new StringWriter();
                    transformer.transform(
                            new DOMSource(document),
                            new StreamResult(writer));
                    System.out.println("\n=========== SAML RESPONSE ===========\n");
                    System.out.println(writer);
                    System.out.println("\n==============================================\n");

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

        }
        filterChain.doFilter(request, response);
    }
}