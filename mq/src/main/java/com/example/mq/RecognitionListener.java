package com.example.mq;

import com.example.mq.RecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class RecognitionListener {

    @Autowired
    private RecognitionService recognitionService;

    @JmsListener(destination = "animal.recognition.queue")
    public void processImage(byte[] imageBytes) {
        String result = recognitionService.recognizeImage(imageBytes);
        recognitionService.saveResult(result);
    }
}
