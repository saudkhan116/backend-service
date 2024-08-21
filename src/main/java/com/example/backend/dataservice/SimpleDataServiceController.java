/********************************************************************************
 * Copyright (c) 2022,2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2021,2024 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package com.example.backend.dataservice;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping
@Slf4j
public class SimpleDataServiceController {
    private final HashMap<String, Object> data = new HashMap<>();

    @PostMapping("/{id}")
    public void addData(@PathVariable final String id, @RequestBody final Object payload) {
        log.info("Adding data for id '{}'", id);
        data.put(id, payload);
    }

    @GetMapping({"/{id}", "/{id}/$value", "/api/{id}", "/api/{id}/$value"})
    public Object getData(@PathVariable final String id) {
        if (data.containsKey(id)) {
            log.info("Returning data for id '{}'", id);
            return data.get(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data found with id '%s'".formatted(id));
        }
    }

    @PostMapping("/api/{id}")
    public void addXmlData(@PathVariable final String id, @RequestBody final String payload) {
        log.info("Adding data for id '{}'", id);
        data.put(id, payload);
    }

    @GetMapping({"/api/hash/{id}"})
    public Object getHash(@PathVariable final String id) {
        String hashId = id + "_hash";
        if (data.containsKey(hashId)) {
            log.info("Returning hash for id '{}'", hashId);
            return data.get(hashId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data found with id '%s'".formatted(id));
        }
    }

    @PostMapping("/api/hash/{id}")
    public void addHash(@PathVariable final String id, @RequestBody final String payload) {
        String hashId = id + "_hash";
        log.info("Adding hash for id '{}'", hashId);
        data.put(hashId, payload);
    }

    @PostMapping(value = "/api/file/{id}")
    public Object writeDataToFile(@PathVariable final String id, @RequestBody final String payload) {

        Path dataDir = Paths.get("data");
        Path path = Paths.get("data/" + id);
        HashMap<String, Object> responseBody = new HashMap<>();

        try {
           if (Files.notExists(dataDir))
               Files.createDirectories(dataDir);
           Files.write(path, payload.getBytes(StandardCharsets.UTF_8));
           log.info("Adding data to a file for id '{}'", id);
            responseBody.put("message", "Data saved successfully..!!");
            responseBody.put("status", 200);
           return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
            responseBody.put("message", "Internal Server Error" + e.getMessage());
            responseBody.put("status", 500);
            return responseBody;
        }
    }

    @GetMapping({"/api/file/{id}", "/api/file/{id}/$value"})
    public Object getDataFromFile(@PathVariable final String id) {

        String path = "data";
        Path  dataDir = Paths.get("data");
        Path filePath = Paths.get("data/" + id);
        HashMap<String, Object> responseBody = new HashMap<>();
        try {
            if (Files.notExists(filePath))
                throw new RuntimeException("Data not found");
            Object fileContent = Files.readString(filePath);
            log.info("Returning data from a file for id '{}'", id);
            // Return the content as the response
            return ResponseEntity.ok(fileContent);

        }
        catch (IOException e) {
            e.printStackTrace();
            // Handle the exception and return an error response if the file is not found or cannot be read
            responseBody.put("message", "Internal Server Error" + e.getMessage());
            responseBody.put("status", 500);
            return responseBody;
        } catch (RuntimeException ex) {
            responseBody.put("message", "Not Found" + ex.getMessage());
            responseBody.put("status", 500);
            return responseBody;
        }
    }
}
