package com.example.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class RecognitionService {

    @Autowired
    private JmsTemplate jmsTemplate;

    private String lastResult = ""; // 存储最后一次的识别结果
    public <T>void sendImageForRecognition(T data,String type) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> messageContent = new HashMap<>();
        messageContent.put("type", type);

        // 检查数据类型并进行相应的处理
        if (data instanceof byte[]) {
            // 处理字节数据
            String imageBase64 = Base64.getEncoder().encodeToString((byte[]) data);
            messageContent.put("data", imageBase64);
        } else {
            // 直接使用其他数据类型
            messageContent.put("data", data);
        }

        // 转换为JSON并发送
        try {
            String jsonMessage = mapper.writeValueAsString(messageContent);
            // 发送JSON消息到指定的队列
            jmsTemplate.convertAndSend("animal.recognition.queue", jsonMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }


    }


    public void saveResult(String result) {
        lastResult = result;
    }

    public String getResult() {
        return lastResult;
    }
    public String recognizeAnimal(byte[] imageBytes) {
        String imgStr = Base64.getEncoder().encodeToString(imageBytes);
        String imgParam;
        try {
            imgParam = URLEncoder.encode(imgStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String param = "image=" + imgParam;
        String accessToken = "24.b41f5dc2ed888a043fbd21976f5faf8e.2592000.1718189709.282335-68460354";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(param, headers);

        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/animal?access_token=" + accessToken;
        ResponseEntity<String> response = new RestTemplate().postForEntity(url, entity, String.class);
        System.out.println(response);
        return response.getBody();
    }
    public String recognizeWord(byte[] imageBytes) {
        String imgStr = Base64.getEncoder().encodeToString(imageBytes);
        String imgParam;
        try {
            imgParam = URLEncoder.encode(imgStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String param = "image=" + imgParam;
        String accessToken = "24.841276b0ab3cc60029bc270d352ec16c.2592000.1718326759.282335-70816099";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(param, headers);

        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token=" + accessToken;
        ResponseEntity<String> response = new RestTemplate().postForEntity(url, entity, String.class);
        System.out.println(response);
        return response.getBody();
    }


    @Autowired
    private RestTemplate restTemplate;

    public String writePoem(String title) {

        String accessToken = "24.841276b0ab3cc60029bc270d352ec16c.2592000.1718326759.282335-70816099"; // 确保这个方法能有效获取到access token
        String url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/poem?access_token=" + accessToken;

        // 构建请求体和请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String requestBody = "{\"text\":\"" + title + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // 发送POST请求

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // 输出响应
        System.out.println(response.getBody());
        return response.getBody();
    }

    public String recognizeFace(byte[] imageBytes) {
        String imgStr = Base64.getEncoder().encodeToString(imageBytes);
        String imgParam;
        try {
            imgParam = URLEncoder.encode(imgStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String param = "image=" + imgParam + "&image_type=BASE64"; // 添加 image_type 参数
        String accessToken = "24.4bc5ae1662299ee69485fde583252a58.2592000.1718372923.282335-71021403";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(param, headers);

        String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token=" + accessToken;
        ResponseEntity<String> response = new RestTemplate().postForEntity(url, entity, String.class);
        System.out.println(response);
        return response.getBody();
    }

}
