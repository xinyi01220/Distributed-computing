package com.example.mq;

import com.example.mq.RecognitionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Component
public class RecognitionListener {

    @Autowired
    private RecognitionService recognitionService;

    @JmsListener(destination = "animal.recognition.queue")
    public void processMessage(String jsonMessage) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 解析 JSON 消息
            Map<String, Object> messageMap = mapper.readValue(jsonMessage, Map.class);
            String type = (String) messageMap.get("type");
            Object data = messageMap.get("data");

            // 根据类型调用不同的识别服务
            String result = handleDataByType(type, data);

            // 保存识别结果
            recognitionService.saveResult(result);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON message", e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing data", e);
        }
    }

    private String handleDataByType(String type, Object data) {
        switch (type) {
            case "animal":
                // 假设动物识别数据是Base64编码的图像
                byte[] imageBytes = Base64.getDecoder().decode((String) data);
                return recognitionService.recognizeAnimal(imageBytes);
            case "word":
                // 假设文字识别数据也是Base64编码的图像
                byte[] wordBytes = Base64.getDecoder().decode((String) data);
                return recognitionService.recognizeWord(wordBytes);
            case "poem":
                // 假设诗歌识别数据是直接的字符串
                return recognitionService.writePoem((String) data);
            case "face":
                // 假设人脸识别数据是Base64编码的图像
                byte[] faceBytes = Base64.getDecoder().decode((String) data);
                return recognitionService.recognizeFace(faceBytes);
            default:
                return "Unsupported type";
        }
    }
}
