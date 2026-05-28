package com.emudhra.Registration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.metadata.Saml2MetadataResolver;
import org.springframework.security.saml2.provider.service.registration.*;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Configuration
public class SamlSecurityConfig {

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() throws Exception {
        System.out.println("CUSTOM SAML REGISTRATION LOADED");

// eMudhra Configuration
        X509Certificate certificate = (X509Certificate)
                CertificateFactory
                        .getInstance("X.509")
                        .generateCertificate(new ClassPathResource("emudhra.crt").getInputStream());


        Saml2X509Credential verificationCredential = Saml2X509Credential.verification(certificate);

        RelyingPartyRegistration emudhraRegistration =
                RelyingPartyRegistration
                        .withRegistrationId("emudhra")
                        .entityId("http://10.80.245.48:9090/saml2/service-provider-metadata/emudhra")
                        .assertionConsumerServiceLocation("http://10.80.245.48:9090/login/saml2/sso/emudhra")
                        .assertingPartyMetadata(party -> party
                        .entityId("https://qa.securepass.me/SPAuthPage/saml/metadata")
                        .singleSignOnServiceLocation("http://10.80.240.246:8088/SPAuthPage/saml2/sso/1"))
                        .decryptionX509Credentials(credentials -> credentials.add(verificationCredential))
                        .authnRequestsSigned(false)
                        .build();

// Keycloak Configuration
        ClassPathResource resource =
                new ClassPathResource("employee-portal.jks");

        KeyStore keyStore = KeyStore.getInstance("JKS");

        keyStore.load(
                resource.getInputStream(),
                "changeit".toCharArray()
        );

        X509Certificate keystore_certificate =
                (X509Certificate) keyStore.getCertificate("employee-portal");

        PrivateKey privateKey =
                (PrivateKey) keyStore.getKey(
                        "employee-portal",
                        "changeit".toCharArray()
                );

        Saml2X509Credential signingCredential =
                Saml2X509Credential.signing(
                        privateKey,
                        keystore_certificate
                );

        RelyingPartyRegistration keycloakRegistration =

                RelyingPartyRegistration
                        .withRegistrationId("employee-portal-2")

                        .entityId(
                                "http://localhost:9090/saml2/service-provider-metadata/employee-portal-2"
                        )

                        .assertionConsumerServiceLocation(
                                "http://localhost:9090/login/saml2/sso/employee-portal-2"
                        )

                        .assertingPartyMetadata(details -> details

                                .entityId(
                                        "http://localhost:9091/realms/EmployeePortal"
                                )

                                .singleSignOnServiceLocation(
                                        "http://localhost:9091/realms/EmployeePortal/protocol/saml"
                                )

                                .singleSignOnServiceBinding(
                                        Saml2MessageBinding.REDIRECT
                                )
                        )

                        .signingX509Credentials(credentials ->
                                credentials.add(signingCredential)
                        )

                        .build();

        // RETURN BOTH
        return new InMemoryRelyingPartyRegistrationRepository(
                emudhraRegistration,
                keycloakRegistration
        );
    }
}