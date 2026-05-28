package com.emudhra.Registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SAMLMetadataController {

    @Autowired
    private RelyingPartyRegistrationRepository repository;

    @GetMapping(value = "/saml2/service-provider-metadata/emudhra", produces = "application/xml")
    @ResponseBody
    public String metadata() {

        return """
                <EntityDescriptor entityID="http://10.80.245.48:9090/saml2/service-provider-metadata/emudhra"
                    xmlns="urn:oasis:names:tc:SAML:2.0:metadata">

                    <SPSSODescriptor
                        AuthnRequestsSigned="false"
                        WantAssertionsSigned="true"
                        protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">

                        <AssertionConsumerService
                            Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
                            Location="http://10.80.245.48:9090/login/saml2/sso/emudhra"
                            index="1"/>

                    </SPSSODescriptor>

                </EntityDescriptor>
                """;
    }
}