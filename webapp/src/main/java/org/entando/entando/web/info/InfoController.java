package org.entando.entando.web.info;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/info")
public class InfoController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> info() {
        log.debug("retrieving info check");

        try {
            Map<String, Object> res = InfoLoader.getInfo();
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch(Exception ex) {
            log.warn("error retrieving info check",ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}