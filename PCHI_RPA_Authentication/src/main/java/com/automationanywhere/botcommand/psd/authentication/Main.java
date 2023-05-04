package com.automationanywhere.botcommand.psd.authentication;

public class Main {

    public static void main(String[] args) {
        PCHI_RPA_Authentication pcR = new PCHI_RPA_Authentication();

        System.out.println(pcR.action("username","password"));
    }
}
