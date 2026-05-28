package com.emudhra.Registration.constants;

public final class LoginModes {

    public static final String MANUAL = "MANUAL";
    public static final String FIREBASE_SSO = "FIREBASE_SSO";
    public static final String GOOGLE_SSO = "GOOGLE_SSO";
    public static final String GITHUB_SSO = "GITHUB_SSO";
    public static final String OIDC_SSO = "OIDC_SSO";
    public static final String SAML_SSO = "SAML_SSO";
    public static final String KEYCLOCK_OIDC = "KEYCLOCK_OIDC";
    public static final String KEYCLOCK_SAML = "KEYCLOCK_SAML";

    public static final String DEFAULT = "DEFAULT";
    private LoginModes() {
    }

}