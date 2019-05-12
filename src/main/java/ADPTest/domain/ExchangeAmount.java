package ADPTest.domain;

import java.util.Arrays;

/*
 * Object to represent the input to the machine.
 */
public class ExchangeAmount {

    private int [] numBills = {0,0,0,0,0,0,0};
    private static final int [] billVals = {1, 2, 5, 10, 20, 50, 100};

    public void setNumBills(int [] numBills){
        this.numBills = numBills;
    }

    //Check to see if all elements of numBills are positive
    public boolean checkNumBills(int [] numBills) {
        //JAVA8 Feature
        return numBills != null && numBills.length == billVals.length && Arrays.stream(numBills).allMatch(i -> i >= 0);
    }

    public int getTotal() {
        int total = 0;
        for(int i = 0; i < billVals.length; i++){
            total += numBills[i]*billVals[i];
        }
        return total;
    }

    public int[] getNumBills() {
        return numBills;
    }
}
