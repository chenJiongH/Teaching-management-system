package per.cjh.example.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/13 15:58
 */
@Slf4j
public class SendMessage {
    public static void send(String phone, String message) {
        // 连接阿里云
        // regionId 区域编号-中国杭州 不用改动
        final String regionId = "cn-hangzhou";
        // 使用自己的阿里云用户账号和密码
        String accessKeyId = "LTAI4EsNZ";
        String accessSecret = "ckphpBnPSa2MD";
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName","课程预习系统");
        // 使用自己的 TemplateCode
        request.putQueryParameter("TemplateCode", "SMS_18635");
        //用户的验证码（密码）内容
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + message + "\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info(response.getData() + " " + phone + " " + message);
        } catch (ServerException e) {
        } catch (ClientException e) {
        }
    }
}
