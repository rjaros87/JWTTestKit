package io.github.rjaros87;

import io.micronaut.http.annotation.*;

@Controller("/JWTTestKit")
public class JWTTestKitController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}