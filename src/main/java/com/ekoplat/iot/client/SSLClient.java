package com.ekoplat.iot.client;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 16:18 2019-07-26
 **/

import com.ekoplat.iot.server.ServerChannelInitializer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class SSLClient {

    public static void main(String args[]) throws Exception {


        // 客户端信任改证书，将用于校验服务器传过来的证书的合法性
        String certPath = ServerChannelInitializer.class.getClassLoader().getResource("ssl/cert.crt").getPath();
        InputStream inStream = null;
        Certificate certificate = null;
        try {
            inStream = new FileInputStream(certPath);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            certificate = cf.generateCertificate(inStream);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("cert", certificate);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("sunx509");
        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        SSLSocketFactory socketFactory = sslContext.getSocketFactory();

        Socket socket = null;
        OutputStream out = null;

        try {

            socket = socketFactory.createSocket("localhost", 10101);
            out = socket.getOutputStream();

            // 请求服务器
            String lines = "test success";
            byte[] outputBytes = lines.getBytes("UTF-8");
            out.write(outputBytes);
            out.flush();

        } finally {
            // 关闭连接
            out.close();
            socket.close();
        }

    }
}