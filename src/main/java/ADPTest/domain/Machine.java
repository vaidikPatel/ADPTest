package ADPTest.domain;

import ADPTest.Output;

import java.util.Arrays;

/*
 * Object Representation of the machine. Contains logic for exchanging bills
 */
public class Machine {

    private static int [] numCoins = {100, 100, 100, 100};
    private static final double [] coinVals = {0.01, 0.05, 0.1, 0.25};

    /* Given a value x, the method outputs the equivalent amount in coins. Also outputs result of the transaction.
     * Input: double x, the value we wish to convert to coins
     */
    public Output exchange (double x){
        Output output = new Output();
        if (x > getTotal()) {
            output.setResult(StringConstants.notEnoughCoins);
        } else {
            int i = coinVals.length - 1;
            while (x != 0){
                if(numCoins[i] == 0) {
                    i--;
                    continue;
                }
                int coinsUsed = Math.min((int) (x/coinVals[i]), numCoins[i]);
                numCoins[i] -= coinsUsed;
                if(i == coinVals.length - 1)
                    output.setQuarter(coinsUsed);
                else if(i == coinVals.length - 2)
                    output.setDime(coinsUsed);
                else if(i == coinVals.length - 3)
                    output.setNickel(coinsUsed);
                else if(i == coinVals.length - 4)
                    output.setPennies(coinsUsed);
                x -= coinsUsed*coinVals[i];
                i--;
            }
            output.setResult(StringConstants.successfulExchange);
        }
        if (getTotal() == 0)
            output.setResult(output.getResult() + " " + StringConstants.outOfCoins);
        return output;
    }

    //Returns the dollar value of all the coins in the machine
    public double getTotal() {
        double total = 0;
        for(int i = 0; i < coinVals.length; i++){
            total += numCoins[i]*coinVals[i];
        }
        return total;
    }

    public static void setNumCoins(int[] numCoins) {
        Machine.numCoins = numCoins;
    }

    //Check to see if all elements of numBills are positive
    public boolean checkNumCoins(int [] numCoins) {
        //JAVA8 Feature
        return numCoins != null && numCoins.length == coinVals.length && Arrays.stream(numCoins).allMatch(i -> i >= 0);
    }
}
