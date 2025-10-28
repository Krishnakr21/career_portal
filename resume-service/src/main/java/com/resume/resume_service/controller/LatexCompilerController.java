package com.resume.resume_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/latex")
public class LatexCompilerController {

    private static final Logger logger = LoggerFactory.getLogger(LatexCompilerController.class);

    @PostMapping("/compile")
    public ResponseEntity<byte[]> compileLatex(@RequestBody Map<String, String> payload) {
        String code = payload.getOrDefault("code", "");

        String tmpId = UUID.randomUUID().toString();
        Path texPath = Path.of(System.getProperty("java.io.tmpdir"), tmpId + ".tex");
        Path pdfPath = Path.of(System.getProperty("java.io.tmpdir"), tmpId + ".pdf");

        try {
            Files.writeString(texPath, code);

            ProcessBuilder pb = new ProcessBuilder(
                    "pdflatex",
                    "-output-directory", System.getProperty("java.io.tmpdir"),
                    texPath.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(logger::info);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("Compilation failed with exit code: {}", exitCode);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Compilation error".getBytes());
            }

            byte[] pdfBytes = Files.readAllBytes(pdfPath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", tmpId + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IOException | InterruptedException e) {
            logger.error("Error during LaTeX compilation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Server error: " + e.getMessage()).getBytes());
        } finally {
            try {
                Files.deleteIfExists(texPath);
                Files.deleteIfExists(pdfPath);
            } catch (IOException ignored) {
                logger.warn("Error cleaning up temporary files");
            }
        }
    }
}