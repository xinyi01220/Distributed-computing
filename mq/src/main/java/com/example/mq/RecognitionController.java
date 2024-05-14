package com.example.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Controller
@RequestMapping("/api")
public class RecognitionController {
    private static final Logger logger = LoggerFactory.getLogger(RecognitionController.class);
    private final RecognitionService recognitionService;


    @Autowired
    public RecognitionController(RecognitionService recognitionService) {
        this.recognitionService = recognitionService;

    }

    @PostMapping("/recognize")
    public RedirectView recognizeAnimal(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("message", "File is empty");
            return new RedirectView("/errorPage.html");
        }

        byte[] bytes = file.getBytes();
        recognitionService.saveResult("等待识别结果...");
        recognitionService.sendImageForRecognition(bytes);

        logger.info("Recognition result stored successfully");
        return new RedirectView("/resultPage.html"); // 重定向到结果显示页面
    }

    @GetMapping("/resultPage")
    @ResponseBody
    public ResponseEntity<String> getResult() {
        String result = recognitionService.getResult();
        if (result != null && !result.isEmpty()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}

