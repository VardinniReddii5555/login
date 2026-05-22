package com.emudhra.Registration.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml2.provider.service.registration.*;
import org.springframework.security.saml2.core.Saml2X509Credential;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Configuration
public class SamlSecurityConfig {

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() throws Exception {

        X509Certificate certificate = (X509Certificate)
                CertificateFactory.getInstance("X.509")
                        .generateCertificate(
                                new ClassPathResource("emudhra.crt").getInputStream()
                        );

        Saml2X509Credential verificationCredential =
                Saml2X509Credential.verification(certificate);

        RelyingPartyRegistration registration =
                RelyingPartyRegistration
                        .withRegistrationId("emudhra")
                        .entityId("http://10.80.245.48:9090/saml2/service-provider-metadata/emudhra")
                        .assertionConsumerServiceLocation(
                                "http://10.80.245.48:9090/login/saml2/sso/emudhra"
                        )
                        .assertingPartyMetadata(party -> party
                                .entityId("https://qa.securepass.me/SPAuthPage/saml/metadata")
                                .singleSignOnServiceLocation(
                                        "http://10.80.241.21:8088/SPAuthPage/saml2/sso/1"
                                )
                                .wantAuthnRequestsSigned(false)
                                .verificationX509Credentials(c -> c.add(verificationCredential))
                        )
                        .build();

        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }
}