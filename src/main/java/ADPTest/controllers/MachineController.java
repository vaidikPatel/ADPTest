package ADPTest.controllers;

import ADPTest.Output;
import ADPTest.domain.ExchangeAmount;
import ADPTest.domain.Machine;
import ADPTest.domain.StringConstants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MachineController {

    private Machine machine = new Machine();

    /* Exchanges bills into coins
     * Input: ExchangeAmount object with numBills defined in form [x,x,x,x,x,x,x] where x is the number of 1,2,5,10,20,50,100 bills
     */
    @PostMapping("/exchange")
    Output exchange(@RequestBody ExchangeAmount exchangeAmount) {
        if (exchangeAmount.getNumBills() != null && exchangeAmount.checkNumBills(exchangeAmount.getNumBills()))
            return machine.exchange(exchangeAmount.getTotal());
        else {
            Output output = new Output();
            output.setResult(StringConstants.invalidBills);
            return output;
        }
    }

    /* Resets the coins inside the machine. If no input then defaults to 100 coins of each type
     * Input: integer array num coins of the form [x,x,x,x] where x is the number of 1,5,10,25 cent coins.
     */
    @PostMapping("/reset")
    String reset(@RequestBody(required = false) int [] numCoins) {

        if (numCoins == null){
            machine.setNumCoins(new int[]{100, 100, 100, 100});
        } else if (machine.checkNumCoins(numCoins)) {
            machine.setNumCoins(numCoins);
        } else {
            return StringConstants.failedReset;
        }
        return StringConstants.successfulReset;
    }
}