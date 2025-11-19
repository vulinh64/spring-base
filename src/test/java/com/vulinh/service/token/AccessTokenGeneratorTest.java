package com.vulinh.service.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import module java.base;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.configuration.data.ApplicationProperties.SecurityProperties;
import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.dto.carrier.RefreshTokenCarrier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccessTokenGeneratorTest {

  @Mock ApplicationProperties securityConfigProperties;
  @Mock RefreshTokenGenerator refreshTokenGenerator;

  @InjectMocks AccessTokenGenerator accessTokenGenerator;

  @Test
  @Disabled
  void testGenerateAccessToken() {
    when(refreshTokenGenerator.generateRefreshToken(any(), any()))
        .thenReturn(
            RefreshTokenCarrier.builder()
                .refreshToken("refreshToken")
                .expirationDate(Instant.EPOCH)
                .build());

    when(securityConfigProperties.security())
        .thenReturn(
            SecurityProperties.builder()
                .privateKey(
                    """
                    -----BEGIN RSA PRIVATE KEY-----
                    MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDUdgQ4wThZR0qC1aNFoRX9dl68m91szEDJmEDMqIkTz42lo5LFWCQKarrTYbOVhWADHfSi4EFmiBRCZUjYynuNLQqHIZFWDLNEkarnN0rMlnYzkDCqVbpRRM34YfYgQCQjqNJlqs7KXc0zbB3d2U7/SLKxizej5ztCJqGn9vQrsxGk0KUFX0xxjZe8xd0t/292ok1JmSAwsdOQ9o5hoANY9z/cCmSnHhP/kfS4Dii7MJRaz7qeyr0679QdpOqcJrALLoTs3FJOKlVmEJ1DeTIgtukWBGt9xPkziR0SNbv7Csr8AwLu4GHOhSx8hCd9cDKuxLn1CowjnYTdCJXf7PDjAgMBAAECggEAPKEJB2EsQV30x21L0Hztl40F7/DSuU94VY8bPswBgiPCmjgZlDNY5ZgbhGLnKo4LHhiYTTqNr0K59VCN/z+ZDmqCDJnprZKmPbUL/jtrHwL47DIDkTgxmSt3U6Aw6ncjWQG+OMELjfhCrJ/3ze1Le9I1HDFMSXudD32SuCni9+z2oTaJ8s1WFtheCHKLSXlfNqnax8R8hQR4lBVKpWVDrisj+eXrSoeS6XitQTj9ICANjVeGwK1Wfum1Jt0lVMAtaV9A5co01wYw/T3L8FUdIQ1jU6ySZFuzkrgg4/N/VwL3ggOjQFF/IRQwA+WJs45tn/4mxlXJLF5Gb8novO++EQKBgQD1CQoq8pTFYEbRIjJNK2F43gzbbMap2KzSWKkZ4CGO9E0YsWZ9apMZeSaBJlPHKt67Pi62R/biy1m+lYiuS1tbncN4mBSZgsgF++zaO1prNRxHA2wKclraSIC+fV/nelN5MTrF4eNftek+BTtsQo1E/v9qnj0isY6qguhIjYDslQKBgQDd99PLHhuF8UtO7cs5Hb0GxnPccgkTMSiLmhEUUmg8xXwod2f1k2XA/gHLbDDx7vjuzXDOIWMLhNrvjWPSuZJiSUWQ0gOBCd7711W1UG9or/Fphm16kwmlYw5z0xyw2e7HFoub6Wppypg/f/774QG9D0qRxpnzSQ4hT8znvh2RlwKBgQCVHT0syZazTlWKKy9FOuMENMzKMzXqYks5bm7pqjWB0zWfk0V1iQefdtRxv6s4BuSoOb0ffEfH2Evy6PjWaFFePXGYz6Opj6a9zYNjgr8Rgq6EoJZ8/P5A2+JNCer06MInfEfx5/cAZalc7r4ssYtas3snnMhDdp4FMci9bi9IyQKBgAwFKK3+MmVdfMOIcxHjv2HHi2yrrDwi1FxC+pvMHqLz2tZiKPoOglsiJjy63ier1kUwUOSIwFFWX3jLglVeAURbTW4bQV9ShoXC0nxgH7helsctJW6W2dXf+F9jVlFpa9nSKbtGt6GE/BusNcW0GKEBW/tq8tlO4noBVUpTbEx/AoGBAIKSPtwjERXeeyskScv8QkulX7SB19QW9PKEpEn1YjOHETuj22Vb//XAj7eYvNsxkMngTbU+vmE8vdzZpjjyT70HVCrZ1cvqZDj6BzERoqnlqdmAbkLTsI/XgZGH5vBpYOJfNpH5ZMZqSHW3mn5U2SPQcrHm8EsHOyRZDrTAmWr5
                    -----END RSA PRIVATE KEY-----
                    """)
                .publicKey(
                    """
                    -----BEGIN PUBLIC KEY-----
                    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1HYEOME4WUdKgtWjRaEV/XZevJvdbMxAyZhAzKiJE8+NpaOSxVgkCmq602GzlYVgAx30ouBBZogUQmVI2Mp7jS0KhyGRVgyzRJGq5zdKzJZ2M5AwqlW6UUTN+GH2IEAkI6jSZarOyl3NM2wd3dlO/0iysYs3o+c7Qiahp/b0K7MRpNClBV9McY2XvMXdLf9vdqJNSZkgMLHTkPaOYaADWPc/3Apkpx4T/5H0uA4ouzCUWs+6nsq9Ou/UHaTqnCawCy6E7NxSTipVZhCdQ3kyILbpFgRrfcT5M4kdEjW7+wrK/AMC7uBhzoUsfIQnfXAyrsS59QqMI52E3QiV3+zw4wIDAQAB
                    -----END PUBLIC KEY-----
                    """)
                .jwtDuration(Duration.ofHours(1L))
                .build());

    var actualToken =
        accessTokenGenerator.generateAccessToken(
            CommonConstant.NIL_UUID, CommonConstant.NIL_UUID, Instant.EPOCH);

    assertEquals(
        "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOm51bGwsImV4cCI6MzYwMCwidXNlcklkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAwIiwic2Vzc2lvbklkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAwIiwidG9rZW5UeXBlIjoiQUNDRVNTX1RPS0VOIiwiaWF0IjowfQ.iYK0CL37jIJUzJu1hR921y1ZxyvoROlxN2a9umUfBhf7kED9JmmjvpByzjMPyaEh7pSDq_Vcvh7GdEcnqZCbEdDHS8zvHD5ihjQdx1NMNy7wrAeHR7ynRU8RujwuyazgSq9rg3HsATp9kFj6px6kJtCjPdHz7EJEt6Q7eOyakGdKxTZiG3u-4wzP7Ni-RAK5FS-MUCeevBES8s9Sd72y0EIAU3_nbjfyqnWxu6okEYAlA8UutU3AW_EBxGSw1jUKE0plJJtGWsER7Hwr5Vhz_U8JiLdt32bP48g2--IrgBEDECPY_rKjM_jrAZxi7TUo82s41vky-ZiQsSM7zBB4rQ",
        actualToken.tokenResponse().accessToken());
  }
}
