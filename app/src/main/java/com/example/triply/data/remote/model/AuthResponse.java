package com.example.triply.data.remote.model;

public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private CustomerInfo customerInfo;

    private static class CustomerInfo {
        private int customerId;
        private String fullName;
        private String email;
        private String phone;
        private String address;
        private String userName;
        private String socialProvider;

        public String getUserName() {
            return userName;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public String getUserName() {
        return customerInfo != null ? customerInfo.getUserName() : null;
    }

    public String getFullName() {
        return customerInfo != null ? customerInfo.getFullName() : null;
    }

    public String getEmail() {
        return customerInfo != null ? customerInfo.getEmail() : null;
    }
}


