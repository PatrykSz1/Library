package com.test.testtechnicalsystem.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMQService {

    private final Lock fileLock = new ReentrantLock();

    @RabbitListener(queues = "log_queue")
    public void receiveMessage(String message) {
        String logDirectoryPath = "logs";
        File logDirectory = new File(logDirectoryPath);
        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
        }

        String fileName = "monitoring_log_" + System.currentTimeMillis() + ".json";
        String filePath = logDirectoryPath + File.separator + fileName;

        try {
            fileLock.lock();
            saveToFile(filePath, message);
        } catch (IOException e) {
            log.error("Error creating log file: " + e.getMessage());
        } finally {
            fileLock.unlock();
        }

        log.info(message);
    }

    public void saveToFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath), true))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            log.error("Error writing to JSON file: " + e.getMessage());
            throw e;
        }
    }
}


