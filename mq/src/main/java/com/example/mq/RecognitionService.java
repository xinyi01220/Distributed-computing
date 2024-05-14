package com.example.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

@Service
public class RecognitionService {

    @Autowired
    private JmsTemplate jmsTemplate;
    private String lastResult = ""; // 存储最后一次的识别结果
    public void sendImageForRecognition(byte[] imageBytes) {

        jmsTemplate.convertAndSend("animal.recognition.queue", imageBytes);
    }


    public void saveResult(String result) {
        lastResult = result;
    }

    public String getResult() {
        return lastResult;
    }
    public String recognizeImage(byte[] imageBytes) {
        String imgStr = Base64.getEncoder().encodeToString(imageBytes);
        String imgParam;
        try {
            imgParam = URLEncoder.encode(imgStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String param = "image=" + imgParam;
        String accessToken = "24.b41f5dc2ed888a043fbd21976f5faf8e.2592000.1718189709.282335-68460354"; // Use the actual access token

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(param, headers);

        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/animal?access_token=" + accessToken;
        ResponseEntity<String> response = new RestTemplate().postForEntity(url, entity, String.class);
        System.out.println(response);
        return response.getBody();
    }
}
