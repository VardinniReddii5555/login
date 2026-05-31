package com.emudhra.Registration.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.*;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Configuration
public class SamlSecurityConfig {

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository()
            throws Exception {
//          Load certificate
        InputStream certStream = new ClassPathResource("saml-cert.pem").getInputStream();
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) factory.generateCertificate(certStream);
        Saml2X509Credential verificationCredential = new Saml2X509Credential(
                        certificate, Saml2X509Credential.Saml2X509CredentialType.VERIFICATION);
//         KEYCLOAK SAML
        RelyingPartyRegistration keycloakRegistration =
                RelyingPartyRegistration
                        .withRegistrationId("keycloak")
                        .entityId("employee_portal")
                        .assertionConsumerServiceLocation("http://localhost:9090/login/saml2/sso/keycloak")
                        .assertingPartyMetadata(party -> party
                                .entityId("http://localhost:9091/realms/EmployeePortal")
                                .singleSignOnServiceLocation("http://localhost:9091/realms/EmployeePortal/protocol/saml")
                                .wantAuthnRequestsSigned(false)
                                .verificationX509Credentials(c -> c.add(verificationCredential)))
                        .build();
//        EMUDHRA SAML
        RelyingPartyRegistration emudhraRegistration =
                RelyingPartyRegistration
                        .withRegistrationId("emudhra")
                        .entityId("http://localhost:9090/saml2/service-provider-metadata/emudhra")
                        .assertionConsumerServiceLocation("http://localhost:9090/login/saml2/sso/emudhra")
                        .assertingPartyMetadata(party -> party
                                .entityId("https://qa.securepass.me")
                                .singleSignOnServiceLocation("https://qa.securepass.me/SPAuthPage/ssoEntryAuth")
                                .wantAuthnRequestsSigned(false)
                                .verificationX509Credentials(c -> c.add(verificationCredential)))
                        .build();

        return new InMemoryRelyingPartyRegistrationRepository(keycloakRegistration, emudhraRegistration);
    }
    @PostConstruct
    public void test() {
        System.out.println("Registration loaded");
    }
}
