package com.automationanywhere.botcommand.psd.validatesignature;

public class Main {
    public static void main(String[] args) {
        PdfSignatureChecker pdf = new PdfSignatureChecker();
        String filepath = "C:\\Users\\syed.hasnain\\Downloads\\PdfSamples\\Working\\19146378HDFC_ERGO_General_Insurance_2315205292910100000.PDF";
        System.out.println(pdf.action(filepath));
    }


}
