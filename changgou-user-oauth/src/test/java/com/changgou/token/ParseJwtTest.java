package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: www.itheima
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MjAxMDczMDQ0OSwiYXV0aG9yaXRpZXMiOlsic2Vja2lsbF9saXN0IiwiZ29vZHNfbGlzdCJdLCJqdGkiOiI0ZDg1NTAwYS0zYTU3LTQwODEtYjRkNC1jYWMxOWFlOGQ2OTQiLCJjbGllbnRfaWQiOiJjaGFuZ2dvdSIsInVzZXJuYW1lIjoiemhhbmdzYW4ifQ.p9kqf4uGk-KBQ5NtfbMquYKbXapmGEjNVkBQVuShyP4rupfErLfFx9nypChL7GDy0Sor8Uw2SbmtKQwL1QXAqdCaU-kTo26FMJoG9gsubq9p4MeID0vSbt6Gy4b1wDhOnzXlaoZbs40HG3IQ5Ape27XQPQTnpBTV-nBo3Dfmy-MBpOJkScR1oqyY5Hlywk4K7y-Gja2V_fyn1iDIaMQIUoMJzOX26krkpW77oUOBkp32UiqdhSwM5OeMtp_ULR7Ws5aJYmp6mY1FJdQ-5OU-SQbTRkbM_hd2I-gJWMPJyxi72vuw4GQa2LQO-3OJY32NihZI0gHDbUQunXCp0HPm9Q";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuaY6VM+qZi2Q9J6dBZCE9EQhPuQQlA5TL7ZPk2d+C4Dhc3bfUsxpk7AOIsAjxocwBRdnIQGIEioi14bThhXEyKHChLUvTSHtw324nRiLY8GXVocONyyeixArujbaNl0L63yScFwY97yf+ViBnIUiJ1aUgaEcwJAtM0GRy/mDADs49QiIrKGFCiFq98FO19eAOslsDcUJZk6jcbWRafCas4kbtt7m1ZfIN1oWQh0BSN4W9akiAwVYILpa31vCh8uAVVdT2NP/MzAbsMpFcAmZUSaiXDm4aJ/UDAJyVd/4kRosB79njASOxyUYoNxdR6LyzSJDeTcTq/NweIsjOLlnEQIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
