package Controller;

import Fetchers.StockRangeTester;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BackTestController {

    StockRangeTester stockRangeTester = new StockRangeTester();
    boolean activeTest = false;
    @PostMapping("/")
    public void startBacktest(){
        if(activeTest == false){
            // set up input for StockRangeTester();
            activeTest = true;
        }
    }
}
