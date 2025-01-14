package Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class APIController {

    @GetMapping("/")
    public String getFractalRanges() {
        System.out.println("test");
        return "test";
    }


}