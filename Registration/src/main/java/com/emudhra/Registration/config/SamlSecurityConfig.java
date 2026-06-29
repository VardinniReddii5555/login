package com.emudhra.Registration.config;

import com.emudhra.Registration.repository.SamlFailureHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.registration.*;
import org.springframework.security.saml2.provider.service.web.HttpSessionSaml2AuthenticationRequestRepository;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestRepository;

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
                        .assertionConsumerServiceLocation("{baseUrl}/login/saml2/sso/keycloak")
                        .singleLogoutServiceLocation("{baseUrl}/logout/saml2/slo")
                        .assertingPartyMetadata(party -> party
                                .entityId("http://10.80.241.113:9091/realms/EmployeePortal")
                                .singleSignOnServiceLocation("http://10.80.241.113:9091/realms/EmployeePortal/protocol/saml")
                                .wantAuthnRequestsSigned(false)
                                .singleLogoutServiceLocation("{baseUrl}/logout/saml2/slo")
                                .verificationX509Credentials(c -> c.add(verificationCredential)))
                        .build();
        System.out.println("Certificate Subject: " + certificate.getSubjectX500Principal());

//        EMUDHRA SAML

        InputStream certStream1 = new ClassPathResource("securepass-cert.pem").getInputStream();
        CertificateFactory factory1 = CertificateFactory.getInstance("X.509");
        X509Certificate certificate1 = (X509Certificate) factory1.generateCertificate(certStream1);
        Saml2X509Credential verificationCredential1 = new Saml2X509Credential(
                certificate1, Saml2X509Credential.Saml2X509CredentialType.VERIFICATION);
        RelyingPartyRegistration emudhraRegistration =
                RelyingPartyRegistration
                        .withRegistrationId("emudhra")
                        .entityId("http://10.80.241.113:9090/saml2/service-provider-metadata/emudhra")
                        .assertionConsumerServiceLocation("http://10.80.241.113:9090/login/saml2/sso/emudhra")
                        .singleLogoutServiceLocation("http://10.80.241.113:9090/logout/saml2/slo")
                        .nameIdFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified")
                        .assertingPartyMetadata(party -> party
                                .entityId("https://demo.securepass.me/SPAuthPage/saml/metadata")
                                .singleSignOnServiceLocation("https://demo.securepass.me/SPAuthPage/saml2/sso/1")
                                .wantAuthnRequestsSigned(false)
                                .singleLogoutServiceLocation("http://10.80.241.113:9090/logout/saml2/slo")
                                .verificationX509Credentials(c -> c.add(verificationCredential1)))
                        .build();

        System.out.println("Certificate Subject: " + certificate1.getSubjectX500Principal());
        return new InMemoryRelyingPartyRegistrationRepository(keycloakRegistration, emudhraRegistration);
    }

    @Bean
    public Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest>
    saml2AuthenticationRequestRepository() {
        return new HttpSessionSaml2AuthenticationRequestRepository();
    }
    @Bean
    public SamlFailureHandler samlFailureHandler() {
        return new SamlFailureHandler();
    }
    @PostConstruct
    public void test() {
        System.out.println("Registration loaded");
    }

}
